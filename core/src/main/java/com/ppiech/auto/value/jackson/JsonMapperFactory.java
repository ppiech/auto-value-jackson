package com.ppiech.auto.value.jackson;

public interface JsonMapperFactory {
    <E> JsonMapper<E> mapperFor(JacksonAuto jacksonAuto, Class<E> cls);
}
