package cn.zpc.common.handler.exception;

import cn.zpc.common.utils.SpringContextHolder;
import cn.zpc.common.web.result.Result;
import org.apache.http.HttpStatus;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * Description:公共返回结果模版
 * Author: sukai
 * Date: 2017-08-24
 */
public class GlobalExceptionResult extends RuntimeException implements Result{

    private int code;
    private int status = HttpStatus.SC_OK; // 返回状态码
    private MessageSource messageSource = SpringContextHolder.getBean(MessageSource.class);
    private Object[] params;

    public GlobalExceptionResult(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public GlobalExceptionResult(String msg, int code, Object... params) {
        super(msg);
        this.code = code;
        this.params = params;
    }

    public GlobalExceptionResult(String msg, int code, int status) {
        super(msg);
        this.code = code;
        this.status= status;
    }

    public GlobalExceptionResult(String msg, int code, int status, Object[] params) {
        super(msg);
        this.code = code;
        this.params = params;
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(super.getMessage(), params, locale);
    }
}
