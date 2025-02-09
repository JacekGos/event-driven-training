package com.microservices.demo.elastic.query.web.client.config;

import com.microservices.demo.config.UserConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class WebSecurityConfig {

    private final UserConfigData userConfigData;

    public WebSecurityConfig(UserConfigData userConfigData) {
        this.userConfigData = userConfigData;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(new AntPathRequestMatcher("/"))
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/**"))
                        .hasRole("USER")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        return http.build();
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

//    private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);
//
//    private static final String GROUPS_CLAIM = "groups";
//
//    private static final String ROLE_PREFIX = "ROLE_";
//
////    private final ClientRegistrationRepository clientRegistrationRepository;
//
//    @Value("${security.logout-success-url}")
//    private String logoutSuccessUrl;
//
//    public WebSecurityConfig(ClientRegistrationRepository registrationRepository) {
//        this.clientRegistrationRepository = registrationRepository;
//    }
//
//    OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
//        OidcClientInitiatedLogoutSuccessHandler successHandler =
//                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
//        successHandler.setPostLogoutRedirectUri(logoutSuccessUrl);
//        return successHandler;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(requests -> requests
//                        .requestMatchers(new AntPathRequestMatcher("/"))
//                        .permitAll()
//                        .anyRequest().authenticated())
//                .logout(conf -> conf.logoutSuccessHandler(oidcLogoutSuccessHandler()))
//                .oauth2Client(Customizer.withDefaults())
//                .oauth2Login(conf -> conf.userInfoEndpoint(Customizer.withDefaults()));
//        return http.build();
//    }
//
//    @Bean
//    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
//        return (authorities) -> {
//            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
//
//            authorities.forEach(
//                    authority -> {
//                        if (authority instanceof OidcUserAuthority) {
//                            OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
//                            OidcIdToken oidcIdToken = oidcUserAuthority.getIdToken();
//                            LOG.info("Username from id token: {}", oidcIdToken.getPreferredUsername());
//                            OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();
//
//                            List<SimpleGrantedAuthority> groupAuthorities =
//                                    userInfo.getClaimAsStringList(GROUPS_CLAIM).stream()
//                                            .map(group ->
//                                                    new SimpleGrantedAuthority(ROLE_PREFIX + group.toUpperCase()))
//                                            .collect(Collectors.toList());
//                            mappedAuthorities.addAll(groupAuthorities);
//                        }
//                    });
//            return mappedAuthorities;
//        };
//    }

}
