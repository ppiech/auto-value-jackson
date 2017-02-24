package com.ppiech.auto.value.jackson.internal.objectmappers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.List;

/**
 * Built-in mapper for List objects of an unknown type
 */
public class ListMapper extends com.ppiech.auto.value.jackson.JsonMapper<List<Object>> {

    @Override
    public List<Object> parse(JsonParser jsonParser) throws IOException {
        return com.ppiech.auto.value.jackson.LoganSquare.mapperFor(Object.class).parseList(jsonParser);
    }

    @Override
    public void parseField(List<Object> instance, String fieldName, JsonParser jsonParser) throws IOException { }

    @Override
    public void serialize(List<Object> list, JsonGenerator generator, boolean writeStartAndEnd) throws IOException {
        com.ppiech.auto.value.jackson.LoganSquare.mapperFor(Object.class).serialize(list, generator);
    }

}
