package com.jwell56.security.cloud.service.ids.annotation;

import org.elasticsearch.search.sort.SortOrder;

import java.lang.annotation.*;

/**
 * @author wsg
 * @since 2020/12/3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface EntitySortOrder {
    SortOrder sortOrder() default SortOrder.DESC;
}
