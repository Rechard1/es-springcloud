package com.jwell56.security.cloud.service.ids.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wsg
 * @since 2020/12/10
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldSettingProperty {
    String group() default "";

    boolean isDefault() default false;
}
