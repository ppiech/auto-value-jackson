package com.ppiech.auto.value.jackson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class JacksonAuto {

    public static class Builder {
        List<JsonMapperFactory> factories = new ArrayList<>();
        public Builder registerJsonMapperFactory(JsonMapperFactory factory) {
            factories.add(factory);
            return this;
        }

        public JacksonAuto build() {
            factories.add(new DefaultJsonMapperFactory());
            return new JacksonAuto(factories);
        }
    }

    private final Map<Class, JsonMapper> mappers = Collections.synchronizedMap(new HashMap<Class, JsonMapper>());
    private final List<JsonMapperFactory> factories;

    private JacksonAuto(List<JsonMapperFactory> factories) {
        this.factories = factories;
    }

    /**
     * @return Returns a JsonMapper for a given class that has been annotated with @JsonObject.
     *
     * @param cls The class for which the JsonMapper should be fetched.
     * @param <E> Type of mapper for given object class.
     * @throws NoSuchMapperException thrown if no matching mapper is found.
     */

    public <E> JsonMapper<E> mapperFor(Class<E> cls) throws NoSuchMapperException {
        JsonMapper<E> mapper = null;
        if (mappers.containsKey(cls)) {
            mapper = mappers.get(cls);
        } else {
            for (JsonMapperFactory factory : factories) {
                mapper = factory.mapperFor(this, cls);
                if (mapper != null) {
                    break;
                }
            }
            mappers.put(cls, mapper);
        }
        if (mapper == null) {
            throw new NoSuchMapperException(cls);
        }
        return mapper;
    }

    /**
     * Parse an object from an InputStream.
     *
     * @param is The InputStream, most likely from your networking library.
     * @param jsonObjectClass The @JsonObject class to parse the InputStream into
     * @param <E> Type of object based on given class.
     * @return deserialized object
     * @throws IOException thrown if there's an error deserializing object
     */
    public <E> E parse(InputStream is, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).parse(is);
    }

    /**
     * Parse an object from a String. Note: parsing from an InputStream should be preferred over parsing from a String if possible.
     *
     * @param jsonString The JSON string being parsed.
     * @param jsonObjectClass The @JsonObject class to parse the InputStream into
     * @param <E> Type of object based on given class.
     * @return deserialized object
     * @throws IOException thrown if there's an error deserializing object
     */
    public <E> E parse(String jsonString, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).parse(jsonString);
    }

    /**
     * Parse a list of objects from an InputStream.
     *
     * @param is The inputStream, most likely from your networking library.
     * @param jsonObjectClass The @JsonObject class to parse the InputStream into
     * @param <E> Type of object based on given class.
     * @return deserialized object list
     * @throws IOException thrown if there's an error deserializing object
     */
    public <E> List<E> parseList(InputStream is, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).parseList(is);
    }

    /**
     * Parse a list of objects from a String. Note: parsing from an InputStream should be preferred over parsing from a String if possible.
     *
     * @param jsonString The JSON string being parsed.
     * @param jsonObjectClass The @JsonObject class to parse the InputStream into
     * @param <E> Type of object based on given class.
     * @return deserialized object list
     * @throws IOException thrown if there's an error deserializing object
     */
    public <E> List<E> parseList(String jsonString, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).parseList(jsonString);
    }

    /**
     * Parse a map of objects from an InputStream.
     *
     * @param is The inputStream, most likely from your networking library.
     * @param jsonObjectClass The @JsonObject class to parse the InputStream into
     * @param <E> Type of object based on given class.
     * @return deserialized object map
     * @throws IOException thrown if there's an error deserializing object
     */
    public <E> Map<String, E> parseMap(InputStream is, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).parseMap(is);
    }

    /**
     * Parse a map of objects from a String. Note: parsing from an InputStream should be preferred over parsing from a String if possible.
     *
     * @param jsonString The JSON string being parsed.
     * @param jsonObjectClass The @JsonObject class to parse the InputStream into
     * @param <E> Type of object based on given class.
     * @return deserialized object map
     * @throws IOException thrown if there's an error deserializing object
     */
    public <E> Map<String, E> parseMap(String jsonString, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).parseMap(jsonString);
    }

    /**
     * Serialize an object to a JSON String.
     *
     * @param object The object to serialize.
     * @param <E> Type of object based on given object.
     * @throws IOException thrown if there's an error serializing object
     * @return JSON string
     */
    @SuppressWarnings("unchecked")
    public <E> String serialize(E object) throws IOException {
        return mapperFor((Class<E>)object.getClass()).serialize(object);
    }

    /**
     * Serialize an object to an OutputStream.
     *
     * @param object The object to serialize.
     * @param os The OutputStream being written to.
     * @param <E> Type of object based on given object.
     * @throws IOException thrown if there's an error serializing object
     */
    @SuppressWarnings("unchecked")
    public <E> void serialize(E object, OutputStream os) throws IOException {
        mapperFor((Class<E>)object.getClass()).serialize(object, os);
    }

    /**
     * Serialize a list of objects to a JSON String.
     *
     * @param list The list of objects to serialize.
     * @param jsonObjectClass The @JsonObject class of the list elements
     * @param <E> Type of object based on given list type.
     * @throws IOException thrown if there's an error serializing object
     * @return JSON string
     */
    public <E> String serialize(List<E> list, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).serialize(list);
    }

    /**
     * Serialize a list of objects to an OutputStream.
     *
     * @param list The list of objects to serialize.
     * @param os The OutputStream to which the list should be serialized
     * @param jsonObjectClass The @JsonObject class of the list elements
     * @param <E> Type of object based on given list type.
     * @throws IOException thrown if there's an error serializing object
     */
    public <E> void serialize(List<E> list, OutputStream os, Class<E> jsonObjectClass) throws IOException {
        mapperFor(jsonObjectClass).serialize(list, os);
    }

    /**
     * Serialize a map of objects to a JSON String.
     *
     * @param map The map of objects to serialize.
     * @param jsonObjectClass The @JsonObject class of the list elements
     * @param <E> Type of object based on given type.
     * @throws IOException thrown if there's an error serializing object
     * @return JSON string
     */
    public <E> String serialize(Map<String, E> map, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).serialize(map);
    }

    /**
     * Serialize a map of objects to an OutputStream.
     *
     * @param map The map of objects to serialize.
     * @param os The OutputStream to which the list should be serialized
     * @param jsonObjectClass The @JsonObject class of the list elements
     * @param <E> Type of object based on given type.
     * @throws IOException thrown if there's an error serializing object
     */
    public <E> void serialize(Map<String, E> map, OutputStream os, Class<E> jsonObjectClass) throws IOException {
        mapperFor(jsonObjectClass).serialize(map, os);
    }


    private static class DefaultJsonMapperFactory implements JsonMapperFactory {
        @Override
        public <E> JsonMapper<E> mapperFor(JacksonAuto jacksonAuto, Class<E> cls) {
            try {
                return LoganSquare.mapperFor(cls);
            } catch (NoSuchMapperException e) {
                return null;
            }
        }
    }

}
