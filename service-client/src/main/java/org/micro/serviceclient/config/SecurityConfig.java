package org.micro.serviceclient.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .oauth2Login(Customizer.withDefaults())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/start/hello").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2ResourceServer ->oauth2ResourceServer.jwt(jwt ->
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter () {
            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            converter.setPrincipalClaimName("preferred_username");
            converter.setJwtGrantedAuthoritiesConverter(jwt -> {
                Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
                List<String> roles = jwt.getClaimAsStringList("spring_sec_roles");

                return Stream.concat(authorities.stream(),
                                roles.stream()
                                        .filter(role -> role.startsWith("ROLE_"))
                                        .map(SimpleGrantedAuthority::new)
                                        .map(GrantedAuthority.class::cast))
                        .toList();
            });

            return converter;
        }

        @Bean
        public OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService () {
            var oidcUserService = new OidcUserService();
            return userRequest -> {
                var oidcUser = oidcUserService.loadUser(userRequest);
                var roles = oidcUser.getClaimAsStringList("spring_sec_roles");
                var authorities = Stream.concat(oidcUser.getAuthorities().stream(),
                                roles.stream()
                                        .filter(role -> role.startsWith("ROLE_"))
                                        .map(SimpleGrantedAuthority::new)
                                        .map(GrantedAuthority.class::cast))
                        .toList();

                return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
            };
        }
    }

