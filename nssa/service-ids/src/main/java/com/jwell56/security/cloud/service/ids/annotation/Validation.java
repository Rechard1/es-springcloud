package com.jwell56.security.cloud.service.ids.annotation;

import java.lang.annotation.*;

/**
 * 输入参数有效性验证
 *
 * @author wsg
 * @since 2020/11/14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Documented
public @interface Validation {

    boolean allowEmpty() default true;

    String regex() default "";

    String regexMessage() default "";

    int length() default 0;

    String enumValue() default "";
}
