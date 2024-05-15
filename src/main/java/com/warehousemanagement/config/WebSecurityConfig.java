package com.warehousemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private UserDetailsService userDetailsService;

//    @Autowired
//    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/resources/**", "/static/**", "/webjars/**", "/css/**", "/register", "/login").permitAll()
                .antMatchers("/system/admin").access("hasRole('SYSTEM_ADMIN')")
//                .antMatchers("/manage/warehouse").access("hasRole('WAREHOUSE_MANAGER')")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").usernameParameter("email").permitAll()
                .and()
                .logout().permitAll();

        return http.build();
    }

    //TODO: JWT Implementation - Work In Progress
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests((authorize) -> {
//                    authorize.requestMatchers("/resources/**", "/static/**", "/webjars/**", "/register", "/login").permitAll();
//                    authorize.requestMatchers("/admin/**").hasRole(RoleEnum.SYSTEM_ADMIN.getRole());
//                    authorize.requestMatchers("/manage/warehouse/**").hasRole(RoleEnum.WAREHOUSE_MANAGER.getRole());
//                    authorize.anyRequest().authenticated();
//                })
//                .formLogin((loginConfig) -> loginConfig.loginPage("/login").usernameParameter("email").permitAll())
//                .logout((logoutConfig) -> logoutConfig.logoutUrl("/logout").permitAll())
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .exceptionHandling((exceptions) -> exceptions.authenticationEntryPoint(
//                        (request, response, ex) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage())
//                ))
//                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//    }

//    @Bean
//    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests((authorize) -> {
//                    authorize.requestMatchers("/resources/**", "/static/**", "/webjars/**", "/register", "/login").permitAll();
//                    authorize.requestMatchers("/admin/**").hasRole(RoleEnum.SYSTEM_ADMIN.getRole());
//                    authorize.requestMatchers("/manage/warehouse/**").hasRole(RoleEnum.WAREHOUSE_MANAGER.getRole());
//                    authorize.anyRequest().authenticated();
//                })
//                .formLogin((loginConfig) -> loginConfig.loginPage("/login").usernameParameter("email").permitAll())
//                .logout(LogoutConfigurer::permitAll);
//
//        return http.build();
//    }

    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}
