package com.ppiech.auto.value.jackson.processor.type.field;

import com.ppiech.auto.value.jackson.processor.type.TypeUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Set;

import static com.ppiech.auto.value.jackson.processor.type.TypeUtils.JSON_GENERATOR_VARIABLE_NAME;
import static com.ppiech.auto.value.jackson.processor.type.TypeUtils.JSON_PARSER_VARIABLE_NAME;

public class TypeConverterFieldType extends FieldType {

  private TypeName mTypeName;
  private ClassName mTypeConverter;

  public TypeConverterFieldType(TypeName typeName, ClassName typeConverterClassName) {
    mTypeName = typeName;
    mTypeConverter = typeConverterClassName;
  }

  @Override
  public TypeName getTypeName() {
    return mTypeName;
  }

  @Override
  public TypeName getNonPrimitiveTypeName() {
    return mTypeName;
  }

  public ClassName getTypeConverterClassName() {
    return mTypeConverter;
  }

  @Override
  public void parse(Builder builder, int depth, String setter, Object... setterFormatArgs) {
    setter = replaceLastLiteral(setter, "$L.parse($L)");
    builder.addStatement(setter,
        expandStringArgs(setterFormatArgs, TypeUtils.getStaticFinalTypeConverterVariableName(mTypeConverter),
            JSON_PARSER_VARIABLE_NAME));
  }

  @Override
  public void serialize(Builder builder, int depth, String fieldName, List<String> processedFieldNames, String getter,
      boolean isObjectProperty, boolean checkIfNull, String writeIfNull, String writeCollectionElementIfNull) {
    builder
        .addStatement("$L.serialize($L, $S, $L, $L)", TypeUtils.getStaticFinalTypeConverterVariableName(mTypeConverter),
            getter, fieldName, isObjectProperty, JSON_GENERATOR_VARIABLE_NAME);
  }

  @Override
  public Set<TypeName> getUsedTypeConverters() {
    return super.getUsedTypeConverters();
  }
}
