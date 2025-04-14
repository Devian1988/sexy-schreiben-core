package com.devsoft.sexyschreiben.core.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger log = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    final JwtUtils utils;

    public AuthEntryPointJwt(JwtUtils utils) {
        this.utils = utils;
    }

    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        if ((request.getMethod().equalsIgnoreCase("post") && request.getServletPath().endsWith("api/login")) || //
                (request.getMethod().equalsIgnoreCase("get") && request.getServletPath().endsWith("api/status/health"))) {
            return;
        }
        if (response.getStatus() == 200) {
            Optional<String> token = utils.getToken(request);
            if (token.isEmpty() || utils.getValidatedToken(token.get()).isEmpty()) {
                response.setStatus(401);
            } else {
                response.setStatus(403);
            }
        }
        log.info("Error: {} url: {}", authException.getMessage(), request.getRequestURL().toString());
    }
}


