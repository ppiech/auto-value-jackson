package com.ppiech.auto.value.jackson.processor.type.field;

import com.fasterxml.jackson.core.JsonToken;
import com.ppiech.auto.value.jackson.processor.type.TypeUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

public class IntegerFieldType extends NumberFieldType {

  public IntegerFieldType(boolean isPrimitive) {
    super(isPrimitive);
  }

  @Override
  public TypeName getTypeName() {
    return (isPrimitive ? TypeName.INT : ClassName.get(Integer.class));
  }

  @Override
  public TypeName getNonPrimitiveTypeName() {
    return ClassName.get(Integer.class);
  }

  @Override
  public void parse(Builder builder, int depth, String setter, Object... setterFormatArgs) {
    if (isPrimitive) {
      setter = replaceLastLiteral(setter, "$L.getValueAsInt()");
      builder.addStatement(setter, expandStringArgs(setterFormatArgs, TypeUtils.JSON_PARSER_VARIABLE_NAME));
    } else {
      setter = replaceLastLiteral(setter,
          "$L.getCurrentToken() == $T.VALUE_NULL ? null : Integer.valueOf($L.getValueAsInt())");
      builder.addStatement(setter,
          expandStringArgs(setterFormatArgs, TypeUtils.JSON_PARSER_VARIABLE_NAME, JsonToken.class, TypeUtils.JSON_PARSER_VARIABLE_NAME));
    }
  }
}
