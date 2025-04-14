package com.devsoft.sexyschreiben.core.auth;


import com.devsoft.sexyschreiben.core.dao.client.ClientRepo;
import com.devsoft.sexyschreiben.core.dao.client.entity.ClientEntity;
import com.devsoft.sexyschreiben.core.dao.user.UserRepo;
import com.devsoft.sexyschreiben.core.dao.user.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Component
public class JwtSessionAuthenticationStrategy implements SessionAuthenticationStrategy {
    private static final Logger log = LoggerFactory.getLogger(JwtSessionAuthenticationStrategy.class);


    final JwtUtils jwtUtils;
    private final UserRepo userRepo;
    private final ClientRepo clientRepo;


    public JwtSessionAuthenticationStrategy(JwtUtils jwtUtils,
                                            UserRepo userRepo,
                                            ClientRepo clientRepo) {
        this.jwtUtils = jwtUtils;
        this.userRepo = userRepo;
        this.clientRepo = clientRepo;
    }

    public void onAuthentication(Authentication authentication,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws SessionAuthenticationException {
        if (request.getMethod().equalsIgnoreCase("post") && request.getRequestURL().toString().contains("/api/login")) {
            return;
        }
        if (authentication.isAuthenticated() && authentication.getClass().isAssignableFrom(UsernamePasswordClientAuthenticationToken.class)) {
            UsernamePasswordClientAuthenticationToken authToken = (UsernamePasswordClientAuthenticationToken) authentication;
            Optional<String> token = this.jwtUtils.getToken(request);
            if (token.isPresent()) {
                Optional<Jws<Claims>> validatedToken = this.jwtUtils.getValidatedToken(token.get());
                if (validatedToken.isPresent()) {
                    Claims claims = validatedToken.get().getPayload();
                    LocalDateTime issuedAt = claims.getIssuedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime expiredAt = issuedAt.plus(JwtUtils.VALID_DURATION);
                    if (LocalDateTime.now().isAfter(issuedAt) && LocalDateTime.now().isAfter(issuedAt.plusHours(1)) && LocalDateTime.now().isBefore(expiredAt)) {
                        String username = authToken.getPrincipal().toString();
                        String clientIdentifier = authToken.getClientIdentifier();
                        Optional<ClientEntity> clientOpt = clientRepo.findByClientIdentifier(clientIdentifier);
                        if (clientOpt.isPresent()) {
                            ClientEntity client = clientOpt.get();
                            UserEntity user = userRepo.findByUsernameAndClient(username, client);
                            Cookie jwtCookie = this.jwtUtils.generateCookie(user, request.getServerName());
                            log.debug("Refreshed JWTToken for: {}", username);
                            response.addCookie(jwtCookie);
                        } else {
                            deleteJwtCookie(request, response);
                        }
                    } else if (LocalDateTime.now().isAfter(expiredAt)) {
                        deleteJwtCookie(request, response);
                    }
                } else {
                    deleteJwtCookie(request, response);
                }
            }
        }
    }

    private void deleteJwtCookie(HttpServletRequest request,
                                 HttpServletResponse response) {
        Cookie jwtCookie = this.jwtUtils.generateDeleteCookie(request.getServerName());
        response.addCookie(jwtCookie);
    }
}


