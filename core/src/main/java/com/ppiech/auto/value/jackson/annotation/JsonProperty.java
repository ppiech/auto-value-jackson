package com.ppiech.auto.value.jackson.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Declare that a property should be parsed/serialized.
 * <pre><code>
 * {@literal @}JsonProperty(name = "random_variable_name")
 * public String randomVariableName;
 * </code></pre>
 */
@Target(METHOD)
@Retention(CLASS)
public @interface JsonProperty {

    /**
     * @return The name(s) of this field in JSON. Use an array if this could be represented by multiple names.
     */
    String[] name() default {};

    /**
     * @return The TypeConverter that will be used to parse/serialize this variable.
     * */
    Class typeConverter() default void.class;

}
