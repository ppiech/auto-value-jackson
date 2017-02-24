package com.ppiech.auto.value.jackson.processor.type.field;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

import java.util.List;

import static com.ppiech.auto.value.jackson.processor.type.TypeUtils.JSON_GENERATOR_VARIABLE_NAME;
import static com.ppiech.auto.value.jackson.processor.type.TypeUtils.JSON_PARSER_VARIABLE_NAME;

public class StringFieldType extends FieldType {

  @Override
  public TypeName getTypeName() {
    return ClassName.get(String.class);
  }

  @Override
  public TypeName getNonPrimitiveTypeName() {
    return ClassName.get(String.class);
  }

  @Override
  public void parse(Builder builder, int depth, String setter, Object... setterFormatArgs) {
    setter = replaceLastLiteral(setter, "$L.getValueAsString(null)");
    builder.addStatement(setter, expandStringArgs(setterFormatArgs, JSON_PARSER_VARIABLE_NAME));
  }

  @Override
  public void serialize(Builder builder, int depth, String fieldName, List<String> processedFieldNames, String getter,
      boolean isObjectProperty, boolean checkIfNull, String writeIfNull, String writeCollectionElementIfNull) {
    if (checkIfNull) {
      builder.beginControlFlow("if ($L != null)", getter);
    }

    if (isObjectProperty) {
      builder.addStatement("$L.writeStringField($S, $L)", JSON_GENERATOR_VARIABLE_NAME, fieldName, getter);
    } else {
      builder.addStatement("$L.writeString($L)", JSON_GENERATOR_VARIABLE_NAME, getter);
    }

    if (checkIfNull) {
      builder.nextControlFlow("else if ($N)", writeIfNull);

      if (isObjectProperty) {
        builder.addStatement("$L.writeFieldName($S)", JSON_GENERATOR_VARIABLE_NAME, fieldName);
      }
      builder.addStatement("$L.writeNull()", JSON_GENERATOR_VARIABLE_NAME);

      builder.endControlFlow();
    }
  }
}