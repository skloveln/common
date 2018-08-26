package cn.zpc.mvc.user.security.exception;

import cn.zpc.common.web.result.Result;
import org.springframework.security.core.AuthenticationException;

/**
 * Description:身份校验失败
 * Author: sukai
 * Date: 2017-08-17
 */
public class AuthFailureException extends AuthenticationException {

    private int code = Result.AUTH_FAILURE;

    public AuthFailureException() {
        super("user.auth.fail");
    }

    public AuthFailureException(String message){
        super(message);
    }

    public AuthFailureException(Integer code, String message){
        super(message);
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
