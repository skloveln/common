package cn.zpc.mvc.user.security.interceptor;

import cn.zpc.common.config.Global;
import cn.zpc.mvc.user.security.AuthTokenProvider;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.exception.AuthFailureException;
import cn.zpc.mvc.user.security.model.AuthenticationToken;
import cn.zpc.mvc.user.security.model.BaseToken;
import cn.zpc.mvc.user.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description:权限拦截器
 * Author: Simon
 * Date: 2017-08-17
 */
public class AuthInterceptor implements HandlerInterceptor{


    private final AuthTokenProvider authTokenProvider;


    @Autowired
    public AuthInterceptor(AuthTokenProvider authTokenProvider) {
        this.authTokenProvider = authTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //如果验证token失败，并且方法注明了Authorization，返回401错误
        Authorization auth = handlerMethod.getMethodAnnotation(Authorization.class);

        if(auth != null){
            //从header中得到token
            String authorization = request.getHeader(Global.AUTHORIZATION);

            BaseToken baseToken = new BaseToken(authorization);
            // 验证token
            Authentication authentication = authTokenProvider.authenticate(new AuthenticationToken(baseToken));
            if(!authentication.isAuthenticated()){
                if(auth.intercept()){
                    throw new AuthFailureException();
                }
            }
            UserContext userContext = (UserContext) authentication.getPrincipal();
            userContext.setAuthenticated(authentication.isAuthenticated());
            request.setAttribute(Global.CURRENT_AUTHENTICATION, userContext);
            UserUtils.setUserContext(userContext);

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
