package com.ppiech.auto.value.jackson.annotation;

import com.ppiech.auto.value.jackson.JsonMapperFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Annotation to indicate that a given class should generate a concrete implementation of a
 * {@link JsonMapperFactory} that handles all the publicly denoted adapter implementations of this
 * project.
 * <p>
 * <code>
 *   &#64;AutoValueJsonObjectMapperFactory
 *   public abstract class Factory implements JsonObjectMapperFactory {
 *     public static Factory create() {
 *       return new AutoValueJackson_Factory();
 *     }
 *   }
 * </code>
 */
@Retention(SOURCE)
@Target(TYPE)
public @interface AutoValueJsonMapperFactory {
}
