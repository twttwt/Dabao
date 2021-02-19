package com.twt.router_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明当前注解可以被保留的时间
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Destination {
    String url();

    String description();
}
