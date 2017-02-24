package com.ppiech.auto.value.jackson.processor.type.field;

import com.ppiech.auto.value.jackson.processor.type.TypeUtils;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Set;

public class AutoFieldType extends FieldType {

  private final TypeName mTypeName;
  private final String mMapperClassName;

  public AutoFieldType(TypeMirror typeMirror) {
    mTypeName = TypeName.get(typeMirror);
    mMapperClassName = TypeUtils.getMapperVariableName(typeMirror.toString());
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
    setter = replaceLastLiteral(setter, "$N.parse($L)");
    builder.addStatement(setter, expandStringArgs(setterFormatArgs, mMapperClassName, TypeUtils.JSON_PARSER_VARIABLE_NAME));
  }

  @Override
  public void serialize(Builder builder, int depth, String fieldName, List<String> processedFieldNames, String getter,
      boolean isObjectProperty, boolean checkIfNull, String writeIfNull, String writeCollectionElementIfNull)
  {
    if (!mTypeName.isPrimitive() && checkIfNull) {
      builder.beginControlFlow("if ($L != null)", getter);
    }

    if (isObjectProperty) {
      builder.addStatement("$L.writeFieldName($S)", TypeUtils.JSON_GENERATOR_VARIABLE_NAME, fieldName);
    }

    builder.addStatement("$N.serialize($L, $L, true)", mMapperClassName, getter, TypeUtils.JSON_GENERATOR_VARIABLE_NAME);

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
  public Set<ClassNameObjectMapper> getUsedJsonObjectMappers() {
    Set<ClassNameObjectMapper> set = super.getUsedJsonObjectMappers();
    set.add(new ClassNameObjectMapper(mTypeName, mMapperClassName));
    return set;
  }
}
