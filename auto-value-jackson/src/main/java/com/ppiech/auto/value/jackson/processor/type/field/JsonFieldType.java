package com.ppiech.auto.value.jackson.processor.type.field;

import com.ppiech.auto.value.jackson.Constants;
import com.ppiech.auto.value.jackson.processor.type.TypeUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Set;

public class JsonFieldType extends FieldType {

  private final ClassName mClassName;
  private final String mMapperClassName;
  private final String mMapperVariableName;

  public JsonFieldType(ClassName className) {
    mClassName = className;
    mMapperClassName = mClassName.toString() + Constants.MAPPER_CLASS_SUFFIX;
    mMapperVariableName = TypeUtils.getMapperVariableName(mMapperClassName);
  }

  @Override
  public TypeName getTypeName() {
    return mClassName;
  }

  @Override
  public TypeName getNonPrimitiveTypeName() {
    return mClassName;
  }

  @Override
  public void parse(Builder builder, int depth, String setter, Object... setterFormatArgs) {
    setter = replaceLastLiteral(setter, "$L.parse($L)");
    builder.addStatement(setter, expandStringArgs(setterFormatArgs, mMapperVariableName, TypeUtils.JSON_PARSER_VARIABLE_NAME));
  }

  @Override
  public void serialize(Builder builder, int depth, String fieldName, List<String> processedFieldNames, String getter,
      boolean isObjectProperty, boolean checkIfNull, String writeIfNull, String writeCollectionElementIfNull) {

    if (checkIfNull) {
      builder.beginControlFlow("if ($L != null)", getter);
    }

    if (isObjectProperty) {
      builder.addStatement("$L.writeFieldName($S)", TypeUtils.JSON_GENERATOR_VARIABLE_NAME, fieldName);
    }

    builder.addStatement("$L.serialize($L, $L, true)", mMapperVariableName, getter, TypeUtils.JSON_GENERATOR_VARIABLE_NAME);

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
  public Set<ClassNameObjectMapper> getUsedJsonObjectMappers() {
    Set<ClassNameObjectMapper> set = super.getUsedJsonObjectMappers();
    set.add(new ClassNameObjectMapper(mClassName, mMapperClassName));
    return set;
  }
}
