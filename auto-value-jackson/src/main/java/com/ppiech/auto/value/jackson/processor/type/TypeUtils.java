package com.ppiech.auto.value.jackson.processor.type;

import com.ppiech.auto.value.jackson.Constants;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.List;

public class TypeUtils {


  public static final String JSON_PARSER_VARIABLE_NAME = "jsonParser";
  public static final String JSON_GENERATOR_VARIABLE_NAME = "jsonGenerator";

  public static String getSimpleClassName(TypeElement type, String packageName) {
    return type.getQualifiedName().toString().substring(packageName.length() + 1).replace('.', '$');
  }

  public static String getInjectedFQCN(TypeElement type, Elements elements) {
    String packageName = elements.getPackageOf(type).getQualifiedName().toString();
    return packageName + "." + getSimpleClassName(type, packageName) + Constants.MAPPER_CLASS_SUFFIX;
  }

  public static String getInjectedFQCN(ClassName className) {
    StringBuilder name = new StringBuilder();
    for (String part : className.simpleNames()) {
      if (name.length() > 0) {
        name.append("$");
      }
      name.append(part);
    }
    return className.packageName() + "." + name.toString() + Constants.MAPPER_CLASS_SUFFIX;
  }

  @SuppressWarnings("unchecked")
  public static List<TypeMirror> getParameterizedTypes(TypeMirror typeMirror) {
    if (!(typeMirror instanceof DeclaredType)) {
      return null;
    }

    DeclaredType declaredType = (DeclaredType) typeMirror;
    return (List<TypeMirror>) declaredType.getTypeArguments();
  }

  public static String getStaticFinalTypeConverterVariableName(TypeName typeName) {
    return typeName.toString().replaceAll("\\.", "_").replaceAll("\\$", "_").toUpperCase();
  }

  public static String getTypeConverterVariableName(TypeName typeName) {
    return typeName.toString().replaceAll("\\.", "_").replaceAll("\\$", "_") + "_type_converter";
  }

  public static String getMapperVariableName(Class cls) {
    return getMapperVariableName(cls.getCanonicalName());
  }

  public static String getMapperVariableName(String fullyQualifiedClassName) {
    return fullyQualifiedClassName.replaceAll("\\.", "_").replaceAll("\\$", "_");
  }

  public static String getTypeConverterGetter(TypeName typeName) {
    return "get" + getTypeConverterVariableName(typeName);
  }

}
