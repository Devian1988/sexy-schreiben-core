package com.devsoft.sexyschreiben.core.auth;


import com.devsoft.sexyschreiben.core.dao.user.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    public static final String COOKIE_NAME = "a-token";
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString("sexyschreiben!9y$B&E)H65344vbdfö+@11#-#ÄMcQfTj".getBytes());
    public static final Duration VALID_DURATION = Duration.ofHours(48);
    static SecretKey hmacKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    static List<String> allowedDomains = new ArrayList<>();

    static {
        allowedDomains.add("localhost");
        allowedDomains.add("sexy-schreiben.de");
        allowedDomains.add("api.sexy-schreiben.de");
    }

    @Value("${isLocal:false}")
    boolean isLocal;

    public Cookie generateCookie(UserEntity userEntity,
                                 String domain) {
        if (!isLocal && !allowedDomains.contains(domain)) {
            throw new RuntimeException("Unallowed domain: " + domain);
        }
        String token = null;
        if (userEntity != null) {
            Instant now = Instant.now();
            token = Jwts.builder() //
                    .claim("id", userEntity.getId().toString()) //
                    .claim("username", userEntity.getUsername()) //
                    .claim("roles", userEntity.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet())) //
                    .issuedAt(Date.from(now)) //
                    .expiration(Date.from(now.plus(1L, ChronoUnit.DAYS))) //
                    .signWith(hmacKey).id(UUID.randomUUID().toString()) //
                    .compact();
        }
        return generaterCookieInternal(token, Math.toIntExact(VALID_DURATION.getSeconds()), domain);
    }

    private Cookie generaterCookieInternal(String token,
                                           int ageInSecounds,
                                           String domain) {
        Cookie jwtCookie = new Cookie(COOKIE_NAME, token);
        jwtCookie.setHttpOnly(false);
        jwtCookie.setPath("/");
        jwtCookie.setDomain(domain);
        jwtCookie.setSecure(true);
        jwtCookie.setAttribute("SameSite", "Strict");
        jwtCookie.setMaxAge(ageInSecounds);
        return jwtCookie;
    }

    public Cookie generateDeleteCookie(String domain) {
        return generaterCookieInternal(null, 0, domain);
    }

    public Optional<String> getToken(HttpServletRequest request) {
        Optional<String> token = Optional.empty();
        String header = request.getHeader(COOKIE_NAME);
        if (header != null) {
            token = Optional.of(header);
        }
        if (token.isEmpty()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (!cookie.getName().equals(COOKIE_NAME)) {
                        continue;
                    }
                    token = Optional.of(cookie.getValue());
                }
            }
        }
        if (token.isEmpty()) {
            String queryToken = request.getParameter(COOKIE_NAME);
            if (queryToken != null) {
                token = Optional.of(queryToken);
            }
        }
        return token;
    }

    public Optional<Jws<Claims>> getValidatedToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(hmacKey).build().parseSignedClaims(token);
            LocalDateTime issuedAt = claimsJws.getPayload().getIssuedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime expiredAt = issuedAt.plus(JwtUtils.VALID_DURATION);
            if (LocalDateTime.now().isAfter(expiredAt)) {
                return Optional.empty();
            } else {
                return Optional.of(claimsJws);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Can not validate token: " + token, e);
            }
            return Optional.empty();
        }
    }
}


