package cn.zpc.mvc.user.security.annotation;

import java.lang.annotation.*;

/**
 * Author: sukai
 * Date: 2017/8/4.
 */
@Documented
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorization {

    /**
     * 是否拦截
     */
    boolean intercept() default true;
}

