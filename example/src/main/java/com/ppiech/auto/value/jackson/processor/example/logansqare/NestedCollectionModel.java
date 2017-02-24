package com.ppiech.auto.value.jackson.processor.example.logansqare;

import com.google.auto.value.AutoValue;
import com.ppiech.auto.value.jackson.JacksonAuto;
import com.ppiech.auto.value.jackson.JsonMapper;
import com.ppiech.auto.value.jackson.annotation.JsonProperty;

import java.util.*;

@AutoValue
public abstract class NestedCollectionModel {

    public static JsonMapper<NestedCollectionModel> jsonMapper(JacksonAuto jacksonAuto) {
        return new AutoValue_NestedCollectionModel.JsonObjectMapper(jacksonAuto);
    }

    public static NestedCollectionModel create(List<ArrayList<Set<Map<String, List<String>>>>> crazyCollection) {
        return new AutoValue_NestedCollectionModel(crazyCollection);
    }

    @JsonProperty
    public abstract List<ArrayList<Set<Map<String, List<String>>>>> crazyCollection();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return crazyCollection().equals(((NestedCollectionModel)o).crazyCollection());

    }

    @Override
    public int hashCode() {
        return crazyCollection() != null ? crazyCollection().hashCode() : 0;
    }
}
