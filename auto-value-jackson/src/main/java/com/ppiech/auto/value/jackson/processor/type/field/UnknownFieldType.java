package com.ppiech.auto.value.jackson.processor.type.field;

import com.ppiech.auto.value.jackson.internal.objectmappers.ObjectMapper;
import com.ppiech.auto.value.jackson.processor.type.TypeUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Set;


public class UnknownFieldType extends FieldType {

  @Override
  public TypeName getTypeName() {
    return ClassName.get(Object.class);
  }

  @Override
  public TypeName getNonPrimitiveTypeName() {
    return ClassName.get(Object.class);
  }

  @Override
  public void parse(Builder builder, int depth, String setter, Object... setterFormatArgs) {
    setter = replaceLastLiteral(setter, "$L.parse($L)");
    builder.addStatement(setter, expandStringArgs(setterFormatArgs, TypeUtils.getMapperVariableName(ObjectMapper.class),
        TypeUtils.JSON_PARSER_VARIABLE_NAME));
  }

  @Override
  public void serialize(Builder builder, int depth, String fieldName, List<String> processedFieldNames, String getter,
      boolean isObjectProperty, boolean checkIfNull, String writeIfNull, String writeCollectionElementIfNull) {
    if (checkIfNull) {
      builder.beginControlFlow("if ($L != null)", getter);
    }

    builder.addStatement("$L.serialize($L, $L, $L)", TypeUtils.getMapperVariableName(ObjectMapper.class), getter,
        TypeUtils.JSON_GENERATOR_VARIABLE_NAME, isObjectProperty);

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
    set.add(new ClassNameObjectMapper(ClassName.get(Object.class), ObjectMapper.class.getCanonicalName()));
    return set;
  }
}
