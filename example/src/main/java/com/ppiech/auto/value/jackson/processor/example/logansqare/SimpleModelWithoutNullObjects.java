package com.ppiech.auto.value.jackson.processor.example.logansqare;


import com.google.auto.value.AutoValue;
import com.ppiech.auto.value.jackson.JacksonAuto;
import com.ppiech.auto.value.jackson.JsonMapper;
import com.ppiech.auto.value.jackson.annotation.JsonProperty;

import java.util.Date;

@AutoValue
public abstract class SimpleModelWithoutNullObjects {

    public static JsonMapper<SimpleModelWithoutNullObjects> jsonMapper(JacksonAuto jacksonAuto) {
        return new AutoValue_SimpleModelWithoutNullObjects.JsonObjectMapper(jacksonAuto);
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
}
