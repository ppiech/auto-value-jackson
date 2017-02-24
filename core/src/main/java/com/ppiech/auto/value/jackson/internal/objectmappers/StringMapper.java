package com.ppiech.auto.value.jackson.internal.objectmappers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

/**
 * Built-in mapper for String objects
 */
public class StringMapper extends com.ppiech.auto.value.jackson.JsonMapper<String> {

    @Override
    public String parse(JsonParser jsonParser) throws IOException {
        return jsonParser.getText();
    }

    @Override
    public void parseField(String instance, String fieldName, JsonParser jsonParser) throws IOException { }

    @Override
    public void serialize(String object, JsonGenerator generator, boolean writeStartAndEnd) throws IOException {
        generator.writeString(object);
    }
}
