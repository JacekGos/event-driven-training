package com.microservices.demo.elastic.query.config;

import com.microservices.demo.config.UserConfigData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;

@Configuration
//@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

//    private final TwitterQueryUserDetailsService twitterQueryUserDetailsService;
//
//    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;
//
//    private final QueryServicePermissionEvaluator queryServicePermissionEvaluator;
//
//    public WebSecurityConfig(TwitterQueryUserDetailsService userDetailsService,
//                             OAuth2ResourceServerProperties resourceServerProperties,
//                             QueryServicePermissionEvaluator queryServicePermissionEvaluator) {
//        this.twitterQueryUserDetailsService = userDetailsService;
//        this.oAuth2ResourceServerProperties = resourceServerProperties;
//        this.queryServicePermissionEvaluator = queryServicePermissionEvaluator;
//    }

    @Value("${security.paths-to-ignore}")
    private String[] pathsToIgnore;

    private final UserConfigData userConfigData;

    public WebSecurityConfig(UserConfigData userConfigData) {
        this.userConfigData = userConfigData;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//        http.authorizeHttpRequests(requests -> requests
//                        .requestMatchers(
//                                new AntPathRequestMatcher("/api-docs/**"),
//                                new AntPathRequestMatcher("/swagger-ui/**"),
//                                new AntPathRequestMatcher("/swagger-ui.html"),
//                                new AntPathRequestMatcher("/v3/api-docs/**"),
//                                new AntPathRequestMatcher("/actuator/**")
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .csrf(AbstractHttpConfigurer::disable);
//
//        return http.build();

//        http
//                .authorizeHttpRequests(requests -> requests
//                        .requestMatchers(Arrays.stream(pathsToIgnore).map(AntPathRequestMatcher::new).toList().toArray(new RequestMatcher[]{}))
//                        .hasRole("USER")
//                        .anyRequest().authenticated())
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .csrf(AbstractHttpConfigurer::disable);
//        return http.build();

        http
                .authorizeHttpRequests(request -> request
//                        .requestMatchers("/**")
                        .requestMatchers(Arrays.stream(pathsToIgnore)
                                .map(AntPathRequestMatcher::new)
                                .toList().toArray(new RequestMatcher[]{}))
//                        .hasRole("USER")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());

        return http.build();
//        http
//                .authorizeHttpRequests(requests -> requests
//                        .requestMatchers(Arrays.stream(pathsToIgnore).map(AntPathRequestMatcher::new).toList().toArray(new RequestMatcher[]{}))
//                        .permitAll()
//                        .anyRequest().authenticated())
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .csrf(AbstractHttpConfigurer::disable)
//                .oauth2ResourceServer((oauth2) -> oauth2
//                        .jwt(jwt -> jwt
//                                .jwtAuthenticationConverter(twitterQueryUserJwtConverter())));
//        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername(userConfigData.getUsername())
                .password(passwordEncoder().encode(userConfigData.getPassword()))
                .roles(userConfigData.getRoles())
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public MethodSecurityExpressionHandler expressionHandler() {
//        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
//        expressionHandler.setPermissionEvaluator(queryServicePermissionEvaluator);
//        return expressionHandler;
//    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(requests -> requests
//                        .requestMatchers(Arrays.stream(pathsToIgnore).map(AntPathRequestMatcher::new).toList().toArray(new RequestMatcher[]{}))
//                        .permitAll()
//                        .anyRequest().authenticated())
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .csrf(AbstractHttpConfigurer::disable)
//                .oauth2ResourceServer((oauth2) -> oauth2
//                        .jwt(jwt -> jwt
//                                .jwtAuthenticationConverter(twitterQueryUserJwtConverter())));
//        return http.build();
//    }
//
//    @Bean
//    JwtDecoder jwtDecoder(@Qualifier("elastic-query-service-audience-validator")
//                          OAuth2TokenValidator<Jwt> audienceValidator) {
//        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(
//                oAuth2ResourceServerProperties.getJwt().getIssuerUri());
//        OAuth2TokenValidator<Jwt> withIssuer =
//                JwtValidators.createDefaultWithIssuer(
//                        oAuth2ResourceServerProperties.getJwt().getIssuerUri());
//        OAuth2TokenValidator<Jwt> withAudience =
//                new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
//        jwtDecoder.setJwtValidator(withAudience);
//        return jwtDecoder;
//    }
//
//    @Bean
//    Converter<Jwt, ? extends AbstractAuthenticationToken> twitterQueryUserJwtConverter() {
//        return new TwitterQueryUserJwtConverter(twitterQueryUserDetailsService);
//    }

}
