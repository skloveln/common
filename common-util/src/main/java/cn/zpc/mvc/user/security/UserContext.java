package cn.zpc.mvc.user.security;

import cn.zpc.common.utils.StringUtils;

/**
 * Description:用户权限上下文
 * Author: sukai
 * Date: 2017-08-17
 */
public class UserContext{

    private final String userId;

    private boolean authenticated = true;

    private final String cacheKey;


    private UserContext(String userId) {
        this.userId = userId;
        this.cacheKey = "token_" + userId;
    }

    public static UserContext create(String userId) {
        if (StringUtils.isBlank(userId)) throw new IllegalArgumentException("userId is blank: " + userId);
        return new UserContext(userId);
    }

    public Integer getUserId() {
        try {
            if(StringUtils.isNotEmpty(userId)){
                return Integer.parseInt(userId);
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getCacheKey() {
        return cacheKey;
    }
}
