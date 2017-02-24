package com.ppiech.auto.value.jackson.processor;

import com.google.common.collect.ImmutableSet;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Ignore;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class AutoValueJsonMapperFactoryProcessorTest {

  @Ignore
  @Test public void generatesJsonMapperFactory() {
    JavaFileObject source1 = JavaFileObjects.forSourceString("test.Foo", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import JsonMapper;\n"
        + "import com.ppiech.auto.value.jackson.JacksonAuto;\n"
        + "@AutoValue public abstract class Foo {\n"
        + "  public static JsonMapper<Foo> jsonMapper(JacksonAuto loganSquareAuto) {\n"
        + "    return null;\n"
        + "  }\n"
        + "  public abstract String getName();\n"
        + "  public abstract boolean isAwesome();\n"
        + "}");
    JavaFileObject source2 = JavaFileObjects.forSourceString("test.Bar", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import JsonMapper;\n"
        + "import com.ppiech.auto.value.jackson.JacksonAuto;\n"
        + "@AutoValue public abstract class Bar {\n"
        + "  public static JsonMapper<Bar> jsonMapper(JacksonAuto loganSquareAuto) {\n"
        + "    return null;\n"
        + "  }\n"
        + "  public abstract String getName();\n"
        + "}");
    JavaFileObject source3 = JavaFileObjects.forSourceString("test.MyAdapterFactory", ""
        + "package test;\n"
        + "import com.ppiech.auto.value.jackson.JsonMapperFactory;\n"
        + "import AutoValueJsonMapperFactory;\n"
        + "@AutoValueJsonMapperFactory\n"
        + "public abstract class MyAdapterFactory implements JsonMapperFactory {\n"
        + "  public static JsonMapperFactory create() {\n"
        + "    return new AutoValueLoganSquare_MyAdapterFactory();\n"
        + "  }\n"
        + "}");
    JavaFileObject expected = JavaFileObjects.forSourceString("test.AutoValueLoganSquare_MyAdapterFactory", ""
        + "package test;\n"
        + "import JsonMapper;\n"
        + "import com.ppiech.auto.value.jackson.JacksonAuto;\n"
        + "import java.lang.Class;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "\n"
        + "public final class AutoValueLoganSquare_MyAdapterFactory extends MyAdapterFactory {\n"
        + "  @Override\n"
        + "  @SuppressWarnings(\"unchecked\")\n"
        + "  public <T> JsonMapper<T> mapperFor(JacksonAuto loganSquareAuto, Class<T> clazz) {\n"
        + "    if (Foo.class.isAssignableFrom(clazz)) {\n"
        + "      return (JsonMapper<T>) Foo.jsonMapper(loganSquareAuto);\n"
        + "    } else if (Bar.class.isAssignableFrom(clazz)) {\n"
        + "      return (JsonMapper<T>) Bar.jsonMapper(loganSquareAuto);\n"
        + "    } else {\n"
        + "      return null;\n"
        + "    }\n"
        + "  }\n"
        + "}");

    assertAbout(javaSources())
        .that(ImmutableSet.of(source1, source2, source3))
        .processedWith(new AutoValueJsonMapperFactoryProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Ignore
  @Test public void generatesJsonMapperFactory_notAbstract_shouldFail() {
    JavaFileObject source1 = JavaFileObjects.forSourceString("test.Foo", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import JsonMapper;\n"
        + "import com.ppiech.auto.value.jackson.JacksonAuto;\n"
        + "@AutoValue public abstract class Foo {\n"
        + "  public static JsonMapper<Foo> jsonMapper(JacksonAuto loganSquareAuto) {\n"
        + "    return null;\n"
        + "  }\n"
        + "  public abstract String getName();\n"
        + "  public abstract boolean isAwesome();\n"
        + "}");
    JavaFileObject source2 = JavaFileObjects.forSourceString("test.MyAdapterFactory", ""
        + "package test;\n"
        + "import com.ppiech.auto.value.jackson.JsonMapperFactory;\n"
        + "import AutoValueJsonMapperFactory;\n"
        + "@AutoValueJsonMapperFactory\n"
        + "public class MyAdapterFactory implements JsonMapperFactory {\n"
        + "  public static JsonMapperFactory create() {\n"
        + "    return new AutoValueLoganSquare_MyAdapterFactory();\n"
        + "  }\n"
        + "}");

    assertAbout(javaSources())
        .that(ImmutableSet.of(source1, source2))
        .processedWith(new AutoValueJsonMapperFactoryProcessor())
        .failsToCompile()
        .withErrorContaining("Must be abstract!");
  }

  @Ignore
  @Test public void generatesJsonMapperFactory_doesNotImplementJsonMapperFactory_shouldFail() {
    JavaFileObject source1 = JavaFileObjects.forSourceString("test.Foo", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import JsonMapper;\n"
        + "import com.ppiech.auto.value.jackson.JacksonAuto;\n"
        + "@AutoValue public abstract class Foo {\n"
        + "  public static JsonMapper<Foo> jsonMapper(JacksonAuto loganSquareAuto) {\n"
        + "    return null;\n"
        + "  }\n"
        + "  public abstract String getName();\n"
        + "  public abstract boolean isAwesome();\n"
        + "}");
    JavaFileObject source2 = JavaFileObjects.forSourceString("test.Bar", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import JsonMapper;\n"
        + "import com.ppiech.auto.value.jackson.JacksonAuto;\n"
        + "@AutoValue public abstract class Bar {\n"
        + "  public static JsonMapper<Bar> jsonMapper(JacksonAuto loganSquareAuto) {\n"
        + "    return null;\n"
        + "  }\n"
        + "  public abstract String getName();\n"
        + "}");
    JavaFileObject source3 = JavaFileObjects.forSourceString("test.MyAdapterFactory", ""
        + "package test;\n"
        + "import com.ppiech.auto.value.jackson.JsonMapperFactory;\n"
        + "import AutoValueJsonMapperFactory;\n"
        + "@AutoValueJsonMapperFactory\n"
        + "public abstract class MyAdapterFactory {\n"
        + "  public static JsonMapperFactory create() {\n"
        + "    return new AutoValueLoganSquare_MyAdapterFactory();\n"
        + "  }\n"
        + "}");

    assertAbout(javaSources())
        .that(ImmutableSet.of(source1, source2, source3))
        .processedWith(new AutoValueJsonMapperFactoryProcessor())
        .failsToCompile()
        .withErrorContaining("Must implement JsonMapperFactory!");
  }

  @Ignore
  @Test public void generatesJsonMapperFactory_shouldSearchUpComplexAncestry() {
    JavaFileObject source1 = JavaFileObjects.forSourceString("test.Foo", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import JsonMapper;\n"
        + "import com.ppiech.auto.value.jackson.JacksonAuto;\n"
        + "@AutoValue public abstract class Foo {\n"
        + "  public static JsonMapper<Foo> jsonMapper(JacksonAuto loganSquareAuto) {\n"
        + "    return null;\n"
        + "  }\n"
        + "  public abstract String getName();\n"
        + "  public abstract boolean isAwesome();\n"
        + "}");
    JavaFileObject source2 = JavaFileObjects.forSourceString("test.Bar", ""
        + "package test;\n"
        + "import com.google.auto.value.AutoValue;\n"
        + "import JsonMapper;\n"
        + "import com.ppiech.auto.value.jackson.JacksonAuto;\n"
        + "@AutoValue public abstract class Bar {\n"
        + "  public static JsonMapper<Bar> jsonMapper(JacksonAuto loganSquareAuto) {\n"
        + "    return null;\n"
        + "  }\n"
        + "  public abstract String getName();\n"
        + "}");
    JavaFileObject source3 = JavaFileObjects.forSourceString("test.IMyAdapterFactoryBase", ""
        + "package test;\n"
        + "import com.ppiech.auto.value.jackson.JsonMapperFactory;\n"
        + "public interface IMyAdapterFactoryBase extends JsonMapperFactory {\n"
        + "}");
    JavaFileObject source4 = JavaFileObjects.forSourceString("test.MyAdapterFactory", ""
        + "package test;\n"
        + "import com.ppiech.auto.value.jackson.JsonMapperFactory;\n"
        + "import AutoValueJsonMapperFactory;\n"
        + "@AutoValueJsonMapperFactory\n"
        + "public abstract class MyAdapterFactory implements IMyAdapterFactoryBase {\n"
        + "  public static JsonMapperFactory create() {\n"
        + "    return new AutoValueLoganSquare_MyAdapterFactory();\n"
        + "  }\n"
        + "}");
    JavaFileObject expected = JavaFileObjects.forSourceString("test.AutoValueLoganSquare_MyAdapterFactory", ""
        + "package test;\n"
        + "import JsonMapper;\n"
        + "import com.ppiech.auto.value.jackson.JacksonAuto;\n"
        + "import java.lang.Class;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "\n"
        + "public final class AutoValueLoganSquare_MyAdapterFactory extends MyAdapterFactory {\n"
        + "  @Override\n"
        + "  @SuppressWarnings(\"unchecked\")\n"
        + "  public <T> JsonMapper<T> mapperFor(JacksonAuto loganSquareAuto, Class<T> clazz) {\n"
        + "    if (Foo.class.isAssignableFrom(clazz)) {\n"
        + "      return (JsonMapper<T>) Foo.jsonMapper(loganSquareAuto);\n"
        + "    } else if (Bar.class.isAssignableFrom(clazz)) {\n"
        + "      return (JsonMapper<T>) Bar.jsonMapper(loganSquareAuto);\n"
        + "    } else {\n"
        + "      return null;\n"
        + "    }\n"
        + "  }\n"
        + "}");

    assertAbout(javaSources())
        .that(ImmutableSet.of(source1, source2, source3, source4))
        .processedWith(new AutoValueJsonMapperFactoryProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }
}
