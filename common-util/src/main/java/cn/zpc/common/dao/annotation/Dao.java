package cn.zpc.common.dao.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Description:注解标识Dao支持
 * Author: Simon
 * Date: 2017-08-11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface Dao {
    String value() default "";
}
