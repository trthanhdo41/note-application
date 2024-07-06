package com.project.note.security;

import com.project.note.model.User;
import com.project.note.service.Interface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class TwoFactorAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    //cho phép vào những trang này khi chưa xác thực 2FA
    private final List<String> permitAllUrls = List.of(
            "/note/what-is-note-myself", "/auth/logout", "/home"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());

            Boolean is2faVerified = (Boolean) request.getSession().getAttribute("2fa_verified");

            if (permitAllUrls.contains(request.getRequestURI()) || request.getRequestURI().startsWith("/static/")) {
                filterChain.doFilter(request, response);
                return;
            }

            if (user.getTotpSecret() != null && (is2faVerified == null || !is2faVerified)) {
                if (!request.getRequestURI().equals("/auth/verify-login-2fa")) {
                    response.sendRedirect("/auth/verify-login-2fa");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
