package com.devsoft.sexyschreiben.core.auth;


import com.devsoft.sexyschreiben.core.dao.user.UserRepo;
import com.devsoft.sexyschreiben.core.dao.user.entity.Authority;
import com.devsoft.sexyschreiben.core.dao.user.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JWTTokenFilter extends OncePerRequestFilter {

    final UserRepo userRepo;
    final JwtUtils jwtUtils;
    private final TransactionTemplate transactionTemplate;

    public JWTTokenFilter(UserRepo userRepo,
                          JwtUtils jwtUtils,
                          TransactionTemplate transactionTemplate) {
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
        this.transactionTemplate = transactionTemplate;
    }

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Optional<String> jwt = this.jwtUtils.getToken(request);
            if (jwt.isPresent()) {
                Optional<Jws<Claims>> validatedToken = this.jwtUtils.getValidatedToken(jwt.get());
                if (validatedToken.isPresent()) {
                    Claims body = validatedToken.get().getPayload();
                    UUID userId = UUID.fromString(body.get("id", String.class));
                    UserEntity user = transactionTemplate.execute((status) -> {
                        UserEntity entity = userRepo.getReferenceById(userId);
                        entity.getAuthorities();
                        return entity;
                    });
                    UsernamePasswordClientAuthenticationToken authentication = new UsernamePasswordClientAuthenticationToken(user.getUsername(),null, user.getClient().getClientIdentifier(), user.getAuthorities());
                    authentication.setDetails(user);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("unknown", new UserEntity(), List.of((GrantedAuthority) Authority.Type.GUEST::name)));
                }
            } else {
                SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("unknown", new UserEntity(), List.of((GrantedAuthority) Authority.Type.GUEST::name)));
            }
        } catch (Exception e) {
            this.logger.debug("Cannot set user authentication: {}", e);
            SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("unknown", new UserEntity(), List.of((GrantedAuthority) Authority.Type.GUEST::name)));
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected void initFilterBean() throws ServletException {
    }
}


