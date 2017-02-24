# AutoValue: LoganSquare (Jackson) Extension

[![Build Status](https://travis-ci.org/ppiech/auto-value-jackson.svg?branch=master)](https://travis-ci.org/ppiech/auto-value-jackson)

An extension for Google's [AutoValue](https://github.com/google/auto) that creates a simple `JsonMapper` for each AutoValue annotated object.

## Usage

Simply include auto-value-jackson in your project and add a public static method to your `@AutoValue`
annotated class returning a JsonMapper.  You can also annotate your properties using
`@JsonProperty` to define an alternate name for de/serialization.

```java
@AutoValue public abstract class Foo {
  abstract String bar();
  @JsonProperty(name="Baz") abstract String baz();
  abstract int quux();
  abstract String with_underscores();

  // The public static method returning a <Foo> is what
  // tells auto-value-jackson to create a JsonMapper for Foo.
  public static JsonMapper<Foo> jsonMapper(JacksonAuto jacksonAuto) {
    return new AutoValue_Foo.JsonObjectMapper(jacksonAuto)
      // You can set custom default values
      .setDefaultQuux(4711)
      .setDefaultWith_underscores("");
  }
}
```

Now build your project and de/serialize your Foo.

## The JsonMapper

To trigger JsonMapper generation, you need include a non-private static factory method that accepts
a `JacksonAuto` parameter and returns a `JsonMapper` for your AutoValue type. From within this method you
can instantiate a new `JsonObjectMapper` which will have been generated as an inner class of your
AutoValue generated implementation.

```java
@AutoValue public abstract class Foo {
  // properties...

  public static JsonMapper<Foo> typeAdapter(JacksonAuto jacksonAuto) {
    return new AutoValue_Foo.JsonObjectMapper(jacksonAuto);
  }
}
```

## Generics support

TODO

## Factory

Optionally, auto-value-gson can create a single `JsonMapperFactory` so
that you don't have to add each generated JsonMapper to your JacksonAuto instance manually.

To generate a `JsonMapperFactory` for all of your auto-value-jackson classes, simply create
an abstract class that implements `JsonMapperFactory` and annotate it with `@AutoValueJsonMapperFactory`,
and auto-value-jackson will create an implementation for you.  You simply need to provide a static
factory method, just like your AutoValue classes, and you can use the generated `JsonMapperFactory`
to help JacksonAuto de/serialize your types.

```java
@AutoValueJsonMapperFactory
public abstract class MyAdapterFactory implements JsonMapperFactory {

  // Static factory method to access the package
  // private generated implementation
  public static JsonMapperFactory create() {
    return new AutoValueJackson_MyAdapterFactory();
  }

}
```

Then you simply need to register the Factory with Gson.

```java
JacksonAuto gson = new JacksonAuto.Builder()
    .registerJsonMapperFactory(MyAdapterFactory.create())
    .build();
```

## Download

Add a Gradle dependency to the `apt` and `provided` configuration.

```groovy
apt 'com.ppiech.auto.value:auto-value-jackson:0.1.0'
provided 'com.ppiech.auto.value:auto-value-jackson:0.1.0'
compile 'com.ppiech.auto.value:jackson-auto:0.1.0'
```

(Using the [android-apt](https://bitbucket.org/hvisser/android-apt) plugin)

Snapshots of the latest development version are available in [Sonatype's `snapshots` repository](https://oss.sonatype.org/content/repositories/snapshots/).

You will also need a normal runtime dependency for LoganSquare itself.

```groovy
compile 'com.bluelinelabs:logansquare:1.3.7'
```

## License

```
Copyright 2015-2017 Ryan Harter, BlueLine Labs, Inc., Pawel Piech

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
