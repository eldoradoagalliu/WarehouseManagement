package com.warehousemanagement.config;

import com.warehousemanagement.model.enums.UserRole;
import com.warehousemanagement.service.implementation.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                                // Permissions for System Admin role
                                .requestMatchers(HttpMethod.GET, API_PATH + "/account/system/admin").hasAuthority(UserRole.SYSTEM_ADMIN.getRole())
                                .requestMatchers(HttpMethod.POST, API_PATH + "/account/approve/password/change/{id}").hasAuthority(UserRole.SYSTEM_ADMIN.getRole())
                                .requestMatchers(HttpMethod.GET, API_PATH + "/account/{id}/edit").hasAuthority(UserRole.SYSTEM_ADMIN.getRole())
                                .requestMatchers(HttpMethod.DELETE, API_PATH + "/account/{id}").hasAuthority(UserRole.SYSTEM_ADMIN.getRole())
                                // Permissions for Warehouse Manager role
                                .requestMatchers(HttpMethod.GET, API_PATH + "/account/manage/warehouse").hasAuthority(UserRole.WAREHOUSE_MANAGER.getRole())
                                .requestMatchers(API_PATH + "/item/**", API_PATH + "/truck/**").hasAuthority(UserRole.WAREHOUSE_MANAGER.getRole())
                                .requestMatchers(HttpMethod.POST, API_PATH + "/order/approve/{id}", API_PATH + "/order/decline/{id}",
                                        API_PATH + "/order/fulfill/{id}").hasAuthority(UserRole.WAREHOUSE_MANAGER.getRole())
                                .requestMatchers(HttpMethod.GET, API_PATH + "/order/filter").hasAuthority(UserRole.WAREHOUSE_MANAGER.getRole())
                                // Permissions for Client role
                                .requestMatchers(HttpMethod.GET, API_PATH + "/account/dashboard").hasAuthority(UserRole.CLIENT.getRole())
                                .requestMatchers(HttpMethod.POST, API_PATH + "/order/submit/{id}", API_PATH + "/order/cancel/{id}").hasAuthority(UserRole.CLIENT.getRole())
                                .requestMatchers(HttpMethod.GET, API_PATH + "/order/client/filter").hasAuthority(UserRole.CLIENT.getRole())
                                // Common permission for Manager and Client role
                                .requestMatchers(HttpMethod.POST, API_PATH + "/account/request/password/change/{id}")
                                .hasAnyAuthority(UserRole.WAREHOUSE_MANAGER.getRole(), UserRole.CLIENT.getRole())
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
