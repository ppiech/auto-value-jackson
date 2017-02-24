package com.ppiech.auto.value.jackson.processor.example.logansqare;

import com.google.auto.value.AutoValue;
import com.ppiech.auto.value.jackson.JacksonAuto;
import com.ppiech.auto.value.jackson.JsonMapper;
import com.ppiech.auto.value.jackson.annotation.JsonProperty;
import com.ppiech.auto.value.jackson.typeconverters.StringBasedTypeConverter;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class EnumListModel {

    public static JsonMapper<EnumListModel> jsonMapper(JacksonAuto jacksonAuto) {
        return new AutoValue_EnumListModel.JsonObjectMapper(jacksonAuto);
    }

    public enum TestEnum {
        ONE, TWO
    }

    @JsonProperty(name = "enum_obj")
    abstract TestEnum enumObj();

    @JsonProperty(name = "enum_list")
    abstract List<TestEnum> enumList();

    @JsonProperty(name = "enum_map")
    abstract Map<String, TestEnum> enumMap();

    public static class LsEnumTestConverter extends StringBasedTypeConverter<TestEnum> {
        @Override
        public TestEnum getFromString(String string) {
            return TestEnum.valueOf(string);
        }

        @Override
        public String convertToString(TestEnum testEnum) {
            return testEnum.toString();
        }
    }
}