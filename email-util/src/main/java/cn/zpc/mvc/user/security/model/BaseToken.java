package cn.zpc.mvc.user.security.model;

import cn.zpc.mvc.user.security.exception.AuthFailureException;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Description: 访问令牌
 * Author: sukai
 * Date: 2017-08-17
 */
public final class BaseToken {

    private String token;

    public BaseToken(String token) {
        this.token = token;
    }

    public Jws<Claims> parseClaims(String signingKey) {
        try {
            return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            throw new BadCredentialsException("user.auth.token.bad");
        } catch (ExpiredJwtException expiredEx) {
            throw new AuthFailureException("user.auth.token.expired");
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
