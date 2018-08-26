package cn.zpc.common.interceptor;

import cn.zpc.common.config.Global;
import cn.zpc.common.plugins.log.LogCacheService;
import cn.zpc.common.utils.DateUtils;
import cn.zpc.common.utils.SpringContextHolder;
import cn.zpc.mvc.sys.dao.ApiLogDao;
import cn.zpc.mvc.sys.entity.ApiLog;
import cn.zpc.mvc.user.security.AuthTokenProvider;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.model.AuthenticationToken;
import cn.zpc.mvc.user.security.model.BaseToken;
import cn.zpc.mvc.user.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.security.core.Authentication;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 全局拦截器，获取请求的相关信息
 * Author: sukai
 * Date: 2017/8/3.
 */
public class BaseInterceptor implements HandlerInterceptor{

    @Autowired
    private ApiLogDao apiLogDao;
    @Autowired
    private AuthTokenProvider authTokenProvider;
    private final static Logger logger = LoggerFactory.getLogger(BaseInterceptor.class);
    private final static ThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<>("StartTime ThreadLocal");
    private final static LogCacheService logCacheService = SpringContextHolder.getBean(LogCacheService.class);


    /**
     * 进入控制器之前执行拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        if(logger.isDebugEnabled()){
            long startTime = System.currentTimeMillis();
            startTimeThreadLocal.set(startTime);
        }
        return true;
    }


    /**
     * 控制器处理之后执行
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ApiLog apiLog = new ApiLog();
        String authorization = request.getHeader(Global.AUTHORIZATION);
        if(authorization!=null && !authorization.isEmpty()){
            BaseToken baseToken = new BaseToken(authorization);
            Authentication authentication = authTokenProvider.authenticate(new AuthenticationToken(baseToken));
            if(authentication.isAuthenticated()){
                UserContext userContext = (UserContext) authentication.getPrincipal();
                userContext.setAuthenticated(authentication.isAuthenticated());
                request.setAttribute(Global.CURRENT_AUTHENTICATION, userContext);
                UserUtils.setUserContext(userContext);
                apiLog.setUserId(userContext.getUserId());
            }
        }
        String os = request.getParameter("appOS");
        if(os != null && !os.isEmpty()) {
            apiLog.setOs(Integer.parseInt(request.getParameter("appOS")));
            apiLog.setVersion(request.getParameter("appVersion"));
            apiLog.setTime(new Date());
            apiLog.setUrl(request.getRequestURI());
            apiLog.setParam(getParamString(request));
            apiLogDao.insert(apiLog);
        }
    }


    /**
     * 返回视图后执行
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        if(logger.isDebugEnabled()){
            if (handler instanceof HandlerMethod) {
                long beginTime = startTimeThreadLocal.get();//得到线程绑定的局部变量（开始时间）
                System.out.println("\n\n\n");
                logger.debug("------------------------------- Start: {} -------------------------------------", new SimpleDateFormat("hh:mm:ss.SSS").format(beginTime));
                HandlerMethod h = (HandlerMethod) handler;
                logger.debug("controller     : " + h.getBean().getClass().getName());
                logger.debug("Method         : " + h.getMethod().getName());
                logger.debug("authorization  : " + request.getHeader(Global.AUTHORIZATION));
                logger.debug("OS            : " + request.getParameter("appOS"));
                logger.debug("Params         : " + getParamString(request));
                logger.debug("URI            : " + request.getRequestURI());

                long endTime = System.currentTimeMillis();
                String time = "End: {}  Used: {}  MaxMemory: {}m  UsedMemory: {}m  FreeMemory: {}m  MaxCanUsedMemory: {}m";
                logger.debug(time, new Object[]{new SimpleDateFormat("hh:mm:ss.SSS").format(endTime),
                        DateUtils.formatDateTime(endTime - beginTime),
                        Runtime.getRuntime().maxMemory()/1024/1024,
                        Runtime.getRuntime().totalMemory()/1024/1024,
                        Runtime.getRuntime().freeMemory()/1024/1024,
                        (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory())/1024/1024});
                //删除线程变量中的数据，防止内存泄漏
                startTimeThreadLocal.remove();

                logger.debug("-----------------------------------------------------------------------------------------\n\n\n");

            }
        }

        // 缓存action 统计
//        if(request.getParameter("appOS") == null){
//            logCacheService.cacheAction(request.getRequestURI(),  request.getMethod());
//        }
    }


    public static String getParamString(HttpServletRequest request) {
       String value = "";
        Enumeration names = request.getParameterNames();
        if(names != null){
            while (names.hasMoreElements()){
                String name = (String) names.nextElement();
                value += name;
                String parameter = request.getParameter(name);
                value += "=";
                value += parameter + "\t";
            }
        }
       return value;
    }

}
