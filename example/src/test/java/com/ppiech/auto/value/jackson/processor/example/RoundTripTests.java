package com.ppiech.auto.value.jackson.processor.example;

import com.ppiech.auto.value.jackson.JacksonAuto;
import com.ppiech.auto.value.jackson.LoganSquare;
import com.ppiech.auto.value.jackson.processor.example.logansqare.*;
import org.junit.Before;
import org.junit.Test;
import org.json.JSONException;
import org.skyscreamer.jsonassert.*;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RoundTripTests {

  JacksonAuto auto;

  @Before
  public void setUp() {
    auto = new JacksonAuto.Builder()
        .registerJsonMapperFactory(SampleMapperFactory.create())
        .build();

  }

  @Test
  public void stringList() throws JSONException, IOException {
    String json = "[\"test1\",\"test2\",\"test3\",\"test4\"]";

    List<String> list = auto.parseList(json, String.class);
    String reserialized = auto.serialize(list, String.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void intList() throws JSONException, IOException {
    String json = "[1,2,3,4,5,6]";

    List<Integer> list = auto.parseList(json, Integer.class);
    String reserialized = auto.serialize(list, Integer.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void floatList() throws JSONException, IOException {
    String json = "[1.4,2.0,3.0,4.0,5.03,6.2]";

    List<Float> list = auto.parseList(json, Float.class);
    String reserialized = auto.serialize(list, Float.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void booleanList() throws JSONException, IOException {
    String json = "[true,false,false,false,true]";

    List<Boolean> list = auto.parseList(json, Boolean.class);
    String reserialized = auto.serialize(list, Boolean.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void mixedObjectList() throws JSONException, IOException {
    String json =
        "[true,1,1.0,\"test1\",false,1000,1000000000,\"test2\",{\"subkey1\":1,\"subkey2\":\"string\"},[1,2,3]]";

    List<Object> list = auto.parseList(json, Object.class);
    String reserialized = auto.serialize(list, Object.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void stringMap() throws JSONException, IOException {
    String json = "{\"key1\":\"val1\",\"key2\":\"val2\",\"key3\":\"val3\"}";

    Map<String, String> map = new TreeMap<>(auto.parseMap(json, String.class));
    String reserialized = auto.serialize(map, String.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void intMap() throws JSONException, IOException {
    String json = "{\"key1\":1,\"key2\":2,\"key3\":3}";

    Map<String, Integer> map = new TreeMap<>(auto.parseMap(json, Integer.class));
    String reserialized = auto.serialize(map, Integer.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void floatMap() throws JSONException, IOException {
    String json = "{\"key1\":1.4,\"key2\":2.0,\"key3\":3.224}";

    Map<String, Float> map = new TreeMap<>(auto.parseMap(json, Float.class));
    String reserialized = auto.serialize(map, Float.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void booleanMap() throws JSONException, IOException {
    String json = "{\"key1\":true,\"key2\":true,\"key3\":false}";

    Map<String, Boolean> map = new TreeMap<>(auto.parseMap(json, Boolean.class));
    String reserialized = auto.serialize(map, Boolean.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void mixedObjectMap() throws JSONException, IOException {
    String json =
        "{\"key1\":true,\"key2\":1,\"key3\":1.02,\"key4\":1002020,\"key5\":\"test2\",\"key6\":{\"subkey1\":1,\"subkey2\":\"string\"},\"key7\":[1,2,3]}";

    Map<String, Object> map = new TreeMap<>(auto.parseMap(json, Object.class));
    String reserialized = auto.serialize(map, Object.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void simpleObject() throws JSONException, IOException {
    String json =
        "{\"date\":\"2015-02-21T18:45:50.748+0000\",\"object_map\":{\"key2\":2,\"key\":\"value\"},\"string\":\"testString\",\"test_double\":342.0,\"test_double_obj\":345.0,\"test_float\":898.0,\"test_float_obj\":382.0,\"test_int\":32,\"test_int_obj\":323,\"test_long\":932,\"test_long_obj\":3920,\"test_string\":\"anotherTestString\"}";

    SimpleModel simpleModel = auto.parse(json, SimpleModel.class);
    String reserialized = auto.serialize(simpleModel);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void simpleObjectWithNulls() throws JSONException, IOException {
    String json =
        "{\"date\":null,\"string\":\"testString2\",\"test_double\":342.0,\"test_double_obj\":null,\"test_float\":898.0,\"test_float_obj\":null,\"test_int\":32,\"test_int_obj\":null,\"test_long\":932,\"test_long_obj\":null,\"test_string\":null}";

    SimpleModel simpleModel = auto.parse(json, SimpleModel.class);
    String reserialized = auto.serialize(simpleModel);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void externalObject() throws JSONException, IOException {
    String json = "{\"string\":\"I am a very good string.\"}";

    ExternalModel simpleModel = auto.parse(json, ExternalModel.class);
    String reserialized = auto.serialize(simpleModel);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void enumList() throws JSONException, IOException {
    String json = "{\"enum_list\":[\"ONE\",\"TWO\",\"ONE\"],\"enum_map\":{\"key\":\"ONE\"},\"enum_obj\":\"ONE\"}";

    LoganSquare.registerTypeConverter(EnumListModel.TestEnum.class, new EnumListModel.LsEnumTestConverter());
    EnumListModel test = auto.parse(json, EnumListModel.class);
    String serialized = auto.serialize(test);

    JSONAssert.assertEquals(json, serialized, false);
  }

  @Test
  public void simpleObjectList() throws JSONException, IOException {
    String json =
        "[{\"date\":\"2015-02-21T18:45:50.748+0000\",\"string\":\"testString\",\"test_double\":342.0,\"test_double_obj\":345.0,\"test_float\":898.0,\"test_float_obj\":382.0,\"test_int\":32,\"test_int_obj\":323,\"test_long\":932,\"test_long_obj\":3920,\"test_string\":\"anotherTestString\"}," +
            "{\"date\":\"2015-02-22T18:45:50.748+0000\",\"string\":\"testString2\",\"test_double\":342.0,\"test_double_obj\":345.0,\"test_float\":898.0,\"test_float_obj\":382.0,\"test_int\":32,\"test_int_obj\":323,\"test_long\":932,\"test_long_obj\":3920,\"test_string\":\"anotherTestString\"}]";

    List<SimpleModel> simpleModels = auto.parseList(json, SimpleModel.class);
    String reserialized = auto.serialize(simpleModels, SimpleModel.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void simpleObjectListWithNulls() throws JSONException, IOException {
    String json =
        "[{\"date\":\"2015-02-21T18:45:50.748+0000\",\"string\":\"testString\",\"test_double\":342.0,\"test_double_obj\":345.0,\"test_float\":898.0,\"test_float_obj\":382.0,\"test_int\":32,\"test_int_obj\":323,\"test_long\":932,\"test_long_obj\":3920,\"test_string\":\"anotherTestString\"}," +
            "{\"date\":\"2015-02-22T18:45:50.748+0000\",\"string\":\"testString2\",\"test_double\":342.0,\"test_double_obj\":345.0,\"test_float\":898.0,\"test_float_obj\":382.0,\"test_int\":32,\"test_int_obj\":323,\"test_long\":932,\"test_long_obj\":3920,\"test_string\":\"anotherTestString\"}," +
            "{\"date\":null,\"string\":\"testString2\",\"test_double\":342.0,\"test_double_obj\":null,\"test_float\":898.0,\"test_float_obj\":null,\"test_int\":32,\"test_int_obj\":null,\"test_long\":932,\"test_long_obj\":null,\"test_string\":null}," +
            "null," +
            "null]";

    List<SimpleModel> simpleModels = auto.parseList(json, SimpleModel.class);
    String reserialized = auto.serialize(simpleModels, SimpleModel.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void simpleObjectMap() throws JSONException, IOException {
    String json =
        "{\"obj1\":{\"date\":\"2015-02-21T18:45:50.748+0000\",\"string\":\"testString\",\"test_double\":342.0,\"test_double_obj\":345.0,\"test_float\":898.0,\"test_float_obj\":382.0,\"test_int\":32,\"test_int_obj\":323,\"test_long\":932,\"test_long_obj\":3920,\"test_string\":\"anotherTestString\"}," +
            "\"obj2\":{\"date\":\"2015-02-22T18:45:50.748+0000\",\"string\":\"testString2\",\"test_double\":342.0,\"test_double_obj\":345.0,\"test_float\":898.0,\"test_float_obj\":382.0,\"test_int\":32,\"test_int_obj\":323,\"test_long\":932,\"test_long_obj\":3920,\"test_string\":\"anotherTestString\"}}";

    Map<String, SimpleModel> simpleModelMap = new TreeMap<>(auto.parseMap(json, SimpleModel.class));
    String reserialized = auto.serialize(simpleModelMap, SimpleModel.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void simpleObjectMapWithNulls() throws JSONException, IOException {
    String json =
        "{\"obj1\":{\"date\":\"2015-02-21T18:45:50.748+0000\",\"string\":\"testString\",\"test_double\":342.0,\"test_double_obj\":345.0,\"test_float\":898.0,\"test_float_obj\":382.0,\"test_int\":32,\"test_int_obj\":323,\"test_long\":932,\"test_long_obj\":3920,\"test_string\":\"anotherTestString\"}," +
            "\"obj2\":{\"date\":\"2015-02-22T18:45:50.748+0000\",\"string\":\"testString2\",\"test_double\":342.0,\"test_double_obj\":345.0,\"test_float\":898.0,\"test_float_obj\":382.0,\"test_int\":32,\"test_int_obj\":323,\"test_long\":932,\"test_long_obj\":3920,\"test_string\":\"anotherTestString\"}," +
            "\"obj3\":{\"date\":null,\"string\":\"testString2\",\"test_double\":342.0,\"test_double_obj\":null,\"test_float\":898.0,\"test_float_obj\":null,\"test_int\":32,\"test_int_obj\":null,\"test_long\":932,\"test_long_obj\":null,\"test_string\":null}," +
            "\"obj4\":null}";

    Map<String, SimpleModel> simpleModelMap = auto.parseMap(json, SimpleModel.class);
    TreeMap<String, SimpleModel> sortedMap = new TreeMap<>();
    sortedMap.put("obj1", simpleModelMap.get("obj1"));
    sortedMap.put("obj2", simpleModelMap.get("obj2"));
    sortedMap.put("obj3", simpleModelMap.get("obj3"));
    sortedMap.put("obj4", simpleModelMap.get("obj4"));

    String reserialized = auto.serialize(sortedMap, SimpleModel.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @Test
  public void simpleObjectMapWithoutNulls() throws JSONException, IOException {
    String json =
        "{\"obj1\":{\"date\":\"2015-02-21T18:45:50.748+0000\",\"string\":\"testString\",\"test_double\":342.0,\"test_double_obj\":345.0,\"test_float\":898.0,\"test_float_obj\":382.0,\"test_int\":32,\"test_int_obj\":323,\"test_long\":932,\"test_long_obj\":3920,\"test_string\":\"anotherTestString\"}," +
            "\"obj2\":{\"date\":\"2015-02-22T18:45:50.748+0000\",\"string\":\"testString2\",\"test_double\":342.0,\"test_double_obj\":345.0,\"test_float\":898.0,\"test_float_obj\":382.0,\"test_int\":32,\"test_int_obj\":323,\"test_long\":932,\"test_long_obj\":3920,\"test_string\":\"anotherTestString\"}," +
            "\"obj3\":{\"string\":\"testString2\",\"test_double\":342.0,\"test_float\":898.0,\"test_int\":32,\"test_long\":932}," +
            "\"obj4\":null}";

    Map<String, SimpleModelWithoutNullObjects> simpleModelMap = auto.parseMap(
        json, SimpleModelWithoutNullObjects.class);
    TreeMap<String, SimpleModelWithoutNullObjects> sortedMap = new TreeMap<>();
    sortedMap.put("obj1", simpleModelMap.get("obj1"));
    sortedMap.put("obj2", simpleModelMap.get("obj2"));
    sortedMap.put("obj3", simpleModelMap.get("obj3"));
    sortedMap.put("obj4", simpleModelMap.get("obj4"));

    String reserialized = auto.serialize(sortedMap, SimpleModelWithoutNullObjects.class);

    JSONAssert.assertEquals(json, reserialized, false);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void nestedCollection() throws JSONException, IOException {
    NestedCollectionModel model = NestedCollectionModel.create(getStringListMapSetArrayListList());

    String json = null;
    try {
      json = auto.serialize(model);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertNotNull(json);

    NestedCollectionModel reconstructedModel = null;
    try {
      reconstructedModel = auto.parse(json, NestedCollectionModel.class);
    } catch (Exception ignored) {
    }

    // Comparing the json doesn't work so well on arrays, since they could be reordered. We'll compare the output instead.
    assertTrue(model.equals(reconstructedModel));
  }

  private List<String> getStringList() {
    List<String> list = new ArrayList<>();
    list.add(UUID.randomUUID().toString());
    list.add(UUID.randomUUID().toString());
    return list;
  }

  private Map<String, List<String>> getStringListMap() {
    Map<String, List<String>> map = new HashMap<>();
    map.put(UUID.randomUUID().toString(), getStringList());
    map.put(UUID.randomUUID().toString(), getStringList());
    return map;
  }

  private Set<Map<String, List<String>>> getStringListMapSet() {
    Set<Map<String, List<String>>> set = new HashSet<>();
    set.add(getStringListMap());
    set.add(getStringListMap());
    set.add(getStringListMap());
    return set;
  }

  private ArrayList<Set<Map<String, List<String>>>> getStringListMapSetArrayList() {
    ArrayList<Set<Map<String, List<String>>>> list = new ArrayList<>();
    list.add(getStringListMapSet());
    list.add(getStringListMapSet());
    return list;
  }

  private List<ArrayList<Set<Map<String, List<String>>>>> getStringListMapSetArrayListList() {
    List<ArrayList<Set<Map<String, List<String>>>>> list = new ArrayList<>();
    list.add(getStringListMapSetArrayList());
    list.add(getStringListMapSetArrayList());
    return list;
  }
}
