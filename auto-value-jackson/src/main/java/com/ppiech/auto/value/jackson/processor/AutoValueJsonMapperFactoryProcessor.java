package com.ppiech.auto.value.jackson.processor;

import com.ppiech.auto.value.jackson.JacksonAuto;
import com.ppiech.auto.value.jackson.JsonMapperFactory;
import com.ppiech.auto.value.jackson.JsonMapper;
import com.google.auto.service.AutoService;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.AutoValueExtension;
import com.google.common.collect.ImmutableSet;
import com.ppiech.auto.value.jackson.annotation.AutoValueJsonMapperFactory;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static javax.lang.model.element.Modifier.*;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * Generates a Gson {@link AutoValueJsonMapperFactory} that adapts all {@link AutoValue} annotated
 * Gson serializable classes.
 */
@AutoService(Processor.class)
public class AutoValueJsonMapperFactoryProcessor extends AbstractProcessor {

  private final AutoValueJacksonExtension extension = new AutoValueJacksonExtension();
  private Types typeUtils;
  private Elements elementUtils;

  @Override public Set<String> getSupportedAnnotationTypes() {
    return ImmutableSet.of(AutoValue.class.getName(), AutoValueJsonMapperFactory.class.getName());
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    typeUtils = processingEnv.getTypeUtils();
    elementUtils = processingEnv.getElementUtils();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    List<Element> elements = new LinkedList<>();
    for (Element element : roundEnv.getElementsAnnotatedWith(AutoValue.class)) {
      AutoValueExtension.Context context = new LimitedContext(processingEnv, (TypeElement) element);
      if (extension.applicable(context)) {
        elements.add(element);
      }
    }

    if (!elements.isEmpty()) {
      Set<? extends Element> adaptorFactories = roundEnv.getElementsAnnotatedWith(AutoValueJsonMapperFactory.class);
      for (Element element : adaptorFactories) {
        if (!element.getModifiers().contains(ABSTRACT)) {
          error(element, "Must be abstract!");
        }
        TypeElement type = (TypeElement) element; // Safe to cast because this is only applicable on types anyway
        if (!implementsTypeAdapterFactory(type)) {
          error(element, "Must implement JsonMapperFactory!");
        }
        String adapterName = classNameOf(type);
        String packageName = packageNameOf(type);

        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "createJsonObjectMapperFactory");

        TypeSpec typeAdapterFactory = createJsonObjectMapperFactory(elements, packageName, adapterName);
        JavaFile file = JavaFile.builder(packageName, typeAdapterFactory).build();
        try {
          file.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
          processingEnv.getMessager().printMessage(ERROR, "Failed to write JsonMapperFactory: " + e.getLocalizedMessage());
        }
      }
    }

    // return false so other processors can consume the @AutoValue annotation
    return false;
  }

  private TypeSpec createJsonObjectMapperFactory(
      List<Element> elements,
      String packageName,
      String adapterName) {
    TypeSpec.Builder factory = TypeSpec.classBuilder(
        ClassName.get(packageName, "AutoValueJackson_" + adapterName));
    factory.addModifiers(PUBLIC, FINAL);
    factory.superclass(ClassName.get(packageName, adapterName));

    ParameterSpec loganSquareAuto = ParameterSpec.builder(JacksonAuto.class, "jacksonAuto").build();
    TypeVariableName t = TypeVariableName.get("T");
    ParameterSpec clazz = ParameterSpec
        .builder(ParameterizedTypeName.get(ClassName.get(Class.class), t), "clazz")
        .build();
    ParameterizedTypeName result = ParameterizedTypeName.get(ClassName.get(JsonMapper.class), t);
    MethodSpec.Builder create = MethodSpec.methodBuilder("mapperFor")
        .addModifiers(PUBLIC)
        .addTypeVariable(t)
        .addAnnotation(Override.class)
        .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
            .addMember("value", "\"unchecked\"")
            .build())
        .addParameters(ImmutableSet.of(loganSquareAuto, clazz))
        .returns(result);

    for (int i = 0, elementsSize = elements.size(); i < elementsSize; i++) {
      Element element = elements.get(i);
      TypeName elementType = rawType(element);
      if (i == 0) {
        create.beginControlFlow("if ($T.class.isAssignableFrom(clazz))", elementType);
      } else {
        create.nextControlFlow("else if ($T.class.isAssignableFrom(clazz))", elementType);
      }
      ExecutableElement jsonMapperMethod = getJsonMapperMethod(element);
      List<? extends VariableElement> params = jsonMapperMethod.getParameters();
      if (params != null && params.size() == 1) {
        create.addStatement("return (JsonMapper<$T>) $T." + jsonMapperMethod.getSimpleName() + "($N)", t, elementType, loganSquareAuto);
      } else {
        create.addStatement("return (JsonMapper<$T>) $T." + jsonMapperMethod.getSimpleName() + "($N, ($T) $N)", t, elementType, loganSquareAuto, params.get(1), clazz);
      }
    }
    create.nextControlFlow("else");
    create.addStatement("return null");
    create.endControlFlow();

    factory.addMethod(create.build());
    return factory.build();
  }

  private TypeName rawType(Element element) {
    TypeName type = TypeName.get(element.asType());
    if (type instanceof ParameterizedTypeName) {
      type = ((ParameterizedTypeName) type).rawType;
    }
    return type;
  }

  private ExecutableElement getJsonMapperMethod(Element element) {
    TypeName type = TypeName.get(element.asType());
    ParameterizedTypeName jsonMapperType = ParameterizedTypeName.get(ClassName.get(JsonMapper.class), type);
    for (ExecutableElement method : ElementFilter.methodsIn(element.getEnclosedElements())) {
      if (method.getModifiers().contains(Modifier.STATIC) && method.getModifiers().contains(Modifier.PUBLIC)) {
        TypeName returnType = TypeName.get(method.getReturnType());
        if (returnType.equals(jsonMapperType)) {
          return method;
        } else if (returnType instanceof ParameterizedTypeName) {
          ParameterizedTypeName paramReturnType = (ParameterizedTypeName) returnType;
          TypeName argument = paramReturnType.typeArguments.get(0);

          // If the original type uses generics, user's don't have to nest the generic type args
          if (type instanceof ParameterizedTypeName) {
            ParameterizedTypeName pTypeName = (ParameterizedTypeName) type;
            if (pTypeName.rawType.equals(argument)) {
              return method;
            }
          }
        }
      }
    }
    return null;
  }

  private void error(Element element, String message, Object... args) {
    if (args.length > 0) {
      message = String.format(message, args);
    }
    processingEnv.getMessager().printMessage(ERROR, message, element);
  }

  private boolean implementsTypeAdapterFactory(TypeElement type) {
    TypeMirror jsonMapperFactoryType = elementUtils.getTypeElement(JsonMapperFactory.class.getCanonicalName()).asType();
    TypeMirror typeMirror = type.asType();
    if (!type.getInterfaces().isEmpty() || typeMirror.getKind() != TypeKind.NONE) {
      while (typeMirror.getKind() != TypeKind.NONE) {
        if (searchInterfacesAncestry(typeMirror, jsonMapperFactoryType)) {
          return true;
        }
        type = (TypeElement) typeUtils.asElement(typeMirror);
        typeMirror = type.getSuperclass();
      }
    }
    return false;
  }

  private boolean searchInterfacesAncestry(TypeMirror rootIface, TypeMirror target) {
    TypeElement rootIfaceElement = (TypeElement) typeUtils.asElement(rootIface);
    // check if it implements valid interfaces
    for (TypeMirror iface : rootIfaceElement.getInterfaces()) {
      TypeElement ifaceElement = (TypeElement) typeUtils.asElement(rootIface);
      while (iface.getKind() != TypeKind.NONE) {
        if (typeUtils.isSameType(iface, target)) {
          return true;
        }
        // go up
        if (searchInterfacesAncestry(iface, target)) {
          return true;
        }
        // then move on
        iface = ifaceElement.getSuperclass();
      }
    }
    return false;
  }

  /**
   * Returns the name of the given type, including any enclosing types but not the package.
   */
  private static String classNameOf(TypeElement type) {
    String name = type.getQualifiedName().toString();
    String pkgName = packageNameOf(type);
    return pkgName.isEmpty() ? name : name.substring(pkgName.length() + 1);
  }

  /**
   * Returns the name of the package that the given type is in. If the type is in the default
   * (unnamed) package then the name is the empty string.
   */
  private static String packageNameOf(TypeElement type) {
    while (true) {
      Element enclosing = type.getEnclosingElement();
      if (enclosing instanceof PackageElement) {
        return ((PackageElement) enclosing).getQualifiedName().toString();
      }
      type = (TypeElement) enclosing;
    }
  }

  private static class LimitedContext implements AutoValueExtension.Context {
    private final ProcessingEnvironment processingEnvironment;
    private final TypeElement autoValueClass;

    public LimitedContext(ProcessingEnvironment processingEnvironment, TypeElement autoValueClass) {
      this.processingEnvironment = processingEnvironment;
      this.autoValueClass = autoValueClass;
    }

    @Override public ProcessingEnvironment processingEnvironment() {
      return processingEnvironment;
    }

    @Override public String packageName() {
      return processingEnvironment().getElementUtils()
          .getPackageOf(autoValueClass).getQualifiedName().toString();
    }

    @Override public TypeElement autoValueClass() {
      return autoValueClass;
    }

    @Override public Map<String, ExecutableElement> properties() {
      return null;
    }

    @Override public Set<ExecutableElement> abstractMethods() {
      return null;
    }
  }
}
