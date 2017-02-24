package com.ppiech.auto.value.jackson.processor;

import com.ppiech.auto.value.jackson.JacksonAuto;
import com.ppiech.auto.value.jackson.JsonMapper;
import com.ppiech.auto.value.jackson.LoganSquare;
import com.ppiech.auto.value.jackson.processor.type.TypeUtils;
import com.ppiech.auto.value.jackson.processor.type.field.TypeConverterFieldType;
import com.ppiech.auto.value.jackson.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.auto.common.MoreTypes;
import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
import com.google.common.base.CaseFormat;
import com.google.common.base.Defaults;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import com.ppiech.auto.value.jackson.processor.type.Type;
import com.ppiech.auto.value.jackson.annotation.JsonProperty;
import com.squareup.javapoet.*;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

import static javax.lang.model.element.Modifier.*;

@AutoService(AutoValueExtension.class)
public class AutoValueJacksonExtension extends AutoValueExtension {

  /**
   * Compiler flag to indicate that collections/maps should default to their empty forms. Default is to default to null.
   */
  static final String COLLECTIONS_DEFAULT_TO_EMPTY = "autovaluegson.defaultCollectionsToEmpty";

  public static class Property {
    final String methodName;
    final String humanName;
    final ExecutableElement element;
    final TypeName typeName;
    final ImmutableSet<String> annotations;
    final TypeMirror typeConverter;
    final Type type;

    public Property(String humanName, ExecutableElement element, Elements elements, Types types) {
      this.methodName = element.getSimpleName().toString();
      this.humanName = humanName;
      this.element = element;

      typeName = TypeName.get(element.getReturnType());
      annotations = buildAnnotations(element);

      typeConverter = getAnnotationProperty(element, JsonProperty.class, "typeConverter");
      type = Type.typeFor(element.getReturnType(), typeConverter, elements, types);
    }

    public static TypeMirror getAnnotationProperty(Element foo, Class<?> annotation, String key) {
      AnnotationMirror am = getAnnotationMirror(foo, annotation);
      if (am == null) {
        return null;
      }
      AnnotationValue av = getAnnotationProperty(am, key);
      return av == null ? null : (TypeMirror) av.getValue();
    }

    private static AnnotationMirror getAnnotationMirror(Element typeElement, Class<?> clazz) {
      String clazzName = clazz.getName();
      for (AnnotationMirror m : typeElement.getAnnotationMirrors()) {
        if (m.getAnnotationType().toString().equals(clazzName)) {
          return m;
        }
      }
      return null;
    }

    private static AnnotationValue getAnnotationProperty(AnnotationMirror annotationMirror, String key) {
      Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotationMirror.getElementValues();
      for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : values.entrySet()) {
        if (entry.getKey().getSimpleName().toString().equals(key)) {
          return entry.getValue();
        }
      }
      return null;
    }

    public String[] jsonNames() {
      JsonProperty jsonMethod = element.getAnnotation(JsonProperty.class);
      if (jsonMethod != null && jsonMethod.name().length != 0) {
        return jsonMethod.name();
      } else {
        return new String[]{humanName};
      }
    }

    public Boolean nullable() {
      return annotations.contains("Nullable");
    }

    private ImmutableSet<String> buildAnnotations(ExecutableElement element) {
      ImmutableSet.Builder<String> builder = ImmutableSet.builder();

      List<? extends AnnotationMirror> annotations = element.getAnnotationMirrors();
      for (AnnotationMirror annotation : annotations) {
        builder.add(annotation.getAnnotationType().asElement().getSimpleName().toString());
      }

      return builder.build();
    }
  }

  @Override
  public boolean applicable(Context context) {
    // check that the class contains a public static method returning a TypeAdapter
    TypeElement type = context.autoValueClass();
    TypeName typeName = TypeName.get(type.asType());

    // not supporting classes with generics
    Messager messager = context.processingEnvironment().getMessager();
    if (typeName instanceof ParameterizedTypeName) {
      messager.printMessage(Diagnostic.Kind.WARNING,
          String.format("Found public static method on class %s with type parameters."
              + "Skipping GsonTypeAdapter generation.", type));
      return false;
    }

    ParameterizedTypeName jsonMapperType = ParameterizedTypeName.get(
        ClassName.get(JsonMapper.class), typeName);
    TypeName returnedTypeAdapter = null;
    for (ExecutableElement method : ElementFilter.methodsIn(type.getEnclosedElements())) {
      if (method.getModifiers().contains(Modifier.STATIC)
          && method.getModifiers().contains(Modifier.PUBLIC)) {
        TypeMirror rType = method.getReturnType();
        TypeName returnType = TypeName.get(rType);
        if (returnType.equals(jsonMapperType)) {
          return true;
        }

        if (returnType.equals(jsonMapperType.rawType)
            || (returnType instanceof ParameterizedTypeName
            && ((ParameterizedTypeName) returnType).rawType.equals(jsonMapperType.rawType))) {
          returnedTypeAdapter = returnType;
        }
      }
    }

    if (returnedTypeAdapter == null) {
      return false;
    }

    // emit a warning if the user added a method returning a JsonMapper, but not of the right type
    if (returnedTypeAdapter instanceof ParameterizedTypeName) {
      ParameterizedTypeName paramReturnType = (ParameterizedTypeName) returnedTypeAdapter;
      TypeName argument = paramReturnType.typeArguments.get(0);

      // If the original type uses generics, user's don't have to nest the generic type args
      if (typeName instanceof ParameterizedTypeName) {
        ParameterizedTypeName pTypeName = (ParameterizedTypeName) typeName;
        if (pTypeName.rawType.equals(argument)) {
          return true;
        }
      } else {
        messager.printMessage(Diagnostic.Kind.WARNING,
            String.format("Found public static method returning JsonMapper<%s> on %s class. "
                + "Skipping JsonObjectMapper generation.", argument, type));
      }
    } else {
      messager.printMessage(Diagnostic.Kind.WARNING, "Found public static method returning "
          + "JsonMapper with no type arguments, skipping JsonObjectMapper generation.");
    }

    return false;
  }

  @Override
  public String generateClass(Context context, String className, String classToExtend, boolean isFinal) {
    ProcessingEnvironment env = context.processingEnvironment();
    List<Property> properties = readProperties(context.properties(), env.getElementUtils(), env.getTypeUtils());

    Map<String, TypeName> types = convertPropertiesToTypes(context.properties());

    ClassName classNameClass = ClassName.get(context.packageName(), className);
    ClassName autoValueClass = ClassName.get(context.autoValueClass());

    TypeName superclasstype = ClassName.get(context.packageName(), classToExtend);
    TypeSpec typeAdapter = createTypeAdapter(context, classNameClass, autoValueClass, properties);

    TypeSpec.Builder subclass = TypeSpec.classBuilder(classNameClass)
        .superclass(superclasstype)
        .addType(typeAdapter)
        .addMethod(generateConstructor(types));

    if (isFinal) {
      subclass.addModifiers(FINAL);
    } else {
      subclass.addModifiers(ABSTRACT);
    }

    return JavaFile.builder(context.packageName(), subclass.build()).build().toString();
  }

  public List<Property> readProperties(Map<String, ExecutableElement> properties, Elements elements, Types types) {
    List<Property> values = new LinkedList<Property>();
    for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
      values.add(new Property(entry.getKey(), entry.getValue(), elements, types));
    }
    return values;
  }

  List<FieldSpec> createTypeConverterFields(List<Property> properties) {
    // TypeConverters could be expensive to create, so just use one per class
    Set<ClassName> usedTypeConverters = new HashSet<>();
    for (Property property : properties) {
      if (property.type instanceof TypeConverterFieldType) {
        usedTypeConverters.add(((TypeConverterFieldType) property.type).getTypeConverterClassName());
      }
    }
    List<FieldSpec> fieldSpecs = new ArrayList<>(usedTypeConverters.size());
    for (ClassName typeConverter : usedTypeConverters) {
      fieldSpecs.add(FieldSpec.builder(typeConverter, TypeUtils.getStaticFinalTypeConverterVariableName(typeConverter))
          .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
          .initializer("new $T()", typeConverter)
          .build());
    }

    return fieldSpecs;
  }

  List<FieldSpec> createDynamicTypeConverterFields(Set<TypeName> usedTypeConverters) {
    List<FieldSpec> fieldSpecs = new ArrayList<>(usedTypeConverters.size());
    for (TypeName usedTypeConverter : usedTypeConverters) {
      final String variableName = TypeUtils.getTypeConverterVariableName(usedTypeConverter);
      fieldSpecs.add(
          FieldSpec
              .builder(ParameterizedTypeName.get(ClassName.get(TypeConverter.class), usedTypeConverter), variableName)
              .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
              .build()
      );
    }
    return fieldSpecs;
  }


  List<MethodSpec> createDynamicTypeConverterMethods(Set<TypeName> usedTypeConverters) {
    List<MethodSpec> methodSpecs = new ArrayList<>(usedTypeConverters.size());
    for (TypeName usedTypeConverter : usedTypeConverters) {
      final String variableName = TypeUtils.getTypeConverterVariableName(usedTypeConverter);
      methodSpecs.add(MethodSpec.methodBuilder(TypeUtils.getTypeConverterGetter(usedTypeConverter))
          .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
          .returns(ParameterizedTypeName.get(ClassName.get(TypeConverter.class), usedTypeConverter))
          .beginControlFlow("if ($L == null)", variableName)
          .addStatement("$L = $T.typeConverterFor($T.class)", variableName, LoganSquare.class, usedTypeConverter)
          .endControlFlow()
          .addStatement("return $L", variableName)
          .build()
      );
    }
    return methodSpecs;
  }

  ImmutableMap<TypeName, FieldSpec> createMapperFields(List<Property> properties) {
    ImmutableMap.Builder<TypeName, FieldSpec> fields = ImmutableMap.builder();

    Set<Type.ClassNameObjectMapper> mappers = new HashSet<>();
    for (Property property : properties) {
      mappers.addAll(property.type.getUsedJsonObjectMappers());
    }
    for (Type.ClassNameObjectMapper usedMapper : mappers) {
      fields.put(
          usedMapper.className,
          FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(JsonMapper.class), usedMapper.className),
              TypeUtils.getMapperVariableName(usedMapper.objectMapper))
              .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
              .build()
      );
    }

    return fields.build();
  }

  private List<FieldSpec> createDefaultValueFields(List<Property> properties,
      ProcessingEnvironment processingEnv) {
    List<FieldSpec> fields = new ArrayList<>(properties.size());
    boolean collectionsDefault = Boolean.parseBoolean(processingEnv.getOptions()
        .getOrDefault(COLLECTIONS_DEFAULT_TO_EMPTY, "false"));
    for (Property prop : properties) {
      FieldSpec fieldSpec = FieldSpec.builder(prop.typeName, "default" + upperCamelizeHumanName(prop), PRIVATE).build();
      CodeBlock defaultValue = getDefaultValue(prop, fieldSpec, collectionsDefault);
      if (defaultValue == null) {
        defaultValue = CodeBlock.of("null");
      }
      fields.add(fieldSpec.toBuilder()
          .initializer(defaultValue)
          .build());
    }
    return fields;
  }

  private String upperCamelizeHumanName(Property prop) {
    return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, prop.humanName);
  }

  MethodSpec generateConstructor(Map<String, TypeName> properties) {
    List<ParameterSpec> params = Lists.newArrayList();
    for (Map.Entry<String, TypeName> entry : properties.entrySet()) {
      params.add(ParameterSpec.builder(entry.getValue(), entry.getKey()).build());
    }

    MethodSpec.Builder builder = MethodSpec.constructorBuilder()
        .addParameters(params);

    StringBuilder superFormat = new StringBuilder("super(");
    for (int i = properties.size(); i > 0; i--) {
      superFormat.append("$N");
      if (i > 1) superFormat.append(", ");
    }
    superFormat.append(")");
    builder.addStatement(superFormat.toString(), properties.keySet().toArray());

    return builder.build();
  }

  /**
   * Converts the ExecutableElement properties to TypeName properties
   */
  Map<String, TypeName> convertPropertiesToTypes(Map<String, ExecutableElement> properties) {
    Map<String, TypeName> types = new LinkedHashMap<String, TypeName>();
    for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
      ExecutableElement el = entry.getValue();
      types.put(entry.getKey(), TypeName.get(el.getReturnType()));
    }
    return types;
  }

  public TypeSpec createTypeAdapter(Context context, ClassName className, ClassName autoValueClassName,
      List<Property> properties) {
    ClassName jsonMapperClass = ClassName.get(JsonMapper.class);
    TypeName autoValueTypeName = autoValueClassName;
    ParameterizedTypeName superClass = ParameterizedTypeName.get(jsonMapperClass, autoValueTypeName);

    ImmutableMap<TypeName, FieldSpec> mappers = createMapperFields(properties);

    List<FieldSpec> defaultValues = createDefaultValueFields(properties, context.processingEnvironment());

    ParameterSpec loganSquareAutoParam = ParameterSpec.builder(JacksonAuto.class, "loganSquareAuto").build();
    ParameterSpec serializeNullObjectsParam = ParameterSpec.builder(boolean.class, "serializeNullObjects").build();
    ParameterSpec serializeNullCollectionElementsParam =
        ParameterSpec.builder(boolean.class, "serializeNullCollectionElements").build();
    MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
        .addParameter(loganSquareAutoParam)
        .addParameter(serializeNullObjectsParam)
        .addParameter(serializeNullCollectionElementsParam);


    // Initialize the serialize NULLs options
    FieldSpec serializeNullObjectsField = FieldSpec.builder(boolean.class, "serializeNullObjects")
        .addModifiers(PRIVATE, FINAL)
        .build();
    FieldSpec serializeNullCollectionElementsField = FieldSpec.builder(
        boolean.class, "serializeNullCollectionElements")
        .addModifiers(PRIVATE, FINAL)
        .build();

    constructor
        .addStatement("this.$N = $N", serializeNullObjectsField, serializeNullObjectsParam)
        .addStatement("this.$N = $N", serializeNullCollectionElementsField, serializeNullCollectionElementsParam);

    // Create the constructor with defaults for serialize NULLs
    MethodSpec.Builder constructorWithDefaults = MethodSpec.constructorBuilder()
        .addParameter(loganSquareAutoParam)
        .addModifiers(PUBLIC)
        .addStatement("this($N, false, false)", loganSquareAutoParam);

    // Initialize mappers
    for (Map.Entry<TypeName, FieldSpec> entry : mappers.entrySet()) {
      TypeName typeName = entry.getKey();
      FieldSpec field = entry.getValue();
      if (field != null) {
        TypeName type = typeName.isPrimitive() ? typeName.box() :  typeName;
        constructor.addStatement("this.$N = $N.mapperFor($T.class)", field, loganSquareAutoParam, type);
      }
    }

    // Add type covnverter methods for static converter object.
    // TODO: Need to figure out when this applies.
    Set<TypeName> usedDynamicFieldTypeConverters = new HashSet<>();
    for (Property property : properties) {
      usedDynamicFieldTypeConverters.addAll(property.type.getUsedTypeConverters());
    }

    ClassName jsonObjectMapperName = className.nestedClass("JsonObjectMapper");

    TypeSpec.Builder classBuilder = TypeSpec.classBuilder(jsonObjectMapperName)
        .addModifiers(PUBLIC, STATIC, FINAL)
        .superclass(superClass)
        .addFields(createTypeConverterFields(properties))
        .addField(serializeNullObjectsField)
        .addField(serializeNullCollectionElementsField)
        //.addFields(createDynamicTypeConverterFields(usedDynamicFieldTypeConverters))
        .addFields(mappers.values())
        .addFields(defaultValues)
        .addMethod(constructor.build())
        .addMethod(constructorWithDefaults.build())
        //.addMethods(createDynamicTypeConverterMethods(usedDynamicFieldTypeConverters))
        .addMethods(createDefaultMethods(jsonObjectMapperName, properties))
        .addMethod(createSerializeMethod(autoValueTypeName, properties, serializeNullObjectsField,
            serializeNullCollectionElementsField))
        .addMethod(createParseMethod(className, autoValueTypeName, properties))
        .addMethod(createParseFieldMethod(autoValueTypeName));

    return classBuilder.build();
  }

  public List<MethodSpec> createDefaultMethods(ClassName gsonTypeAdapterName, List<Property> properties) {
    List<MethodSpec> methodSpecs = new ArrayList<>(properties.size());
    for (Property prop : properties) {
      ParameterSpec valueParam = ParameterSpec.builder(prop.typeName, "default" + upperCamelizeHumanName(prop)).build();

      methodSpecs.add(MethodSpec.methodBuilder("setDefault" + upperCamelizeHumanName(prop))
          .addModifiers(PUBLIC)
          .addParameter(valueParam)
          .returns(gsonTypeAdapterName)
          .addCode(CodeBlock.builder()
              .addStatement("this.default$L = $N", upperCamelizeHumanName(prop), valueParam)
              .addStatement("return this")
              .build())
          .build());
    }
    return methodSpecs;
  }

  public MethodSpec createSerializeMethod(TypeName autoValueClassName, List<Property> properties, FieldSpec serializeNullObjectsField,
      FieldSpec serializeNullCollectionElementsField)
  {
    ParameterSpec annotatedParam = ParameterSpec.builder(autoValueClassName, "object").build();
    ParameterSpec jsonGenerator = ParameterSpec.builder(JsonGenerator.class, "jsonGenerator").build();
    ParameterSpec writeStartAndEndParam = ParameterSpec.builder(boolean.class, "writeStartAndEnd").build();
    MethodSpec.Builder serializeMethod = MethodSpec.methodBuilder("serialize")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(annotatedParam)
        .addParameter(jsonGenerator)
        .addParameter(writeStartAndEndParam)
        .addException(IOException.class);

    serializeMethod.beginControlFlow("if ($N)", writeStartAndEndParam);
    serializeMethod.addStatement("$N.writeStartObject()", jsonGenerator);
    serializeMethod.endControlFlow();

    serializeMethod.beginControlFlow("if ($N == null)", annotatedParam);
    serializeMethod.addStatement("$N.writeNull()", jsonGenerator);
    serializeMethod.addStatement("return");
    serializeMethod.endControlFlow();

    List<String> processedFields = new ArrayList<>(properties.size());
    for (Property prop : properties) {
      prop.type.serialize(serializeMethod, 1, prop.jsonNames()[0], processedFields,
          "object." + prop.methodName + "()", true, true, serializeNullObjectsField.name,
          serializeNullCollectionElementsField.name);
    }

    serializeMethod.beginControlFlow("if ($N)", writeStartAndEndParam);
    serializeMethod.addStatement("$N.writeEndObject()", jsonGenerator);
    serializeMethod.endControlFlow();

    return serializeMethod.build();
  }

  public MethodSpec createParseMethod(ClassName className, TypeName autoValueClassName, List<Property> properties) {
    ParameterSpec jsonParser = ParameterSpec.builder(JsonParser.class, "jsonParser").build();
    MethodSpec.Builder parseMethod = MethodSpec.methodBuilder("parse")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(autoValueClassName)
        .addParameter(jsonParser)
        .addException(IOException.class);

    parseMethod.beginControlFlow("if ($N.getCurrentToken() == null)", jsonParser);
    parseMethod.addStatement("$N.nextToken()", jsonParser);
    parseMethod.endControlFlow();

    parseMethod.beginControlFlow("if ($N.getCurrentToken() != $T.START_OBJECT)", jsonParser, JsonToken.class);
    parseMethod.addStatement("$N.skipChildren()", jsonParser);
    parseMethod.addStatement("return null");
    parseMethod.endControlFlow();

    // add the properties
    Map<Property, FieldSpec> fields = new LinkedHashMap<Property, FieldSpec>(properties.size());
    for (Property prop : properties) {
      TypeName fieldType = prop.typeName;
      FieldSpec field = FieldSpec.builder(fieldType, prop.humanName).build();
      fields.put(prop, field);
      parseMethod.addStatement("$T $N = this.default$L", field.type, field.name, upperCamelizeHumanName(prop));
    }

    parseMethod.beginControlFlow("while ($N.nextToken() != $T.END_OBJECT)", jsonParser, JsonToken.class);

    FieldSpec name = FieldSpec.builder(String.class, "_name").build();
    parseMethod.addStatement("$T $N = $N.getCurrentName()", name.type, name, jsonParser);
    parseMethod.addStatement("$N.nextToken()", jsonParser);

    parseMethod.beginControlFlow("switch ($N)", name);
    for (Map.Entry<Property, FieldSpec> entry : fields.entrySet()) {
      Property prop = entry.getKey();
      FieldSpec field = entry.getValue();

      boolean needToIndent = true;
      for (String jsonName : prop.jsonNames()) {
        if (needToIndent) {
          parseMethod.beginControlFlow("case $S:\n", jsonName);
          needToIndent = false;
        } else {
          parseMethod.addCode("case $S:\n", jsonName);
        }
      }
      prop.type.parse(parseMethod, 3, "$L = $L", new Object[]{prop.humanName});
      parseMethod.addStatement("break");
      parseMethod.endControlFlow();
    }

    parseMethod.endControlFlow(); // switch
    parseMethod.addStatement("$N.skipChildren()", jsonParser);

    parseMethod.endControlFlow(); // while

    StringBuilder format = new StringBuilder("return new ");
    format.append(className.simpleName().replaceAll("\\$", ""));
    if (autoValueClassName instanceof ParameterizedTypeName) {
      format.append("<>");
    }
    format.append("(");
    Iterator<FieldSpec> iterator = fields.values().iterator();
    while (iterator.hasNext()) {
      iterator.next();
      format.append("$N");
      if (iterator.hasNext()) format.append(", ");
    }
    format.append(")");
    parseMethod.addStatement(format.toString(), fields.values().toArray());

    return parseMethod.build();
  }

  public MethodSpec createParseFieldMethod(TypeName autoValueClassName) {
    return MethodSpec.methodBuilder("parseField")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(ParameterSpec.builder(autoValueClassName, "instance").build())
        .addParameter(ParameterSpec.builder(String.class, "fieldName").build())
        .addParameter(ParameterSpec.builder(JsonParser.class, "jsonParser").build())
        .addException(IOException.class)
        .build();
  }

  /**
   * Returns a default value for initializing well-known types, or else {@code null}.
   */
  private CodeBlock getDefaultValue(Property prop, FieldSpec field, boolean collectionsDefaultToEmpty) {
    if (field.type.isPrimitive()) {
      String defaultValue = getDefaultPrimitiveValue(field.type);
      if (defaultValue != null) {
        return CodeBlock.of("$L", defaultValue);
      } else {
        return CodeBlock.of("$T.valueOf(null)", field.type, field, field.type.box());
      }
    }
    if (prop.nullable()) {
      return null;
    }
    TypeMirror type = prop.element.getReturnType();
    if (type.getKind() != TypeKind.DECLARED) {
      return null;
    }
    TypeElement typeElement = MoreTypes.asTypeElement(type);
    if (typeElement == null) {
      return null;
    }
    if (collectionsDefaultToEmpty) {
      try {
        Class<?> clazz = Class.forName(typeElement.getQualifiedName()
            .toString());
        if (clazz.isAssignableFrom(List.class)) {
          return CodeBlock.of("$T.emptyList()", TypeName.get(Collections.class));
        } else if (clazz.isAssignableFrom(Map.class)) {
          return CodeBlock.of("$T.emptyMap()", TypeName.get(Collections.class));
        } else if (clazz.isAssignableFrom(Set.class)) {
          return CodeBlock.of("$T.emptySet()", TypeName.get(Collections.class));
        } else if (clazz.isAssignableFrom(ImmutableList.class)) {
          return CodeBlock.of("$T.of()", TypeName.get(ImmutableList.class));
        } else if (clazz.isAssignableFrom(ImmutableMap.class)) {
          return CodeBlock.of("$T.of()", TypeName.get(ImmutableMap.class));
        } else if (clazz.isAssignableFrom(ImmutableSet.class)) {
          return CodeBlock.of("$T.of()", TypeName.get(ImmutableSet.class));
        } else {
          return null;
        }
      } catch (ClassNotFoundException e) {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * @param type
   * @return the default primitive value as a String.  Returns null if unable to determine default value
   */
  private String getDefaultPrimitiveValue(TypeName type) {
    String valueString = null;
    try {
      Class<?> primitiveClass = Primitives.unwrap(Class.forName(type.box().toString()));
      if (primitiveClass != null) {
        Object defaultValue = Defaults.defaultValue(primitiveClass);
        if (defaultValue != null) {
          valueString = defaultValue.toString();
          if (!Strings.isNullOrEmpty(valueString)) {
            switch (type.toString()) {
              case "double":
                valueString = valueString + "d";
                break;
              case "float":
                valueString = valueString + "f";
                break;
              case "long":
                valueString = valueString + "L";
                break;
              case "char":
                valueString = "'" + valueString + "'";
                break;
            }
          }
        }
      }
    } catch (ClassNotFoundException ignored) {
      //Swallow and return null
    }

    return valueString;
  }
}
