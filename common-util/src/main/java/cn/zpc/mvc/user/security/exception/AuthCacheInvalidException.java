package cn.zpc.mvc.user.security.exception;

/**
 * Description: redis 缓存失效不可用
 * Author: sukai
 * Date: 2017-08-17
 */
public class AuthCacheInvalidException extends RuntimeException {

    public AuthCacheInvalidException() {
        super("user.auth.cache.invalid");
    }

}
