package com.ppiech.auto.value.jackson.processor.type.collection;

import com.fasterxml.jackson.core.JsonToken;
import com.ppiech.auto.value.jackson.processor.type.TextUtils;
import com.ppiech.auto.value.jackson.processor.type.TypeUtils;
import com.ppiech.auto.value.jackson.processor.type.Type;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;

import java.util.List;
import java.util.Map;

public abstract class MapCollectionType extends CollectionType {

  private final ClassName mClassName;

  public MapCollectionType(ClassName className) {
    mClassName = className;
  }

  @Override
  public String getParameterizedTypeString() {
    return "$T<$T, " + parameterTypes.get(1).getParameterizedTypeString() + ">";
  }

  @Override
  public Object[] getParameterizedTypeStringArgs() {
    return expandStringArgs(mClassName, String.class, parameterTypes.get(1).getParameterizedTypeStringArgs());
  }

  @Override
  public void parse(Builder builder, int depth, String setter, Object... setterFormatArgs) {
    Type parameterType = parameterTypes.get(1);

    final String mapVariableName = "map" + depth;
    final String keyVariableName = "key" + depth;

    final String instanceCreator = String
        .format("$T<$T, %s> $L = new $T<$T, %s>()", parameterType.getParameterizedTypeString(),
            parameterType.getParameterizedTypeString());
    final Object[] instanceCreatorArgs =
        expandStringArgs(getTypeName(), String.class, parameterType.getParameterizedTypeStringArgs(), mapVariableName,
            getTypeName(), String.class, parameterType.getParameterizedTypeStringArgs());

    builder.beginControlFlow("if ($L.getCurrentToken() == $T.START_OBJECT)", TypeUtils.JSON_PARSER_VARIABLE_NAME, JsonToken.class)
        .addStatement(instanceCreator, instanceCreatorArgs)
        .beginControlFlow("while ($L.nextToken() != $T.END_OBJECT)", TypeUtils.JSON_PARSER_VARIABLE_NAME, JsonToken.class)
        .addStatement("$T $L = $L.getText()", String.class, keyVariableName, TypeUtils.JSON_PARSER_VARIABLE_NAME)
        .addStatement("$L.nextToken()", TypeUtils.JSON_PARSER_VARIABLE_NAME)
        .beginControlFlow("if ($L.getCurrentToken() == $T.VALUE_NULL)", TypeUtils.JSON_PARSER_VARIABLE_NAME, JsonToken.class)
        .addStatement("$L.put($L, null)", mapVariableName, keyVariableName)
        .nextControlFlow("else");

    parameterType.parse(builder, depth + 1, "$L.put($L, $L)", mapVariableName, keyVariableName);

    builder
        .endControlFlow()
        .endControlFlow()
        .addStatement(setter, expandStringArgs(setterFormatArgs, mapVariableName))
        .nextControlFlow("else")
        .addStatement(setter, expandStringArgs(setterFormatArgs, "null"))
        .endControlFlow();
  }

  @Override
  public void serialize(MethodSpec.Builder builder, int depth, String fieldName,
      List<String> processedFieldNames, String getter, boolean isObjectProperty, boolean checkIfNull,
      String writeIfNull, String writeCollectionElementIfNull) {
    Type parameterType = parameterTypes.get(1);
    final String cleanFieldName = TextUtils.toUniqueFieldNameVariable(fieldName, processedFieldNames);
    final String mapVariableName = "lslocal" + cleanFieldName;
    final String entryVariableName = "entry" + depth;

    final String instanceCreator =
        String.format("final $T<$T, %s> $L = $L", parameterType.getParameterizedTypeString());
    final Object[] instanceCreatorArgs =
        expandStringArgs(Map.class, String.class, parameterType.getParameterizedTypeStringArgs(), mapVariableName,
            getter);

    final String forLine =
        String.format("for ($T<$T, %s> $L : $L.entrySet())", parameterType.getParameterizedTypeString());
    final Object[] forLineArgs =
        expandStringArgs(Map.Entry.class, String.class, parameterType.getParameterizedTypeStringArgs(),
            entryVariableName, mapVariableName);

    builder
        .addStatement(instanceCreator, instanceCreatorArgs)
        .beginControlFlow("if ($L != null)", mapVariableName);

    if (isObjectProperty) {
      builder.addStatement("$L.writeFieldName($S)", TypeUtils.JSON_GENERATOR_VARIABLE_NAME, fieldName);
    }

    builder
        .addStatement("$L.writeStartObject()", TypeUtils.JSON_GENERATOR_VARIABLE_NAME)
        .beginControlFlow(forLine, forLineArgs)
        .addStatement("$L.writeFieldName($L.getKey().toString())", TypeUtils.JSON_GENERATOR_VARIABLE_NAME, entryVariableName)
        .beginControlFlow("if ($L.getValue() != null)", entryVariableName);

    parameterType.serialize(builder, depth + 1, mapVariableName + "Element", processedFieldNames,
        entryVariableName + ".getValue()", false, false, "true", writeCollectionElementIfNull);

    builder
        .nextControlFlow("else if ($N)", writeCollectionElementIfNull)
        .addStatement("$L.writeNull()", TypeUtils.JSON_GENERATOR_VARIABLE_NAME);

    builder
        .endControlFlow()
        .endControlFlow()
        .addStatement("$L.writeEndObject()", TypeUtils.JSON_GENERATOR_VARIABLE_NAME)
        .endControlFlow();
  }
}
