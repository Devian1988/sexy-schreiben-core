package com.devsoft.sexyschreiben.core.configuration;


import com.devsoft.sexyschreiben.core.common.NotSecured;
import com.devsoft.sexyschreiben.core.auth.JWTTokenFilter;
import com.devsoft.sexyschreiben.core.auth.JwtSessionAuthenticationStrategy;
import com.devsoft.sexyschreiben.core.auth.JwtUtils;
import com.devsoft.sexyschreiben.core.auth.UsernamePasswordClientAuthenticationToken;
import com.devsoft.sexyschreiben.core.dao.client.ClientRepo;
import com.devsoft.sexyschreiben.core.dao.client.entity.ClientEntity;
import com.devsoft.sexyschreiben.core.dao.user.UserRepo;
import com.devsoft.sexyschreiben.core.dao.user.entity.Authority;
import com.devsoft.sexyschreiben.core.dao.user.entity.UserEntity;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AutoConfiguration()
@ComponentScan(basePackages = {"com.devsoft.sexyschreiben.core.auth"})
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Slf4j
public class SecurityConfiguration {

    private final ClientRepo clientRepo;
    @Value("${cors.allowed.origin.suffix}")
    String corsAllowedOriginSuffixs;

    String[] corsAllowedOriginSuffixArray;

    public SecurityConfiguration(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("Tali-Lolipop234.14111988.Finni#Lisa!"));
    }

    @PostConstruct
    void init() {
        if (corsAllowedOriginSuffixs != null) {
            corsAllowedOriginSuffixArray = corsAllowedOriginSuffixs.split(",");
        }
    }

    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            String requestedHost = request.getServerName();
            CorsConfiguration configuration = new CorsConfiguration();
            if (checkCors(requestedHost)) {
                String origin = request.getHeader("Origin");
                configuration.setAllowedOrigins(Collections.singletonList(origin));
                configuration.setAllowCredentials(true);
                configuration.setAllowedMethods(Arrays.asList("POST", "GET", "OPTIONS", "DELETE"));
                configuration.setMaxAge(3600L);
                configuration.setAllowedHeaders(List.of("*"));
            }
            return configuration;
        };
    }

    private boolean checkCors(String requestedHost) {
        boolean match = false;
        if (requestedHost != null) {
            for (String suffix : corsAllowedOriginSuffixArray) {
                match = requestedHost.endsWith(suffix);
                if (match) {
                    break;
                }
            }
        }
        return match;
    }

    @Bean
    RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(String.join(" > ", Authority.ADMIN_ROLE, Authority.CLIENT_ADMIN_ROLE, Authority.CLIENT_USER_ROLE, Authority.GUEST_ROLE));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   UserRepo userRepo,
                                                   TransactionTemplate transactionTemplate,
                                                   JwtUtils jwtUtils,
                                                   JwtSessionAuthenticationStrategy jwtSessionAuthenticationStrategy) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.authorizeHttpRequests((requests) -> {
            requests.requestMatchers("/api/status/health", "/api/login", "/api/logout").permitAll();
            requests.requestMatchers(HttpMethod.OPTIONS, new String[]{"/**"}).permitAll();
            requests.anyRequest().authenticated();
        }).formLogin(AbstractHttpConfigurer::disable).logout(LogoutConfigurer::permitAll);
        httpSecurity.addFilterBefore(authenticationJwtTokenFilter(userRepo, jwtUtils, transactionTemplate), UsernamePasswordAuthenticationFilter.class);
        httpSecurity.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });
        httpSecurity.sessionManagement(sessionManagement -> {
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            sessionManagement.addSessionAuthenticationStrategy(jwtSessionAuthenticationStrategy);
        });

        return httpSecurity.build();
    }

    @Bean
    Advisor preAuthorize() {
        JdkRegexpMethodPointcut pattern = new JdkRegexpMethodPointcut();
        pattern.setPattern("com.devsoft.sexyschreiben.*");
        AuthorizationManagerBeforeMethodInterceptor interceptor = new AuthorizationManagerBeforeMethodInterceptor(pattern, (authentication, invocation) -> {
            try {
                MergedAnnotations methodAnnotations = MergedAnnotations.from(invocation.getMethod(), MergedAnnotations.SearchStrategy.DIRECT);
                if (invocation.getThis() != null) {
                    MergedAnnotations classAnnotations = MergedAnnotations.from(invocation.getThis().getClass(), MergedAnnotations.SearchStrategy.DIRECT);
                    if (classAnnotations.get(RestController.class).isPresent() && methodAnnotations.get(ExceptionHandler.class).isPresent()) {
                        return new AuthorizationDecision(true);
                    } else if (classAnnotations.get(RestController.class).isPresent()) {
                        if (methodAnnotations.get(Secured.class).isPresent() || methodAnnotations.get(PreAuthorize.class).isPresent()) {
                            return null;
                        } else if (methodAnnotations.get(NotSecured.class).isPresent()) {
                            return new AuthorizationDecision(true);
                        } else {
                            return new AuthorizationDecision(false);

                        }
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                log.error("", e);
                return new AuthorizationDecision(false);
            }

        });
        interceptor.setOrder(AuthorizationInterceptorsOrder.PRE_AUTHORIZE.getOrder() - 1);
        return interceptor;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder,
                                                       UserRepo userRepo) throws Exception {
        return new ProviderManager(new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                UsernamePasswordClientAuthenticationToken token = (UsernamePasswordClientAuthenticationToken) authentication;
                Optional<ClientEntity> clientOpt = clientRepo.findByClientIdentifier(token.getClientIdentifier());
                if (clientOpt.isPresent()) {
                    Optional<UserEntity> user = userRepo.findOne((root, query, criteriaBuilder) -> //
                            criteriaBuilder.and(//
                                    criteriaBuilder.equal(root.get("username"), token.getPrincipal()), //
                                    criteriaBuilder.equal(root.get("client").get("id"), clientOpt.get().getId())));
                    if (user.isPresent()) {
                        UserEntity userEntity = user.get();
                        if (passwordEncoder.matches(token.getCredentials().toString(), userEntity.getPassword())) {
                            UsernamePasswordClientAuthenticationToken authenticatedToken = new UsernamePasswordClientAuthenticationToken(token.getPrincipal(), token.getCredentials(), token.getClientIdentifier(), userEntity.getAuthorities());
                            authenticatedToken.setDetails(userEntity);
                            return authenticatedToken;
                        } else {
                            throw new BadCredentialsException("Bad credentials");
                        }
                    } else {
                        throw new BadCredentialsException("Bad credentials");
                    }
                } else {
                    throw new BadCredentialsException("Bad credentials");
                }
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordClientAuthenticationToken.class.isAssignableFrom(authentication);
            }
        });
    }

    public JWTTokenFilter authenticationJwtTokenFilter(UserRepo userRepo,
                                                       JwtUtils jwtUtils,
                                                       TransactionTemplate transactionTemplate) {
        return new JWTTokenFilter(userRepo, jwtUtils, transactionTemplate);
    }
}

