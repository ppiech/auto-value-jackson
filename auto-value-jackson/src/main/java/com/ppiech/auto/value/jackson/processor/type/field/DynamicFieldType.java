package com.ppiech.auto.value.jackson.processor.type.field;

import com.ppiech.auto.value.jackson.LoganSquare;
import com.ppiech.auto.value.jackson.processor.type.TypeUtils;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Set;

public class DynamicFieldType extends FieldType {

  private TypeName mTypeName;

  public DynamicFieldType(TypeName typeName) {
    mTypeName = typeName;
  }

  @Override
  public TypeName getTypeName() {
    return mTypeName;
  }

  @Override
  public TypeName getNonPrimitiveTypeName() {
    return mTypeName;
  }

  @Override
  public void parse(Builder builder, int depth, String setter, Object... setterFormatArgs) {
    setter = replaceLastLiteral(setter, "$T.typeConverterFor($T.class).parse($L)");
    builder.addStatement(setter,
        expandStringArgs(setterFormatArgs, LoganSquare.class, mTypeName, TypeUtils.JSON_PARSER_VARIABLE_NAME));
  }

  @Override
  public void serialize(Builder builder, int depth, String fieldName, List<String> processedFieldNames, String getter,
      boolean isObjectProperty, boolean checkIfNull, String writeIfNull, String writeCollectionElementIfNull) {
    if (!mTypeName.isPrimitive() && checkIfNull) {
      builder.beginControlFlow("if ($L != null)", getter);
    }

    builder
        .addStatement("$T.typeConverterFor($T.class).serialize($L, $S, $L, $L)", LoganSquare.class, mTypeName, getter,
            isObjectProperty
                ? fieldName : null, isObjectProperty, TypeUtils.JSON_GENERATOR_VARIABLE_NAME);

    if (!mTypeName.isPrimitive() && checkIfNull) {
      builder.nextControlFlow("else if ($N)", writeIfNull);

      if (isObjectProperty) {
        builder.addStatement("$L.writeFieldName($S)", TypeUtils.JSON_GENERATOR_VARIABLE_NAME, fieldName);
      }
      builder.addStatement("$L.writeNull()", TypeUtils.JSON_GENERATOR_VARIABLE_NAME);
      builder.endControlFlow();
    }
  }

  @Override
  public Set<TypeName> getUsedTypeConverters() {
    Set<TypeName> set = super.getUsedTypeConverters();
    set.add(mTypeName);
    return set;
  }
}
