package com.ppiech.auto.value.jackson.processor.type.field;

import com.ppiech.auto.value.jackson.processor.type.TypeUtils;
import com.ppiech.auto.value.jackson.processor.type.Type;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

public class ParameterizedTypeField extends FieldType {

  private final TypeName mTypeName;
  private String mJsonMapperVariableName;

  public ParameterizedTypeField(TypeName typeName) {
    mTypeName = typeName;
  }

  @Override
  public void parse(Builder builder, int depth, String setter, Object... setterFormatArgs) {
    setter = replaceLastLiteral(setter, String.format("%s.parse($L)", mJsonMapperVariableName));
    builder.addStatement(setter, expandStringArgs(setterFormatArgs, TypeUtils.JSON_PARSER_VARIABLE_NAME));
  }

  @Override
  public void serialize(Builder builder, int depth, String fieldName,
      List<String> processedFieldNames, String getter, boolean isObjectProperty, boolean checkIfNull,
      String writeIfNull, String writeCollectionElementIfNull) {
    if (checkIfNull) {
      builder.beginControlFlow("if ($L != null)", getter);
    }

    if (isObjectProperty) {
      builder.addStatement("$L.writeFieldName($S)", TypeUtils.JSON_GENERATOR_VARIABLE_NAME, fieldName);
    }
    builder.addStatement("$L.serialize($L, $L, true)", mJsonMapperVariableName, getter, TypeUtils.JSON_GENERATOR_VARIABLE_NAME);

    if (checkIfNull) {
      builder.nextControlFlow("else if ($N)", writeIfNull);

      if (isObjectProperty) {
        builder.addStatement("$L.writeFieldName($S)", TypeUtils.JSON_GENERATOR_VARIABLE_NAME, fieldName);
      }
      builder.addStatement("$L.writeNull()", TypeUtils.JSON_GENERATOR_VARIABLE_NAME);
      builder.endControlFlow();
    }
  }

  @Override
  public String getParameterizedTypeString() {
    if (parameterTypes.size() > 0) {
      StringBuilder string = new StringBuilder("$T<");
      for (int i = 0; i < parameterTypes.size(); i++) {
        if (i > 0) {
          string.append(", ");
        }
        string.append(parameterTypes.get(i).getParameterizedTypeString());
      }
      string.append('>');
      return string.toString();
    } else {
      return "$T";
    }
  }

  @Override
  public Object[] getParameterizedTypeStringArgs() {
    List<Object> args = new ArrayList<>();

    args.add(mTypeName);

    for (Type parameterType : parameterTypes) {
      args.add(parameterType.getParameterizedTypeStringArgs());
    }

    return args.toArray(new Object[args.size()]);
  }

  @Override
  public TypeName getNonPrimitiveTypeName() {
    return mTypeName;
  }

  @Override
  public TypeName getTypeName() {
    return mTypeName;
  }

  public void setJsonMapperVariableName(String jsonMapperVariableName) {
    mJsonMapperVariableName = jsonMapperVariableName;
  }

  public String getParameterName() {
    return mTypeName.toString();
  }
}
