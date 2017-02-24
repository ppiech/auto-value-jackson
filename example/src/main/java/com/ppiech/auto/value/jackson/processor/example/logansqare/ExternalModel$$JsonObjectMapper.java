package com.ppiech.auto.value.jackson.processor.example.logansqare;

import com.ppiech.auto.value.jackson.JsonMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;

@SuppressWarnings("unsafe,unchecked")
public final class ExternalModel$$JsonObjectMapper extends JsonMapper<ExternalModel> {
  @Override
  public ExternalModel parse(JsonParser jsonParser) throws IOException {
    ExternalModel instance = new ExternalModel();
    if (jsonParser.getCurrentToken() == null) {
      jsonParser.nextToken();
    }
    if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
      jsonParser.skipChildren();
      return null;
    }
    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      String fieldName = jsonParser.getCurrentName();
      jsonParser.nextToken();
      parseField(instance, fieldName, jsonParser);
      jsonParser.skipChildren();
    }
    return instance;
  }

  @Override
  public void parseField(ExternalModel instance, String fieldName, JsonParser jsonParser) throws IOException {
    if ("string".equals(fieldName)) {
      instance.string = jsonParser.getValueAsString(null);
    }
  }

  @Override
  public void serialize(ExternalModel object, JsonGenerator jsonGenerator, boolean writeStartAndEnd) throws IOException {
    if (writeStartAndEnd) {
      jsonGenerator.writeStartObject();
    }
    if (object.string != null) {
      jsonGenerator.writeStringField("string", object.string);
    } else {
      jsonGenerator.writeFieldName("string");
      jsonGenerator.writeNull();
    }
    if (writeStartAndEnd) {
      jsonGenerator.writeEndObject();
    }
  }
}
