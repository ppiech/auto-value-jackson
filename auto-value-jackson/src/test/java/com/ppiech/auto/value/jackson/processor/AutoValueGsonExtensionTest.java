package com.ppiech.auto.value.jackson.processor;

import com.google.auto.value.processor.AutoValueProcessor;
import com.google.common.collect.ImmutableSet;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.tools.JavaFileObject;
import java.util.Arrays;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static com.ppiech.auto.value.jackson.processor.AutoValueJacksonExtension.COLLECTIONS_DEFAULT_TO_EMPTY;


public class AutoValueGsonExtensionTest {

  private JavaFileObject nullable;


  @Before
  public void setup() {
    nullable = JavaFileObjects.forSourceString("com.ryanharter.auto.value.logansquare.Nullable", ""
        + "package com.ryanharter.auto.value.logansquare;\n"
        + "import java.lang.annotation.Retention;\n"
        + "import java.lang.annotation.Target;\n"
        + "import static java.lang.annotation.ElementType.METHOD;\n"
        + "import static java.lang.annotation.ElementType.PARAMETER;\n"
        + "import static java.lang.annotation.ElementType.FIELD;\n"
        + "import static java.lang.annotation.RetentionPolicy.SOURCE;\n"
        + "@Retention(SOURCE)\n"
        + "@Target({METHOD, PARAMETER, FIELD})\n"
        + "public @interface Nullable {\n"
        + "}");
  }

  @Ignore
  @Test public void address() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Address", "" +
            "package test;\n" +
            "\n" +
            "import JsonMapper;\n" +
            "import JsonObject;\n" +
            "import com.google.auto.value.AutoValue;\n" +
            "import com.ppiech.logansquare.annotation.JsonAutoValueObject;\n" +
            "import com.ppiech.auto.value.jackson.annotation.JsonProperty;\n" +
            "\n" +
            "@AutoValue @JsonAutoValueObject\n" +
            "public abstract class Address {\n" +
            "\n" +
            "    public static Address create(String streetName, String city) {\n" +
            "        return new AutoValue_Address(streetName, city);\n" +
            "    }\n" +
            "\n" +
            "    public static JsonMapper<Address> jsonMapper() {\n" +
            "        return null;\n" +
            "    }\n" +
            "\n" +
            "    @JsonProperty(name=\"street-name\")\n" +
            "    public abstract String streetName();\n" +
            "\n" +
            "    public abstract String city();\n" +
            "    public abstract Integer number();\n" +
            "}\n");

    JavaFileObject expected = JavaFileObjects.forSourceString("test/AutoValue_Address", "" +
            "package com.ppiech.auto.value.logansquare.example;\n" +
            "\n" +
            "import JsonMapper;\n" +
            "import LoganSquare;\n" +
            "import com.fasterxml.jackson.jackson.JsonGenerator;\n" +
            "import com.fasterxml.jackson.jackson.JsonParser;\n" +
            "import com.fasterxml.jackson.jackson.JsonToken;\n" +
            "import java.io.IOException;\n" +
            "import java.lang.Override;\n" +
            "import java.lang.String;\n" +
            "\n" +
            "final class AutoValue_Address extends $AutoValue_Address {\n" +
            "  AutoValue_Address(String streetName, String city) {\n" +
            "    super(streetName, city);\n" +
            "  }\n" +
            "\n" +
            "  public static final class JsonObjectMapper extends JsonMapper<Address> {\n" +
            "    private final JsonMapper<String> streetNameMapper;\n" +
            "    private final JsonMapper<String> cityMapper;\n" +
            "    private String defaultStreetName = null;\n" +
            "    private String defaultCity = null;\n" +
            "    public JsonObjectMapper() {\n" +
            "      this.streetNameMapper = LoganSquare.mapperFor(String.class);\n" +
            "      this.cityMapper = LoganSquare.mapperFor(String.class);\n" +
            "    }\n" +
            "    public JsonObjectMapper setDefaultStreetName(String defaultStreetName) {\n" +
            "      this.defaultStreetName = defaultStreetName;\n" +
            "      return this;\n" +
            "    }\n" +
            "    public JsonObjectMapper setDefaultCity(String defaultCity) {\n" +
            "      this.defaultCity = defaultCity;\n" +
            "      return this;\n" +
            "    }\n" +
            "    @Override\n" +
            "    public void serialize(Address object, JsonGenerator jsonGenerator, boolean writeStartAndEnd)\n" +
            "        throws IOException {\n" +
            "      if (writeStartAndEnd) {\n" +
            "        jsonGenerator.writeStartObject();\n" +
            "      }\n" +
            "      if (object == null) {\n" +
            "        jsonGenerator.writeNull();\n" +
            "        return;\n" +
            "      }\n" +
            "      if (object.streetName() != null) {\n" +
            "        jsonGenerator.writeFieldName(\"street-name\");\n" +
            "        streetNameMapper.serialize(object.streetName(), jsonGenerator, true);\n" +
            "      }\n" +
            "      if (object.city() != null) {\n" +
            "        jsonGenerator.writeFieldName(\"city\");\n" +
            "        cityMapper.serialize(object.city(), jsonGenerator, true);\n" +
            "      }\n" +
            "      if (writeStartAndEnd) {\n" +
            "        jsonGenerator.writeEndObject();\n" +
            "      }\n" +
            "    }\n" +
            "    @Override\n" +
            "    public Address parse(JsonParser jsonParser) throws IOException {\n" +
            "      if (jsonParser.getCurrentToken() == null) {\n" +
            "        jsonParser.nextToken();\n" +
            "      }\n" +
            "      if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {\n" +
            "        jsonParser.skipChildren();\n" +
            "        return null;\n" +
            "      }\n" +
            "      String streetName = this.defaultStreetName;\n" +
            "      String city = this.defaultCity;\n" +
            "      while (jsonParser.nextToken() != JsonToken.END_OBJECT) {\n" +
            "        String _name = jsonParser.getCurrentName();\n" +
            "        jsonParser.nextToken();\n" +
            "        switch (_name) {\n" +
            "          case \"street-name\":\n" +
            "           {\n" +
            "            streetName = streetNameMapper.parse(jsonParser);\n" +
            "            break;\n" +
            "          }\n" +
            "          case \"city\":\n" +
            "           {\n" +
            "            city = cityMapper.parse(jsonParser);\n" +
            "            break;\n" +
            "          }\n" +
            "        }\n" +
            "        jsonParser.skipChildren();\n" +
            "      }\n" +
            "      return new AutoValue_Address(streetName, city);\n" +
            "    }\n" +
            "    @Override\n" +
            "    public void parseField(Address instance, String fieldName, JsonParser jsonParser) throws\n" +
            "        IOException {\n" +
            "    }\n" +
            "  }\n" +
            "}\n");

    assertAbout(javaSources())
            .that(Arrays.asList(nullable, source))
            .withCompilerOptions("-A" + COLLECTIONS_DEFAULT_TO_EMPTY + "=true")
            .processedWith(new AutoValueProcessor())
            .compilesWithoutError();
  }

  @Ignore
  @Test public void simple() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import com.google.logansquare.annotations.SerializedName;\n"
        + "import com.ryanharter.auto.value.logansquare.JsonObjectMapper;\n"
        + "import com.ryanharter.auto.value.logansquare.Nullable;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import com.google.common.collect.ImmutableMap;\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "import com.google.logansquare.TypeAdapterFactory;\n"
        + "import com.google.logansquare.reflect.TypeToken;\n"
        + "import com.google.logansquare.stream.JsonWriter;\n"
        + "import com.google.logansquare.stream.JsonReader;\n"
        + "import java.io.IOException;\n"
        + "import java.util.List;\n"
        + "import java.util.Map;\n"
        + "import java.util.Set;\n"
        + "@AutoValue public abstract class Test {\n"
        + "  public static TypeAdapter<Test> typeAdapter(Gson logansquare) {\n"
        + "    return new AutoValue_Test.JsonObjectMapper(logansquare);\n"
        + "  }\n"
        // Reference type
        + "public abstract String a();\n"
        // Array type
        + "public abstract int[] b();\n"
        // Primitive type
        + "public abstract int c();\n"
        // SerializedName
        + "@SerializedName(\"_D\") public abstract String d();\n"
        // Nullable type
        + "@Nullable abstract String e();\n"
        // Parametrized type, multiple parameters
        + "public abstract ImmutableMap<String, Number> f();\n"
        // Parametrized type, single parameter
        + "public abstract Set<String> g();\n"
        // Nested parameterized type
        + "public abstract Map<String, Set<String>> h();\n"
        // SerializedName with alternate
        + "@SerializedName(value = \"_I\", alternate = {\"_I_1\", \"_I_2\"}) public abstract String i();\n"
        // Nullable collection type
        + "@Nullable public abstract List<String> j();\n"
        // Custom adapter
        + "@JsonObjectMapper(TestTypeAdapter.class) public abstract String k();\n"
        // Custom adapter with generics
        + "@JsonObjectMapper(TestListTypeAdapter.class) public abstract List<String> l();\n"
        // Custom adapter factory
        + "@JsonObjectMapper(TestTypeAdapterFactory.class) public abstract String m();\n"
        // Custom adapter factory with generics
        + "@JsonObjectMapper(TestTypeAdapterFactory.class) public abstract List<String> n();\n"
        // Deeply nested parameterized type
        + "public abstract Map<String, Map<String, Map<String, Map<String, Map<String, String>>>>> o();\n" +
        "  @AutoValue.Builder public static abstract class Builder {\n" +
        "    public abstract Builder a(String a);\n" +
        "    public abstract Builder b(int[] b);\n" +
        "    public abstract Builder c(int c);\n" +
        "    public abstract Builder d(String d);\n" +
        "    public abstract Builder e(String e);\n" +
        "    public abstract Builder f(ImmutableMap<String, Number> f);\n" +
        "    public abstract Builder g(Set<String> g);\n" +
        "    public abstract Builder h(Map<String, Set<String>> h);\n" +
        "    public abstract Builder i(String i);\n" +
        "    public abstract Builder j(List<String> j);\n" +
        "    public abstract Builder k(String k);\n" +
        "    public abstract Builder l(List<String> l);\n" +
        "    public abstract Builder m(String m);\n" +
        "    public abstract Builder n(List<String> n);\n" +
        "    public abstract Builder o(Map<String, Map<String, Map<String, Map<String, Map<String, String>>>>> o);\n" +
        "    public abstract Test build();\n" +
        "  }\n" +
        "  public static class TestTypeAdapter extends TypeAdapter<String> {\n" +
        "    @Override public void write(JsonWriter out, String value) throws IOException {}\n" +
        "    @Override public String read(JsonReader in) throws IOException { return null; }\n" +
        "  }\n" +
        "  public static class TestListTypeAdapter extends TypeAdapter<List<String>> {\n" +
        "    @Override public void write(JsonWriter out, List<String> value) throws IOException {}\n" +
        "    @Override public List<String> read(JsonReader in) throws IOException { return null; }\n" +
        "  }\n" +
        "  public static class TestTypeAdapterFactory implements TypeAdapterFactory {\n" +
        "    @Override public <T> TypeAdapter<T> create(Gson logansquare, TypeToken<T> type) { return null; }\n" +
        "  }\n"
        + "}\n"
    );

    JavaFileObject expected = JavaFileObjects.forSourceString("test/AutoValue_Test", ""
        + "package test;\n"
        + "\n"
        + "import com.google.common.collect.ImmutableMap;\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "import com.google.logansquare.reflect.TypeToken;\n"
        + "import com.google.logansquare.stream.JsonReader;\n"
        + "import com.google.logansquare.stream.JsonToken;\n"
        + "import com.google.logansquare.stream.JsonWriter;\n"
        + "import java.io.IOException;\n"
        + "import java.lang.Integer;\n"
        + "import java.lang.Number;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.String;\n"
        + "import java.util.Collections;\n"
        + "import java.util.List;\n"
        + "import java.util.Map;\n"
        + "import java.util.Set;\n"
        + "\n"
        + "final class AutoValue_Test extends $AutoValue_Test {\n"
        + "  AutoValue_Test(String a, int[] b, int c, String d, String e, ImmutableMap<String, Number> f, Set<String> g, Map<String, Set<String>> h, String i, List<String> j, String k, List<String> l, String m, List<String> n, Map<String, Map<String, Map<String, Map<String, Map<String, String>>>>> o) {\n"
        + "    super(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);\n"
        + "  }\n"
        + "\n"
        + "  public static final class JsonObjectMapper extends TypeAdapter<Test> {\n"
        + "    private final TypeAdapter<String> aAdapter;\n"
        + "    private final TypeAdapter<int[]> bAdapter;\n"
        + "    private final TypeAdapter<Integer> cAdapter;\n"
        + "    private final TypeAdapter<String> dAdapter;\n"
        + "    private final TypeAdapter<String> eAdapter;\n"
        + "    private final TypeAdapter<ImmutableMap<String, Number>> fAdapter;\n"
        + "    private final TypeAdapter<Set<String>> gAdapter;\n"
        + "    private final TypeAdapter<Map<String, Set<String>>> hAdapter;\n"
        + "    private final TypeAdapter<String> iAdapter;\n"
        + "    private final TypeAdapter<List<String>> jAdapter;\n"
        + "    private final TypeAdapter<String> kAdapter;\n"
        + "    private final TypeAdapter<List<String>> lAdapter;\n"
        + "    private final TypeAdapter<String> mAdapter;\n"
        + "    private final TypeAdapter<List<String>> nAdapter;\n"
        + "    private final TypeAdapter<Map<String, Map<String, Map<String, Map<String, Map<String, String>>>>>> oAdapter;\n"
        + "    private String defaultA = null;\n"
        + "    private int[] defaultB = null;\n"
        + "    private int defaultC = 0;\n"
        + "    private String defaultD = null;\n"
        + "    private String defaultE = null;\n"
        + "    private ImmutableMap<String, Number> defaultF = ImmutableMap.of();\n"
        + "    private Set<String> defaultG = Collections.emptySet();\n"
        + "    private Map<String, Set<String>> defaultH = Collections.emptyMap();\n"
        + "    private String defaultI = null;\n"
        + "    private List<String> defaultJ = null;\n"
        + "    private String defaultK = null;\n"
        + "    private List<String> defaultL = Collections.emptyList();\n"
        + "    private String defaultM = null;\n"
        + "    private List<String> defaultN = Collections.emptyList();\n"
        + "    private Map<String, Map<String, Map<String, Map<String, Map<String, String>>>>> defaultO = Collections.emptyMap();\n"
        + "    public JsonObjectMapper(Gson logansquare) {\n"
        + "      this.aAdapter = logansquare.getAdapter(String.class);\n"
        + "      this.bAdapter = logansquare.getAdapter(int[].class);\n"
        + "      this.cAdapter = logansquare.getAdapter(Integer.class);\n"
        + "      this.dAdapter = logansquare.getAdapter(String.class);\n"
        + "      this.eAdapter = logansquare.getAdapter(String.class);\n"
        + "      this.fAdapter = (TypeAdapter<ImmutableMap<String, Number>>) logansquare.getAdapter(TypeToken.getParameterized(ImmutableMap.class, String.class, Number.class));\n"
        + "      this.gAdapter = (TypeAdapter<Set<String>>) logansquare.getAdapter(TypeToken.getParameterized(Set.class, String.class));\n"
        + "      this.hAdapter = (TypeAdapter<Map<String, Set<String>>>) logansquare.getAdapter(TypeToken.getParameterized(Map.class, String.class, TypeToken.getParameterized(Set.class, String.class).getType()));\n"
        + "      this.iAdapter = logansquare.getAdapter(String.class);\n"
        + "      this.jAdapter = (TypeAdapter<List<String>>) logansquare.getAdapter(TypeToken.getParameterized(List.class, String.class));\n"
        + "      this.kAdapter = new Test.TestTypeAdapter();\n"
        + "      this.lAdapter = new Test.TestListTypeAdapter();\n"
        + "      this.mAdapter = new Test.TestTypeAdapterFactory().create(logansquare, TypeToken.get(String.class));\n"
        + "      this.nAdapter = (TypeAdapter<List<String>>) new Test.TestTypeAdapterFactory().create(logansquare, TypeToken.getParameterized(List.class, String.class));\n"
        + "      this.oAdapter = (TypeAdapter<Map<String, Map<String, Map<String, Map<String, Map<String, String>>>>>>) logansquare.getAdapter("
        + "          TypeToken.getParameterized(Map.class, String.class,"
        + "              TypeToken.getParameterized(Map.class, String.class,"
        + "                  TypeToken.getParameterized(Map.class, String.class,"
        + "                      TypeToken.getParameterized(Map.class, String.class,"
        + "                          TypeToken.getParameterized(Map.class, String.class, String.class).getType()"
        + "                      ).getType()"
        + "                  ).getType()"
        + "              ).getType()"
        + "          ));\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultA(String defaultA) {\n"
        + "      this.defaultA = defaultA;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultB(int[] defaultB) {\n"
        + "      this.defaultB = defaultB;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultC(int defaultC) {\n"
        + "      this.defaultC = defaultC;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultD(String defaultD) {\n"
        + "      this.defaultD = defaultD;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultE(String defaultE) {\n"
        + "      this.defaultE = defaultE;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultF(ImmutableMap<String, Number> defaultF) {\n"
        + "      this.defaultF = defaultF;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultG(Set<String> defaultG) {\n"
        + "      this.defaultG = defaultG;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultH(Map<String, Set<String>> defaultH) {\n"
        + "      this.defaultH = defaultH;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultI(String defaultI) {\n"
        + "      this.defaultI = defaultI;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultJ(List<String> defaultJ) {\n"
        + "      this.defaultJ = defaultJ;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultK(String defaultK) {\n"
        + "      this.defaultK = defaultK;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultL(List<String> defaultL) {\n"
        + "      this.defaultL = defaultL;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultM(String defaultM) {\n"
        + "      this.defaultM = defaultM;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultN(List<String> defaultN) {\n"
        + "      this.defaultN = defaultN;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultO(Map<String, Map<String, Map<String, Map<String, Map<String, String>>>>> defaultO) {\n"
        + "      this.defaultO = defaultO;\n"
        + "      return this;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void write(JsonWriter jsonWriter, Test object) throws IOException {\n"
        + "      if (object == null) {\n"
        + "        jsonWriter.nullValue();\n"
        + "        return;\n"
        + "      }\n"
        + "      jsonWriter.beginObject();\n"
        + "      jsonWriter.name(\"a\");\n"
        + "      aAdapter.write(jsonWriter, object.a());\n"
        + "      jsonWriter.name(\"b\");\n"
        + "      bAdapter.write(jsonWriter, object.b());\n"
        + "      jsonWriter.name(\"c\");\n"
        + "      cAdapter.write(jsonWriter, object.c());\n"
        + "      jsonWriter.name(\"_D\");\n"
        + "      dAdapter.write(jsonWriter, object.d());\n"
        + "      jsonWriter.name(\"e\");\n"
        + "      eAdapter.write(jsonWriter, object.e());\n"
        + "      jsonWriter.name(\"f\");\n"
        + "      fAdapter.write(jsonWriter, object.f());\n"
        + "      jsonWriter.name(\"g\");\n"
        + "      gAdapter.write(jsonWriter, object.g());\n"
        + "      jsonWriter.name(\"h\");\n"
        + "      hAdapter.write(jsonWriter, object.h());\n"
        + "      jsonWriter.name(\"_I\");\n"
        + "      iAdapter.write(jsonWriter, object.i());\n"
        + "      jsonWriter.name(\"j\");\n"
        + "      jAdapter.write(jsonWriter, object.j());\n"
        + "      jsonWriter.name(\"k\");\n"
        + "      kAdapter.write(jsonWriter, object.k());\n"
        + "      jsonWriter.name(\"l\");\n"
        + "      lAdapter.write(jsonWriter, object.l());\n"
        + "      jsonWriter.name(\"m\");\n"
        + "      mAdapter.write(jsonWriter, object.m());\n"
        + "      jsonWriter.name(\"n\");\n"
        + "      nAdapter.write(jsonWriter, object.n());\n"
        + "      jsonWriter.name(\"o\");\n"
        + "      oAdapter.write(jsonWriter, object.o());\n"
        + "      jsonWriter.endObject();\n"
        + "    }\n"
        + "    @Override\n"
        + "    public Test read(JsonReader jsonReader) throws IOException {\n"
        + "      if (jsonReader.peek() == JsonToken.NULL) {\n"
        + "        jsonReader.nextNull();\n"
        + "        return null;\n"
        + "      }\n"
        + "      jsonReader.beginObject();\n"
        + "      String a = this.defaultA;\n"
        + "      int[] b = this.defaultB;\n"
        + "      int c = this.defaultC;\n"
        + "      String d = this.defaultD;\n"
        + "      String e = this.defaultE;\n"
        + "      ImmutableMap<String, Number> f = this.defaultF;\n"
        + "      Set<String> g = this.defaultG;\n"
        + "      Map<String, Set<String>> h = this.defaultH;\n"
        + "      String i = this.defaultI;\n"
        + "      List<String> j = this.defaultJ;\n"
        + "      String k = this.defaultK;\n"
        + "      List<String> l = this.defaultL;\n"
        + "      String m = this.defaultM;\n"
        + "      List<String> n = this.defaultN;\n"
        + "      Map<String, Map<String, Map<String, Map<String, Map<String, String>>>>> o = this.defaultO;\n"
        + "      while (jsonReader.hasNext()) {\n"
        + "        String _name = jsonReader.nextName();\n"
        + "        if (jsonReader.peek() == JsonToken.NULL) {\n"
        + "          jsonReader.nextNull();\n"
        + "          continue;\n"
        + "        }\n"
        + "        switch (_name) {\n"
        + "          case \"a\": {\n"
        + "            a = aAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"b\": {\n"
        + "            b = bAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"c\": {\n"
        + "            c = cAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"_D\": {\n"
        + "            d = dAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"e\": {\n"
        + "            e = eAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"f\": {\n"
        + "            f = fAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"g\": {\n"
        + "            g = gAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"h\": {\n"
        + "            h = hAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"_I_1\":\n"
        + "          case \"_I_2\":\n"
        + "          case \"_I\": {\n"
        + "            i = iAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"j\": {\n"
        + "            j = jAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"k\": {\n"
        + "            k = kAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"l\": {\n"
        + "            l = lAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"m\": {\n"
        + "            m = mAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"n\": {\n"
        + "            n = nAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"o\": {\n"
        + "            o = oAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          default: {\n"
        + "            jsonReader.skipValue();\n"
        + "          }\n"
        + "        }\n"
        + "      }\n"
        + "      jsonReader.endObject();\n"
        + "      return new AutoValue_Test(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources())
        .that(Arrays.asList(nullable, source))
        .withCompilerOptions("-A" + COLLECTIONS_DEFAULT_TO_EMPTY + "=true")
        .processedWith(new AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Ignore
  @Test public void simpleNoEmpty() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import com.google.logansquare.annotations.SerializedName;\n"
        + "import com.ryanharter.auto.value.logansquare.JsonObjectMapper;\n"
        + "import com.ryanharter.auto.value.logansquare.Nullable;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import com.google.common.collect.ImmutableMap;\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "import com.google.logansquare.TypeAdapterFactory;\n"
        + "import com.google.logansquare.reflect.TypeToken;\n"
        + "import com.google.logansquare.stream.JsonWriter;\n"
        + "import com.google.logansquare.stream.JsonReader;\n"
        + "import java.io.IOException;\n"
        + "import java.util.List;\n"
        + "import java.util.Map;\n"
        + "import java.util.Set;\n"
        + "@AutoValue public abstract class Test {\n"
        + "  public static TypeAdapter<Test> typeAdapter(Gson logansquare) {\n"
        + "    return new AutoValue_Test.JsonObjectMapper(logansquare);\n"
        + "  }\n"
        // Reference type
        + "public abstract String a();\n"
        // Array type
        + "public abstract int[] b();\n"
        // Primitive type
        + "public abstract int c();\n"
        // SerializedName
        + "@SerializedName(\"_D\") public abstract String d();\n"
        // Nullable type
        + "@Nullable abstract String e();\n"
        // Parametrized type, multiple parameters
        + "public abstract ImmutableMap<String, Number> f();\n"
        // Parametrized type, single parameter
        + "public abstract Set<String> g();\n"
        // Nested parameterized type
        + "public abstract Map<String, Set<String>> h();\n"
        // SerializedName with alternate
        + "@SerializedName(value = \"_I\", alternate = {\"_I_1\", \"_I_2\"}) public abstract String i();\n"
        // Nullable collection type
        + "@Nullable public abstract List<String> j();\n"
        // Custom adapter
        + "@JsonObjectMapper(TestTypeAdapter.class) public abstract String k();\n"
        // Custom adapter with generics
        + "@JsonObjectMapper(TestListTypeAdapter.class) public abstract List<String> l();\n"
        // Custom adapter factory
        + "@JsonObjectMapper(TestTypeAdapterFactory.class) public abstract String m();\n"
        // Custom adapter factory with generics
        + "@JsonObjectMapper(TestTypeAdapterFactory.class) public abstract List<String> n();\n" +
        "  @AutoValue.Builder public static abstract class Builder {\n" +
        "    public abstract Builder a(String a);\n" +
        "    public abstract Builder b(int[] b);\n" +
        "    public abstract Builder c(int c);\n" +
        "    public abstract Builder d(String d);\n" +
        "    public abstract Builder e(String e);\n" +
        "    public abstract Builder f(ImmutableMap<String, Number> f);\n" +
        "    public abstract Builder g(Set<String> g);\n" +
        "    public abstract Builder h(Map<String, Set<String>> h);\n" +
        "    public abstract Builder i(String i);\n" +
        "    public abstract Builder j(List<String> j);\n" +
        "    public abstract Builder k(String k);\n" +
        "    public abstract Builder l(List<String> l);\n" +
        "    public abstract Builder m(String m);\n" +
        "    public abstract Builder n(List<String> n);\n" +
        "    public abstract Test build();\n" +
        "  }\n" +
        "  public static class TestTypeAdapter extends TypeAdapter<String> {\n" +
        "    @Override public void write(JsonWriter out, String value) throws IOException {}\n" +
        "    @Override public String read(JsonReader in) throws IOException { return null; }\n" +
        "  }\n" +
        "  public static class TestListTypeAdapter extends TypeAdapter<List<String>> {\n" +
        "    @Override public void write(JsonWriter out, List<String> value) throws IOException {}\n" +
        "    @Override public List<String> read(JsonReader in) throws IOException { return null; }\n" +
        "  }\n" +
        "  public static class TestTypeAdapterFactory implements TypeAdapterFactory {\n" +
        "    @Override public <T> TypeAdapter<T> create(Gson logansquare, TypeToken<T> type) { return null; }\n" +
        "  }\n"
        + "}\n"
    );

    JavaFileObject expected = JavaFileObjects.forSourceString("test/AutoValue_Test", ""
        + "package test;\n"
        + "\n"
        + "import com.google.common.collect.ImmutableMap;\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "import com.google.logansquare.reflect.TypeToken;\n"
        + "import com.google.logansquare.stream.JsonReader;\n"
        + "import com.google.logansquare.stream.JsonToken;\n"
        + "import com.google.logansquare.stream.JsonWriter;\n"
        + "import java.io.IOException;\n"
        + "import java.lang.Integer;\n"
        + "import java.lang.Number;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.String;\n"
        + "import java.util.List;\n"
        + "import java.util.Map;\n"
        + "import java.util.Set;\n"
        + "\n"
        + "final class AutoValue_Test extends $AutoValue_Test {\n"
        + "  AutoValue_Test(String a, int[] b, int c, String d, String e, ImmutableMap<String, Number> f, Set<String> g, Map<String, Set<String>> h, String i, List<String> j, String k, List<String> l, String m, List<String> n) {\n"
        + "    super(a, b, c, d, e, f, g, h, i, j, k, l, m, n);\n"
        + "  }\n"
        + "\n"
        + "  public static final class JsonObjectMapper extends TypeAdapter<Test> {\n"
        + "    private final TypeAdapter<String> aAdapter;\n"
        + "    private final TypeAdapter<int[]> bAdapter;\n"
        + "    private final TypeAdapter<Integer> cAdapter;\n"
        + "    private final TypeAdapter<String> dAdapter;\n"
        + "    private final TypeAdapter<String> eAdapter;\n"
        + "    private final TypeAdapter<ImmutableMap<String, Number>> fAdapter;\n"
        + "    private final TypeAdapter<Set<String>> gAdapter;\n"
        + "    private final TypeAdapter<Map<String, Set<String>>> hAdapter;\n"
        + "    private final TypeAdapter<String> iAdapter;\n"
        + "    private final TypeAdapter<List<String>> jAdapter;\n"
        + "    private final TypeAdapter<String> kAdapter;\n"
        + "    private final TypeAdapter<List<String>> lAdapter;\n"
        + "    private final TypeAdapter<String> mAdapter;\n"
        + "    private final TypeAdapter<List<String>> nAdapter;\n"
        + "    private String defaultA = null;\n"
        + "    private int[] defaultB = null;\n"
        + "    private int defaultC = 0;\n"
        + "    private String defaultD = null;\n"
        + "    private String defaultE = null;\n"
        + "    private ImmutableMap<String, Number> defaultF = null;\n"
        + "    private Set<String> defaultG = null;\n"
        + "    private Map<String, Set<String>> defaultH = null;\n"
        + "    private String defaultI = null;\n"
        + "    private List<String> defaultJ = null;\n"
        + "    private String defaultK = null;\n"
        + "    private List<String> defaultL = null;\n"
        + "    private String defaultM = null;\n"
        + "    private List<String> defaultN = null;\n"
        + "    public JsonObjectMapper(Gson logansquare) {\n"
        + "      this.aAdapter = logansquare.getAdapter(String.class);\n"
        + "      this.bAdapter = logansquare.getAdapter(int[].class);\n"
        + "      this.cAdapter = logansquare.getAdapter(Integer.class);\n"
        + "      this.dAdapter = logansquare.getAdapter(String.class);\n"
        + "      this.eAdapter = logansquare.getAdapter(String.class);\n"
        + "      this.fAdapter = (TypeAdapter<ImmutableMap<String, Number>>) logansquare.getAdapter(TypeToken.getParameterized(ImmutableMap.class, String.class, Number.class));\n"
        + "      this.gAdapter = (TypeAdapter<Set<String>>) logansquare.getAdapter(TypeToken.getParameterized(Set.class, String.class));\n"
        + "      this.hAdapter = (TypeAdapter<Map<String, Set<String>>>) logansquare.getAdapter(TypeToken.getParameterized(Map.class, String.class, TypeToken.getParameterized(Set.class, String.class).getType()));\n"
        + "      this.iAdapter = logansquare.getAdapter(String.class);\n"
        + "      this.jAdapter = (TypeAdapter<List<String>>) logansquare.getAdapter(TypeToken.getParameterized(List.class, String.class));\n"
        + "      this.kAdapter = new Test.TestTypeAdapter();\n"
        + "      this.lAdapter = new Test.TestListTypeAdapter();\n"
        + "      this.mAdapter = new Test.TestTypeAdapterFactory().create(logansquare, TypeToken.get(String.class));\n"
        + "      this.nAdapter = (TypeAdapter<List<String>>) new Test.TestTypeAdapterFactory().create(logansquare, TypeToken.getParameterized(List.class, String.class));\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultA(String defaultA) {\n"
        + "      this.defaultA = defaultA;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultB(int[] defaultB) {\n"
        + "      this.defaultB = defaultB;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultC(int defaultC) {\n"
        + "      this.defaultC = defaultC;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultD(String defaultD) {\n"
        + "      this.defaultD = defaultD;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultE(String defaultE) {\n"
        + "      this.defaultE = defaultE;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultF(ImmutableMap<String, Number> defaultF) {\n"
        + "      this.defaultF = defaultF;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultG(Set<String> defaultG) {\n"
        + "      this.defaultG = defaultG;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultH(Map<String, Set<String>> defaultH) {\n"
        + "      this.defaultH = defaultH;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultI(String defaultI) {\n"
        + "      this.defaultI = defaultI;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultJ(List<String> defaultJ) {\n"
        + "      this.defaultJ = defaultJ;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultK(String defaultK) {\n"
        + "      this.defaultK = defaultK;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultL(List<String> defaultL) {\n"
        + "      this.defaultL = defaultL;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultM(String defaultM) {\n"
        + "      this.defaultM = defaultM;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultN(List<String> defaultN) {\n"
        + "      this.defaultN = defaultN;\n"
        + "      return this;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void write(JsonWriter jsonWriter, Test object) throws IOException {\n"
        + "      if (object == null) {\n"
        + "        jsonWriter.nullValue();\n"
        + "        return;\n"
        + "      }\n"
        + "      jsonWriter.beginObject();\n"
        + "      jsonWriter.name(\"a\");\n"
        + "      aAdapter.write(jsonWriter, object.a());\n"
        + "      jsonWriter.name(\"b\");\n"
        + "      bAdapter.write(jsonWriter, object.b());\n"
        + "      jsonWriter.name(\"c\");\n"
        + "      cAdapter.write(jsonWriter, object.c());\n"
        + "      jsonWriter.name(\"_D\");\n"
        + "      dAdapter.write(jsonWriter, object.d());\n"
        + "      jsonWriter.name(\"e\");\n"
        + "      eAdapter.write(jsonWriter, object.e());\n"
        + "      jsonWriter.name(\"f\");\n"
        + "      fAdapter.write(jsonWriter, object.f());\n"
        + "      jsonWriter.name(\"g\");\n"
        + "      gAdapter.write(jsonWriter, object.g());\n"
        + "      jsonWriter.name(\"h\");\n"
        + "      hAdapter.write(jsonWriter, object.h());\n"
        + "      jsonWriter.name(\"_I\");\n"
        + "      iAdapter.write(jsonWriter, object.i());\n"
        + "      jsonWriter.name(\"j\");\n"
        + "      jAdapter.write(jsonWriter, object.j());\n"
        + "      jsonWriter.name(\"k\");\n"
        + "      kAdapter.write(jsonWriter, object.k());\n"
        + "      jsonWriter.name(\"l\");\n"
        + "      lAdapter.write(jsonWriter, object.l());\n"
        + "      jsonWriter.name(\"m\");\n"
        + "      mAdapter.write(jsonWriter, object.m());\n"
        + "      jsonWriter.name(\"n\");\n"
        + "      nAdapter.write(jsonWriter, object.n());\n"
        + "      jsonWriter.endObject();\n"
        + "    }\n"
        + "    @Override\n"
        + "    public Test read(JsonReader jsonReader) throws IOException {\n"
        + "      if (jsonReader.peek() == JsonToken.NULL) {\n"
        + "        jsonReader.nextNull();\n"
        + "        return null;\n"
        + "      }\n"
        + "      jsonReader.beginObject();\n"
        + "      String a = this.defaultA;\n"
        + "      int[] b = this.defaultB;\n"
        + "      int c = this.defaultC;\n"
        + "      String d = this.defaultD;\n"
        + "      String e = this.defaultE;\n"
        + "      ImmutableMap<String, Number> f = this.defaultF;\n"
        + "      Set<String> g = this.defaultG;\n"
        + "      Map<String, Set<String>> h = this.defaultH;\n"
        + "      String i = this.defaultI;\n"
        + "      List<String> j = this.defaultJ;\n"
        + "      String k = this.defaultK;\n"
        + "      List<String> l = this.defaultL;\n"
        + "      String m = this.defaultM;\n"
        + "      List<String> n = this.defaultN;\n"
        + "      while (jsonReader.hasNext()) {\n"
        + "        String _name = jsonReader.nextName();\n"
        + "        if (jsonReader.peek() == JsonToken.NULL) {\n"
        + "          jsonReader.nextNull();\n"
        + "          continue;\n"
        + "        }"
        + "        switch (_name) {\n"
        + "          case \"a\": {\n"
        + "            a = aAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"b\": {\n"
        + "            b = bAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"c\": {\n"
        + "            c = cAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"_D\": {\n"
        + "            d = dAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"e\": {\n"
        + "            e = eAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"f\": {\n"
        + "            f = fAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"g\": {\n"
        + "            g = gAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"h\": {\n"
        + "            h = hAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"_I_1\":\n"
        + "          case \"_I_2\":\n"
        + "          case \"_I\": {\n"
        + "            i = iAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"j\": {\n"
        + "            j = jAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"k\": {\n"
        + "            k = kAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"l\": {\n"
        + "            l = lAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"m\": {\n"
        + "            m = mAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"n\": {\n"
        + "            n = nAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          default: {\n"
        + "            jsonReader.skipValue();\n"
        + "          }\n"
        + "        }\n"
        + "      }\n"
        + "      jsonReader.endObject();\n"
        + "      return new AutoValue_Test(a, b, c, d, e, f, g, h, i, j, k, l, m, n);\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources())
        .that(Arrays.asList(nullable, source))
        .processedWith(new AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Ignore
  @Test public void propertyMethodReferencedWithPrefix() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "@AutoValue public abstract class Test {\n"
        + "  public static TypeAdapter<Test> typeAdapter(Gson logansquare) {\n"
        + "    return new AutoValue_Test.JsonObjectMapper(logansquare);\n"
        + "  }\n"
        + "  public abstract String getName();\n"
        + "  public abstract boolean isAwesome();\n"
        + "}"
    );
    JavaFileObject expected = JavaFileObjects.forSourceString("test/AutoValue_Test", ""
        + "package test;\n"
        + "\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "import com.google.logansquare.stream.JsonReader;\n"
        + "import com.google.logansquare.stream.JsonToken;\n"
        + "import com.google.logansquare.stream.JsonWriter;\n"
        + "import java.io.IOException;\n"
        + "import java.lang.Boolean;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.String;\n"
        + "\n"
        + "final class AutoValue_Test extends $AutoValue_Test {\n"
        + "  AutoValue_Test(String name, boolean awesome) {\n"
        + "    super(name, awesome);\n"
        + "  }\n"
        + "\n"
        + "  public static final class JsonObjectMapper extends TypeAdapter<Test> {\n"
        + "    private final TypeAdapter<String> nameAdapter;\n"
        + "    private final TypeAdapter<Boolean> awesomeAdapter;\n"
        + "    private String defaultName = null;\n"
        + "    private boolean defaultAwesome = false;\n"
        + "    public TestTypeAdapter(Gson logansquare) {\n"
        + "      this.nameAdapter = logansquare.getAdapter(String.class);\n"
        + "      this.awesomeAdapter = logansquare.getAdapter(Boolean.class);\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultName(String defaultName) {\n"
        + "      this.defaultName = defaultName;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultAwesome(boolean defaultAwesome) {\n"
        + "      this.defaultAwesome = defaultAwesome;\n"
        + "      return this;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void write(JsonWriter jsonWriter, Test object) throws IOException {\n"
        + "      if (object == null) {\n"
        + "        jsonWriter.nullValue();\n"
        + "        return;\n"
        + "      }\n"
        + "      jsonWriter.beginObject();\n"
        + "      jsonWriter.name(\"name\");\n"
        + "      nameAdapter.write(jsonWriter, object.getName());\n"
        + "      jsonWriter.name(\"awesome\");\n"
        + "      awesomeAdapter.write(jsonWriter, object.isAwesome());\n"
        + "      jsonWriter.endObject();\n"
        + "    }\n"
        + "    @Override\n"
        + "    public Test read(JsonReader jsonReader) throws IOException {\n"
        + "      if (jsonReader.peek() == JsonToken.NULL) {\n"
        + "        jsonReader.nextNull();\n"
        + "        return null;\n"
        + "      }\n"
        + "      jsonReader.beginObject();\n"
        + "      String name = this.defaultName;\n"
        + "      boolean awesome = this.defaultAwesome;\n"
        + "      while (jsonReader.hasNext()) {\n"
        + "        String _name = jsonReader.nextName();\n"
        + "        if (jsonReader.peek() == JsonToken.NULL) {\n"
        + "          jsonReader.nextNull();\n"
        + "          continue;\n"
        + "        }\n"
        + "        switch (_name) {\n"
        + "          case \"name\": {\n"
        + "            name = nameAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"awesome\": {\n"
        + "            awesome = awesomeAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          default: {\n"
        + "            jsonReader.skipValue();\n"
        + "          }\n"
        + "        }\n"
        + "      }\n"
        + "      jsonReader.endObject();\n"
        + "      return new AutoValue_Test(name, awesome);\n"
        + "    }\n"
        + "  }\n"
        + "}");

    assertAbout(javaSource())
        .that(source)
        .processedWith(new AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Ignore
  @Test public void generatesNothingWithoutTypeAdapterMethod() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "@AutoValue public abstract class Test {\n"
        + "  public abstract String a();\n"
        + "  public abstract boolean b();\n"
        + "}"
    );
    JavaFileObject expected = JavaFileObjects.forSourceString("test/AutoValue_Test", ""
        + "package test;\n"
        + "\n"
        + "import javax.annotation.Generated;\n"
        + "\n"
        + "@Generated(\"com.google.auto.value.processor.AutoValueProcessor\")\n"
        + " final class AutoValue_Test extends Test {\n"
        + "\n"
        + "  private final String a;\n"
        + "  private final boolean b;\n"
        + "\n"
        + "  AutoValue_Test(\n"
        + "      String a,\n"
        + "      boolean b) {\n"
        + "    if (a == null) {\n"
        + "      throw new NullPointerException(\"Null a\");\n"
        + "    }\n"
        + "    this.a = a;\n"
        + "    this.b = b;\n"
        + "  }\n"
        + "\n"
        + "  @Override\n"
        + "  public String a() {\n"
        + "    return a;\n"
        + "  }\n"
        + "\n"
        + "  @Override\n"
        + "  public boolean b() {\n"
        + "    return b;\n"
        + "  }\n"
        + "\n"
        + "  @Override\n"
        + "  public String toString() {\n"
        + "    return \"Test{\"\n"
        + "        + \"a=\" + a + \", \"\n"
        + "        + \"b=\" + b\n"
        + "        + \"}\";\n"
        + "  }\n"
        + "\n"
        + "  @Override\n"
        + "  public boolean equals(Object o) {\n"
        + "    if (o == this) {\n"
        + "      return true;\n"
        + "    }\n"
        + "    if (o instanceof Test) {\n"
        + "      Test that = (Test) o;\n"
        + "      return (this.a.equals(that.a()))\n"
        + "           && (this.b == that.b());\n"
        + "    }\n"
        + "    return false;\n"
        + "  }\n"
        + "\n"
        + "  @Override\n"
        + "  public int hashCode() {\n"
        + "    int h = 1;\n"
        + "    h *= 1000003;\n"
        + "    h ^= this.a.hashCode();\n"
        + "    h *= 1000003;\n"
        + "    h ^= this.b ? 1231 : 1237;\n"
        + "    return h;\n"
        + "  }\n"
        + "\n"
        + "}");

    assertAbout(javaSource())
        .that(source)
        .processedWith(new AutoValueProcessor())
        .compilesWithoutError()
        .withWarningCount(2)
        .and()
        .generatesSources(expected);
  }

  @Ignore
  @Test public void emitsWarningForWrongTypeAdapterTypeArgument() {
    JavaFileObject source1 = JavaFileObjects.forSourceString("test.Foo", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "@AutoValue public abstract class Foo {\n"
        + "  public static TypeAdapter<Bar> typeAdapter(Gson logansquare) {\n"
        + "    return null;"
        + "  }\n"
        + "  public abstract String a();\n"
        + "  public abstract boolean b();\n"
        + "}"
    );

    JavaFileObject source2 = JavaFileObjects.forSourceString("test.Bar", ""
        + "package test;\n"
        + "public class Bar {\n"
        + "}");

    assertAbout(javaSources())
        .that(ImmutableSet.of(source1, source2))
        .processedWith(new AutoValueProcessor())
        .compilesWithoutError()
        .withWarningContaining("Found public static method returning TypeAdapter<test.Bar> on "
            + "test.Foo class. Skipping JsonObjectMapper generation.");
  }

  @Ignore
  @Test public void emitsWarningForNoTypeAdapterTypeArgument() {
    JavaFileObject source1 = JavaFileObjects.forSourceString("test.Foo", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "@AutoValue public abstract class Foo {\n"
        + "  public static TypeAdapter typeAdapter(Gson logansquare) {\n"
        + "    return null;"
        + "  }\n"
        + "  public abstract String a();\n"
        + "  public abstract boolean b();\n"
        + "}"
    );

    assertAbout(javaSource())
        .that(source1)
        .processedWith(new AutoValueProcessor())
        .compilesWithoutError()
        .withWarningContaining("Found public static method returning TypeAdapter with no type "
            + "arguments, skipping JsonObjectMapper generation.");
  }

  @Ignore
  @Test public void compilesWithCapitalPackageName() {
    JavaFileObject source1 = JavaFileObjects.forSourceString("MyPackage.Foo", ""
        + "package MyPackage;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "@AutoValue public abstract class Foo {\n"
        + "  public static TypeAdapter<Foo> typeAdapter(Gson logansquare) {\n"
        + "    return new AutoValue_Foo.JsonObjectMapper(logansquare);"
        + "  }\n"
        + "  public abstract String a();\n"
        + "  public abstract boolean b();\n"
        + "}"
    );

    assertAbout(javaSource())
        .that(source1)
        .processedWith(new AutoValueProcessor())
        .compilesWithoutError()
        .withWarningCount(2);
  }

  @Ignore
  @Test public void generatesCorrectDefaultCharPrimitiveValue() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "@AutoValue public abstract class Test {\n"
        + "  public static TypeAdapter<Test> typeAdapter(Gson logansquare) {\n"
        + "    return new AutoValue_Test.JsonObjectMapper(logansquare);\n"
        + "  }\n"
        + "public abstract char c();\n"
        + "}\n"
    );

    JavaFileObject expected = JavaFileObjects.forSourceString("test/AutoValue_Test", ""
        + "package test;\n"
        + "\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "import com.google.logansquare.stream.JsonReader;\n"
        + "import com.google.logansquare.stream.JsonToken;\n"
        + "import com.google.logansquare.stream.JsonWriter;\n"
        + "import java.io.IOException;\n"
        + "import java.lang.Character;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.String;\n"
        + "\n"
        + "final class AutoValue_Test extends $AutoValue_Test {\n"
        + "  AutoValue_Test(char c) {\n"
        + "    super(c);\n"
        + "  }\n"
        + "\n"
        + "  public static final class JsonObjectMapper extends TypeAdapter<Test> {\n"
        + "    private final TypeAdapter<Character> cAdapter;\n"
        + "    private char defaultC = '\0';\n"
        + "    public JsonObjectMapper(Gson logansquare) {\n"
        + "      this.cAdapter = logansquare.getAdapter(Character.class);\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultC(char defaultC) {\n"
        + "      this.defaultC = defaultC;\n"
        + "      return this;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void write(JsonWriter jsonWriter, Test object) throws IOException {\n"
        + "      if (object == null) {\n"
        + "        jsonWriter.nullValue();\n"
        + "        return;\n"
        + "      }\n"
        + "      jsonWriter.beginObject();\n"
        + "      jsonWriter.name(\"c\");\n"
        + "      cAdapter.write(jsonWriter, object.c());\n"
        + "      jsonWriter.endObject();\n"
        + "    }\n"
        + "    @Override\n"
        + "    public Test read(JsonReader jsonReader) throws IOException {\n"
        + "      if (jsonReader.peek() == JsonToken.NULL) {\n"
        + "        jsonReader.nextNull();\n"
        + "        return null;\n"
        + "      }\n"
        + "      jsonReader.beginObject();\n"
        + "      char c = this.defaultC;\n"
        + "      while (jsonReader.hasNext()) {\n"
        + "        String _name = jsonReader.nextName();\n"
        + "        if (jsonReader.peek() == JsonToken.NULL) {\n"
        + "          jsonReader.nextNull();\n"
        + "          continue;\n"
        + "        }\n"
        + "        switch (_name) {\n"
        + "          case \"c\": {\n"
        + "            c = cAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          default: {\n"
        + "            jsonReader.skipValue();\n"
        + "          }\n"
        + "        }\n"
        + "      }\n"
        + "      jsonReader.endObject();\n"
        + "      return new AutoValue_Test(c);\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources())
      .that(Arrays.asList(nullable, source))
      .processedWith(new AutoValueProcessor())
      .compilesWithoutError()
      .and()
      .generatesSources(expected);
  }

  @Ignore
  @Test public void handlesGenericTypes() {
    JavaFileObject source1 = JavaFileObjects.forSourceString("test.Foo", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "import com.google.logansquare.reflect.TypeToken;\n"
        + "import java.util.List;\n"
        + "import java.util.Map;\n"
        + "@AutoValue public abstract class Foo<A, B, C> {\n"
        + "  public static <A, B, C> TypeAdapter<Foo<A, B, C>> typeAdapter(Gson logansquare, TypeToken<? extends Foo<A, B, C>> typeToken) {\n"
        + "    return new AutoValue_Foo.JsonObjectMapper(logansquare, typeToken);"
        + "  }\n"
        + "  public abstract C c();\n"
        + "  public abstract A a();\n"
        + "  public abstract B b();\n"
        + "  public abstract List<A> list();\n"
        + "  public abstract Map<String, List<C>> map();\n"
        + "  public abstract String d();\n"
        + "}"
    );

    JavaFileObject expected = JavaFileObjects.forSourceString("test.AutoValue_Test", ""
        + "package test;\n"
        + "\n"
        + "import com.google.logansquare.Gson;\n"
        + "import com.google.logansquare.TypeAdapter;\n"
        + "import com.google.logansquare.reflect.TypeToken;\n"
        + "import com.google.logansquare.stream.JsonReader;\n"
        + "import com.google.logansquare.stream.JsonToken;\n"
        + "import com.google.logansquare.stream.JsonWriter;\n"
        + "import java.io.IOException;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.String;\n"
        + "import java.lang.reflect.ParameterizedType;\n"
        + "import java.lang.reflect.Type;\n"
        + "import java.util.Collections;\n"
        + "import java.util.List;\n"
        + "import java.util.Map;\n"
        + "\n"
        + "final class AutoValue_Foo<A, B, C> extends $AutoValue_Foo<A, B, C> {\n"
        + "  AutoValue_Foo(C c, A a, B b, List<A> list, Map<String, List<C>> map, String d) {\n"
        + "    super(c, a, b, list, map, d);\n"
        + "  }\n"
        + "\n"
        + "  public static final class JsonObjectMapper<A, B, C> extends TypeAdapter<Foo<A, B, C>> {\n"
        + "    private final TypeAdapter<C> cAdapter;\n"
        + "    private final TypeAdapter<A> aAdapter;\n"
        + "    private final TypeAdapter<B> bAdapter;\n"
        + "    private final TypeAdapter<List<A>> listAdapter;\n"
        + "    private final TypeAdapter<Map<String, List<C>>> mapAdapter;\n"
        + "    private final TypeAdapter<String> dAdapter;\n"
        + "    private C defaultC = null;\n"
        + "    private A defaultA = null;\n"
        + "    private B defaultB = null;\n"
        + "    private List<A> defaultList = Collections.emptyList();\n"
        + "    private Map<String, List<C>> defaultMap = Collections.emptyMap();\n"
        + "    private String defaultD = null;\n"
        + "    public JsonObjectMapper(Gson logansquare, TypeToken<? extends Foo<A, B, C>> typeToken) {\n"
        + "      ParameterizedType type = (ParameterizedType) typeToken.getType();\n"
        + "      Type[] typeArgs = type.getActualTypeArguments();\n"
        + "      this.cAdapter = (TypeAdapter<C>) logansquare.getAdapter(TypeToken.get(typeArgs[2]));\n"
        + "      this.aAdapter = (TypeAdapter<A>) logansquare.getAdapter(TypeToken.get(typeArgs[0]));\n"
        + "      this.bAdapter = (TypeAdapter<B>) logansquare.getAdapter(TypeToken.get(typeArgs[1]));\n"
        + "      this.listAdapter = (TypeAdapter<List<A>>) logansquare.getAdapter(TypeToken.getParameterized(List.class, typeArgs[0]));\n"
        + "      this.mapAdapter = (TypeAdapter<Map<String, List<C>>>) logansquare.getAdapter(TypeToken.getParameterized(Map.class, String.class, TypeToken.getParameterized(List.class, typeArgs[2]).getType());\n"
        + "      this.dAdapter = logansquare.getAdapter(String.class);\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultC(C defaultC) {\n"
        + "      this.defaultC = defaultC;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultA(A defaultA) {\n"
        + "      this.defaultA = defaultA;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultB(B defaultB) {\n"
        + "      this.defaultB = defaultB;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultList(List<A> defaultList) {\n"
        + "      this.defaultList = defaultList;\n"
        + "      return this;\n"
        + "    }\n"
        + "    public JsonObjectMapper setDefaultMap(Map<String, List<C>> defaultMap) {\n"
        + "      this.defaultMap = defaultMap;\n"
        + "      return this;\n"
        + "    }"
        + "    public JsonObjectMapper setDefaultD(String defaultD) {\n"
        + "      this.defaultD = defaultD;\n"
        + "      return this;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void write(JsonWriter jsonWriter, Foo<A, B, C> object) throws IOException {\n"
        + "      if (object == null) {\n"
        + "        jsonWriter.nullValue();\n"
        + "        return;\n"
        + "      }\n"
        + "      jsonWriter.beginObject();\n"
        + "      jsonWriter.name(\"c\");\n"
        + "      cAdapter.write(jsonWriter, object.c());\n"
        + "      jsonWriter.name(\"a\");\n"
        + "      aAdapter.write(jsonWriter, object.a());\n"
        + "      jsonWriter.name(\"b\");\n"
        + "      bAdapter.write(jsonWriter, object.b());\n"
        + "      jsonWriter.name(\"list\");\n"
        + "      listAdapter.write(jsonWriter, object.list());\n"
        + "      jsonWriter.name(\"map\");\n"
        + "      mapAdapter.write(jsonWriter, object.map());\n"
        + "      jsonWriter.name(\"d\");\n"
        + "      dAdapter.write(jsonWriter, object.d());\n"
        + "      jsonWriter.endObject();\n"
        + "    }\n"
        + "    @Override\n"
        + "    public Foo<A, B, C> read(JsonReader jsonReader) throws IOException {\n"
        + "      if (jsonReader.peek() == JsonToken.NULL) {\n"
        + "        jsonReader.nextNull();\n"
        + "        return null;\n"
        + "      }\n"
        + "      jsonReader.beginObject();\n"
        + "      C c = this.defaultC;\n"
        + "      A a = this.defaultA;\n"
        + "      B b = this.defaultB;\n"
        + "      List<A> list = this.defaultList;\n"
        + "      Map<String, List<C>> map = this.defaultMap;\n"
        + "      String d = this.defaultD;\n"
        + "      while (jsonReader.hasNext()) {\n"
        + "        String _name = jsonReader.nextName();\n"
        + "        if (jsonReader.peek() == JsonToken.NULL) {\n"
        + "          jsonReader.nextNull();\n"
        + "          continue;\n"
        + "        }\n"
        + "        switch (_name) {\n"
        + "          case \"c\": {\n"
        + "            c = cAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"a\": {\n"
        + "            a = aAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"b\": {\n"
        + "            b = bAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"list\": {\n"
        + "            list = listAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"map\": {\n"
        + "            map = mapAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          case \"d\": {\n"
        + "            d = dAdapter.read(jsonReader);\n"
        + "            break;\n"
        + "          }\n"
        + "          default: {\n"
        + "            jsonReader.skipValue();\n"
        + "          }\n"
        + "        }\n"
        + "      }\n"
        + "      jsonReader.endObject();\n"
        + "      return new AutoValue_Foo<>(c, a, b, list, map, d);\n"
        + "    }\n"
        + "  }\n"
        + "}");

    assertAbout(javaSource())
        .that(source1)
        .withCompilerOptions("-A" + COLLECTIONS_DEFAULT_TO_EMPTY + "=true")
        .processedWith(new AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }
}
