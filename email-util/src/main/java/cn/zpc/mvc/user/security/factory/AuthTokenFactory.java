package cn.zpc.mvc.user.security.factory;

import cn.zpc.common.redis.Cache;
import cn.zpc.common.redis.Redis;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.config.JwtSetting;
import cn.zpc.mvc.user.security.exception.AuthCacheInvalidException;
import cn.zpc.mvc.user.security.exception.AuthFailureException;
import cn.zpc.mvc.user.security.model.BaseToken;
import cn.zpc.mvc.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * Description: redis管理令牌
 * Author: sukai
 * Date: 2017-08-17
 */
@Component
@Scope("singleton")
public class AuthTokenFactory {

    private final JwtSetting jwtSetting;

    private UserService userService;

    @Autowired
    public AuthTokenFactory(JwtSetting jwtSetting) {
        this.jwtSetting = jwtSetting;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 创建用户的accessToken
     * @param user
     * @return
     */
    public BaseToken createAccessJwtToken(UserContext user) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(user.getUserId()));
        LocalDateTime currentTime = LocalDateTime.now();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .plusMinutes(jwtSetting.getTokenExpirationTime())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtSetting.getShortTokenSigningKey())
                .compact();

        // 只有短效token需要缓存
        Cache use = Redis.use();
        if(use == null){
            throw new AuthCacheInvalidException();
        }

        // 设置缓存过期时间，分钟转秒
        use.setex(user.getCacheKey(), jwtSetting.getTokenExpirationTime() * 60, token);
        return new BaseToken(token);
    }

    /**
     * 创建用户的refreshToken
     * @param user
     * @return
     */
    public BaseToken createRefreshToken(UserContext user) {

        LocalDateTime currentTime = LocalDateTime.now();
        Claims claims = Jwts.claims().setSubject(String.valueOf(user.getUserId()));

        String token = Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .plusMinutes(jwtSetting.getRefreshTokenExpTime())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtSetting.getTokenSigningKey())
                .compact();

        userService.saveToken(token, user);

        return new BaseToken(token);
    }

    /**
     * 检查accessToken是否存在
     * @param cacheKey
     * @param token
     */
    public void checkCache(String cacheKey, String token){
        Cache use = Redis.use();
        if(use == null){
            throw new AuthCacheInvalidException();
        }
        String cache = use.get(cacheKey);
        if(StringUtils.isEmpty(cache) || !token.equals(cache)){
            throw new AuthFailureException();
        }
    }

}
