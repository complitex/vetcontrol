package org.vetcontrol.util.book.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.01.2010 20:51:11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidProperty {
    boolean value() default true;
}
