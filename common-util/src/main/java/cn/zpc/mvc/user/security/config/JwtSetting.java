package cn.zpc.mvc.user.security.config;

import org.springframework.stereotype.Component;

/**
 * Description:令牌配置
 * Author: Simon
 * Date: 2017-08-17
 */
@Component
public class JwtSetting {

    // 1天有效期
    private int tokenExpirationTime = 60 * 24;

    // 30天
    private int refreshTokenExpTime = 60 * 24 * 30;

    // 令牌key
    private String tokenSigningKey = "ashiahsf@123)($023-NJOIIn_";

    private String shortTokenSigningKey = "sl!93nj10d~kdl''.,[]@34_";

    public int getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(int tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public int getRefreshTokenExpTime() {
        return refreshTokenExpTime;
    }

    public void setRefreshTokenExpTime(int refreshTokenExpTime) {
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    public String getTokenSigningKey() {
        return tokenSigningKey;
    }

    public String getShortTokenSigningKey() {
        return shortTokenSigningKey;
    }

}
