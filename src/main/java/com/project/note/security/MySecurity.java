package com.project.note.security;

import com.project.note.service.Implement.MyUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MySecurity {

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    @Lazy
    private TwoFactorAuthenticationFilter twoFactorAuthenticationFilter;

    @Autowired
    @Lazy
    private ActiveStatusFilter activeStatusFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/auth/verify-2fa", "/auth/verify-login-2fa", "/user/**/avatar", "/user/**/avatar/delete"))
                .authorizeRequests(configurer -> configurer
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/note/what-is-note-myself").permitAll()
                        .requestMatchers("/auth/forgot", "/auth/reset-password-form","auth/register/confirm", "/auth/activate").permitAll()
                        .requestMatchers("/auth/two-factor-auth", "/auth/generate-2fa", "/auth/enable-2fa", "/auth/disable-2fa", "/auth/setup-instructions", "/auth/verify-2fa","/auth/verify-login-2fa", "/auth/information").authenticated()
                        .requestMatchers("/note/**", "/loading/**", "/user/**", "/group/**").authenticated()
                        .requestMatchers("/group/**").hasAnyRole("VIPMEMBER", "ADMIN", "MANAGER")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MANAGER")
                        .anyRequest().permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/authenticateTheUser")
                        .successHandler(twoFactorAuthenticationSuccessHandler())
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/auth/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login")
                        .permitAll()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.FOUND),
                                new AntPathRequestMatcher("/**")
                        )
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/auth/login");
                        })
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .addFilterBefore(twoFactorAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(activeStatusFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(myUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            if (request.getUserPrincipal() == null) {
                response.sendRedirect("/auth/login");
            } else {
                response.sendRedirect("/auth/login");
            }
        };
    }

    @Bean
    public SimpleUrlAuthenticationSuccessHandler twoFactorAuthenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
                if (userDetails.is2FaEnabled()) {
                    request.getSession().setAttribute("2fa_verified", false);
                    response.sendRedirect("/auth/verify-login-2fa");
                } else {
                    super.onAuthenticationSuccess(request, response, authentication);
                }
            }
        };
    }
}
