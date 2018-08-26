package cn.zpc.mvc.user.security.resolver;

import cn.zpc.common.config.Global;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Description:增加方法注入，将含有CurrentUser注解的方法参数注入当前登录用户
 * Author: Simon
 * Date: 2017-08-17
 */
@Component
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver{

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        //如果参数类型是User并且有CurrentUser注解则支持
        return parameter.getParameterType().isAssignableFrom(UserContext.class) &&
                parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        return nativeWebRequest.getAttribute(Global.CURRENT_AUTHENTICATION, RequestAttributes.SCOPE_REQUEST);
    }
}
