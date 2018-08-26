package cn.zpc.common.handler;

import cn.zpc.common.config.Global;
import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.interceptor.BaseInterceptor;
import cn.zpc.common.plugins.email.EmailService;
import cn.zpc.common.utils.PropertiesLoader;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.common.web.validators.Validators;
import cn.zpc.mvc.user.security.exception.AuthFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;


/**
 * Description:校验处理器
 * Author: sukai
 * Date: 2017-08-16
 */
@ControllerAdvice
@Component
public class GlobalExceptionHandler {

    private final static PropertiesLoader loader = new PropertiesLoader("application.properties");
    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final static String env = loader.getProperty("env");


    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Autowired
    EmailService emailService;

    // 对应 beanvalidator 参数校验绑定
    @ExceptionHandler(BindException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result handle(BindException exception) {
        MessageResult messageResult = new MessageResult();
        messageResult.setCode(Result.EXCEPTION);
        BindingResult bindingResult = exception.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if(!allErrors.isEmpty()){
            String defaultMessage = allErrors.get(0).getDefaultMessage();
            messageResult.setMessage(defaultMessage);
        }
        return messageResult;
    }


    // 对应 requestParam 多个参数校验绑定
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result handle(ConstraintViolationException exception) {
        MessageResult messageResult = new MessageResult();
        messageResult.setCode(Result.EXCEPTION);
        List<String> errors = Validators.extractPropertyAndMessageAsList(exception);
        if(!errors.isEmpty()){
            messageResult.setMessage(errors.get(0));
        }
        return messageResult;
    }


    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result handle(Exception exception, HttpServletRequest request) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw, true));
        String os = request.getParameter("appOS");
        if(os == null){
            os = "未知   ";
        }else if(os.equals("1")){
            os = "IOS   ";
        }else if(os.equals("2")){
            os = "Android   ";
        }
        // TODO 500 错误需要发邮件通知管理员
        emailService.sendException(os + env + "Exception异常通知",
                "authorization  : "  + request.getHeader(Global.AUTHORIZATION) + "\r\n" +
                "OS             : "  + request.getParameter("appOS") + "\r\n" +
                "Params         : "  + BaseInterceptor.getParamString(request) + "\r\n" +
                "URI            : "  + request.getRequestURI() + "\r\n\r\n" + sw.toString());

        return MessageResult.getExceptionMessage(sw.toString());
    }

    /**
     * 身份校验异常处理
     * @param exception
     * @return
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result handle(AuthenticationException exception) {
        if(logger.isDebugEnabled()){
            exception.printStackTrace();
        }
        MessageResult messageResult = new MessageResult();
        if(exception instanceof AuthFailureException){
            AuthFailureException authFailureException = (AuthFailureException) exception;
            messageResult.setCode(authFailureException.getCode());
        } else {
            messageResult.setCode(Result.AUTH_FAILURE);
        }
        messageResult.setMessage(exception.getMessage());
        return messageResult;
    }


    /**
     * 公共异常信息返回模版 直接抛异常
     * @see GlobalExceptionResult
     *
     */
    @ExceptionHandler(GlobalExceptionResult.class)
    @ResponseBody
    public Result handle(GlobalExceptionResult result, HttpServletResponse response){
        response.setStatus(result.getStatus());
        MessageResult messageResult = new MessageResult();
        messageResult.setCode(result.getCode());
        messageResult.setMessage(result.getMessage());

        return messageResult;
    }

}