package com.ppiech.auto.value.jackson.processor.example.logansqare;


import com.google.auto.value.AutoValue;
import com.ppiech.auto.value.jackson.JacksonAuto;
import com.ppiech.auto.value.jackson.JsonMapper;
import com.ppiech.auto.value.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

@AutoValue
public abstract class SimpleModel {

    public static JsonMapper<SimpleModel> jsonMapper(JacksonAuto jacksonAuto) {
        return new AutoValue_SimpleModel.JsonObjectMapper(jacksonAuto, true, false);
    }

    @JsonProperty
    public abstract String string();

    @JsonProperty
    @Nullable
    public abstract Date date();

    @JsonProperty(name = "test_int")
    public abstract int testInt();

    @JsonProperty(name = "test_long")
    public abstract long testLong();

    @JsonProperty(name = "test_float")
    public abstract float testFloat();

    @JsonProperty(name = "test_double")
    public abstract double testDouble();

    @JsonProperty(name = "test_string")
    @Nullable
    public abstract String testString();

    @JsonProperty(name = "test_int_obj")
    @Nullable
    public abstract Integer testIntObj();

    @JsonProperty(name = "test_long_obj")
    @Nullable
    public abstract Long testLongObj();

    @JsonProperty(name = "test_float_obj")
    @Nullable
    public abstract Float testFloatObj();

    @JsonProperty(name = "test_double_obj")
    @Nullable
    public abstract Double testDoubleObj();

    @JsonProperty(name = "object_map")
    @Nullable
    public abstract Map<String, Object> objectMap();
}
