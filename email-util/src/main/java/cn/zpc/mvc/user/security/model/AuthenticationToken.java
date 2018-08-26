package cn.zpc.mvc.user.security.model;

import cn.zpc.mvc.user.security.UserContext;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Description:
 * Author: sukai
 * Date: 2017-08-17
 */
public class AuthenticationToken extends AbstractAuthenticationToken {

    private BaseToken token;

    private UserContext userContext;

    public AuthenticationToken(BaseToken token) {
        super(null);
        this.token = token;
    }

    public AuthenticationToken(UserContext userContext) {
        super(null);
        this.eraseCredentials();
        this.userContext = userContext;
        super.setAuthenticated(true);
    }

    public AuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return this.userContext;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.token = null;
    }
}
