package com.devsoft.sexyschreiben.core.auth;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class UsernamePasswordClientAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String clientIdentifier;

    public UsernamePasswordClientAuthenticationToken(Object principal,
                                                     Object credentials,
                                                     String clientIdentifier) {
        super(principal, credentials);
        this.clientIdentifier = clientIdentifier;
    }

    public UsernamePasswordClientAuthenticationToken(Object principal,
                                                     Object credentials,
                                                     String clientIdentifier,
                                                     Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.clientIdentifier = clientIdentifier;
    }
}
