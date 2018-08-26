package cn.zpc.common.web.result;

import cn.zpc.common.utils.SpringContextHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import sun.misc.resources.Messages_es;

import java.util.Locale;

/**
 * Description:消息结果
 * Author: sukai
 * Date: 2017-08-15
 */
public class MessageResult implements Result {

    @JsonIgnore
    private MessageSource messageSource = SpringContextHolder.getBean(MessageSource.class);

    public int code = NORMAL;

    public String message = "请求处理成功";

    public MessageResult(){}

    public MessageResult(int code){
        this.code = code;
    }

    public MessageResult(String message){
        this.message = message;
    }

    public MessageResult(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        Locale locale = LocaleContextHolder.getLocale();
        this.message = messageSource.getMessage(message, null, locale);
    }

    public void setMessage(String message, Object... params) {
        Locale locale = LocaleContextHolder.getLocale();
        this.message = messageSource.getMessage(message, params, locale);
    }

    public static MessageResult getNormalMessage(){
        MessageResult result = new MessageResult();
        result.setMessage("请求处理成功");
        result.setCode(Result.NORMAL);
        return result;
    }

    public static MessageResult getExceptionMessage(String message){
        MessageResult result = new MessageResult();
        result.setMessage(message);
        result.setCode(Result.EXCEPTION);
        return result;
    }

}

