package com.ppiech.auto.value.jackson.processor.example.gson;

import com.google.auto.value.AutoValue;
import com.ppiech.auto.value.jackson.JacksonAuto;
import com.ppiech.auto.value.jackson.JsonMapper;
import com.ppiech.auto.value.jackson.annotation.JsonProperty;

@AutoValue
public abstract class Address {

    public static Address create(String streetName, String city) {
        return new AutoValue_Address(streetName, city);
    }

    public static JsonMapper<Address> jsonMapper(JacksonAuto jacksonAuto) {
        return new AutoValue_Address.JsonObjectMapper(jacksonAuto);
    }

    @JsonProperty(name="street-name")
    public abstract String streetName();

    public abstract String city();
}
