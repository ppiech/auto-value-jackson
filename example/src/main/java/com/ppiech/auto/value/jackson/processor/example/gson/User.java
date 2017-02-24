package com.ppiech.auto.value.jackson.processor.example.gson;

import com.google.auto.value.AutoValue;
import com.ppiech.auto.value.jackson.JacksonAuto;
import com.ppiech.auto.value.jackson.JsonMapper;

@AutoValue
public abstract class User {
  abstract String firstname();
  abstract String lastname();

  public static JsonMapper<User> jsonMapper(JacksonAuto jacksonAuto) {
    return new AutoValue_User.JsonObjectMapper(jacksonAuto);
  }

  public static User with(String firstname, String lastname) {
    return new AutoValue_User(firstname, lastname);
  }
}
