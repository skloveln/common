package cn.zpc.mvc.user.security;

import cn.zpc.mvc.user.security.config.JwtSetting;
import cn.zpc.mvc.user.security.factory.AuthTokenFactory;
import cn.zpc.mvc.user.security.model.BaseToken;
import cn.zpc.mvc.user.security.model.AuthenticationToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Author: sukai
 * Date: 2017-08-17
 */
@Component
public class AuthTokenProvider implements AuthenticationProvider{

    private final JwtSetting jwtSetting;

    @Autowired
    private AuthTokenFactory authTokenFactory;

    @Autowired
    public AuthTokenProvider(JwtSetting jwtSetting) {
        this.jwtSetting = jwtSetting;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        BaseToken token = (BaseToken) authentication.getCredentials();
        Jws<Claims> jwsClaims = token.parseClaims(jwtSetting.getShortTokenSigningKey());
        String subject = jwsClaims.getBody().getSubject();
        UserContext context = UserContext.create(subject);
        authTokenFactory.checkCache(context.getCacheKey(), token.getToken());

        return new AuthenticationToken(context);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BaseToken.class.isAssignableFrom(authentication);
    }
}
