package com.warehousemanagement.config;

import com.warehousemanagement.model.enums.UserRole;
import com.warehousemanagement.service.implementation.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static com.warehousemanagement.constant.Constants.API_PATH;
import static com.warehousemanagement.constant.Constants.REDIRECT_USER_API_PATH;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthenticationService authenticationService;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/WEB-INF/**", "/webjars/**", "/css/**", "/images/**",
                                        API_PATH + "/login", API_PATH + "/authenticate", API_PATH + "/register", REDIRECT_USER_API_PATH).permitAll()
                                .requestMatchers(API_PATH + "/account/system/admin/**").hasAuthority(UserRole.SYSTEM_ADMIN.getRole())
                                .requestMatchers(API_PATH + "/account/manage/warehouse/**").hasAuthority(UserRole.WAREHOUSE_MANAGER.getRole())
                                .anyRequest().authenticated())
                .formLogin(loginConfig ->
                        loginConfig.loginPage(API_PATH + "/login")
                                .loginProcessingUrl(API_PATH + "/authenticate")
                                .usernameParameter("email")
                                .successForwardUrl(REDIRECT_USER_API_PATH)
                                .permitAll())
                .logout(logoutConfig ->
                        logoutConfig.logoutUrl(API_PATH + "/logout")
                                .permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(authenticationService);

        return http.build();
    }
}
