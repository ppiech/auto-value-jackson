package com.ppiech.auto.value.jackson.processor.example.gson;

import com.google.auto.value.AutoValue;
import com.ppiech.auto.value.jackson.JacksonAuto;
import com.ppiech.auto.value.jackson.JsonMapper;
import com.ppiech.auto.value.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class Person {
    public abstract String name();

    public abstract int gender();

    public abstract int age();

    public abstract List<String> friends();

    @JsonProperty(typeConverter=BirthdateConverter.class)
    public abstract Date birthdate();

    public abstract Address address();

    public static Builder builder() {
        return new AutoValue_Person.Builder();
    }

    public static JsonMapper<Person> jsonMapper(JacksonAuto jacksonAuto) {
        return new AutoValue_Person.JsonObjectMapper(jacksonAuto);
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder name(String name);

        public abstract Builder gender(int gender);

        public abstract Builder age(int age);

        public abstract Builder friends(List<String> friends);

        public abstract Builder birthdate(Date birthdate);

        public abstract Builder address(Address address);

        public abstract Person build();
    }
}
