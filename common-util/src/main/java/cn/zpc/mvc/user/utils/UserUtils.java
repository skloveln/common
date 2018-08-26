package cn.zpc.mvc.user.utils;

import cn.zpc.mvc.user.security.UserContext;
import org.springframework.core.NamedThreadLocal;

import java.util.UUID;

/**
 * Description:用户工具
 * Author: sukai
 * Date: 2017-08-15
 */
public class UserUtils {

    private static final ThreadLocal<UserContext> userContextHolder =
            new NamedThreadLocal<>("User context");

    // 长效令牌失效时间 一个月
    public final static long THIRTY_DAYS = 1000L * 60L * 60L * 24L * 30L;
    /**
     *  生成令牌，通过用户ID和加密码
     */
    public static synchronized String generateToken(String msg){

        UUID uuid = UUID.randomUUID();

        String token = UUID.nameUUIDFromBytes(msg.getBytes()) + uuid.toString();

        token = token.replaceAll("-", "");

        String t = "";

        for(int i = 0; i < token.length(); i += 2){
            t += token.charAt(i);
        }

        return t;
    }


    public static void setUserContext(UserContext userContext){
        userContextHolder.set(userContext);
    }

    /**
     * 是否通过身份验证
     */
    public static boolean isAuthenticated(){
        UserContext userContext = userContextHolder.get();
        return  userContext.isAuthenticated();
    }

    /**
     * 获取用户上下文信息
     */
    public static UserContext getUserContext(){
        UserContext userContext = userContextHolder.get();
        if(userContext == null) userContext = UserContext.create("");
        return userContext;
    }

}

