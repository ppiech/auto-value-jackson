package com.ppiech.auto.value.jackson.processor.example;

import com.ppiech.auto.value.jackson.annotation.AutoValueJsonMapperFactory;
import com.ppiech.auto.value.jackson.JsonMapperFactory;

@AutoValueJsonMapperFactory
public abstract class SampleMapperFactory implements JsonMapperFactory {

    public static SampleMapperFactory create() {
        return new AutoValueJackson_SampleMapperFactory();
    }
}
