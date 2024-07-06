package com.project.note.controller;

import com.project.note.model.User;
import com.project.note.service.Interface.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
public class TwoFactorAuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/auth/two-factor-auth")
    public String getTwoFactorAuthPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            model.addAttribute("user", user);
            model.addAttribute("is2faEnabled", user.getTotpSecret() != null);
        }
        return "auth/twoFactorAuth";
    }

    @GetMapping("/auth/setup-instructions")
    public String getSetupInstructionsPage(Model model, Authentication authentication, HttpServletRequest request) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            if (user.getTotpSecret() != null) {
                return "redirect:/auth/two-factor-auth";
            }
            Map<String, String> response = userService.generateTwoFactorAuthentication(user, request);
            model.addAttribute("totpSecret", response.get("totpSecret"));
            model.addAttribute("qrDataUri", response.get("qrDataUri"));
            model.addAttribute("user", user);
        }
        return "auth/setupInstructions";
    }

    @GetMapping("/auth/verify-2fa")
    public String getVerify2FaPage(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            if (user.getTotpSecret() != null) {
                return "redirect:/auth/two-factor-auth";
            }
        }
        return "auth/verify2fa";
    }

    @PostMapping("/auth/verify-2fa")
    @ResponseBody
    public Map<String, String> verifyTwoFactorAuthentication(@RequestBody Map<String, String> request, Authentication authentication, HttpServletRequest httpServletRequest) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            String totpCode = request.get("totpCode");
            String result = userService.enableTwoFactorAuthentication(user, totpCode, httpServletRequest);
            if (result.equals("success")) {
                return Map.of("status", "success");
            } else {
                return Map.of("status", "error", "message", result);
            }
        }
        return Map.of("status", "error", "message", "Unauthorized - Không được phép");
    }

    @PostMapping("/auth/generate-2fa")
    @ResponseBody
    public Map<String, String> generateTwoFactorAuthentication(Authentication authentication, HttpServletRequest request) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            return userService.generateTwoFactorAuthentication(user, request);
        }
        throw new RuntimeException("Unauthorized - Không được phép");
    }

    @PostMapping("/auth/disable-2fa")
    public RedirectView disableTwoFactorAuthentication(Authentication authentication, HttpServletRequest request) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            String result = userService.disableTwoFactorAuthentication(user);
            if (result.equals("success")) {
                request.getSession().setAttribute("disable2faSuccess", true);
                return new RedirectView("/auth/two-factor-auth");
            } else {
                request.getSession().setAttribute("disable2faError", result);
                return new RedirectView("/auth/two-factor-auth");
            }
        }
        request.getSession().setAttribute("disable2faError", "Unauthorized - Không được phép");
        return new RedirectView("/auth/two-factor-auth");
    }

    @GetMapping("/auth/verify-login-2fa")
    public String getVerifyLogin2FaPage() {
        return "auth/verify-login-2fa";
    }

    @PostMapping("/auth/verify-login-2fa")
    @ResponseBody
    public Map<String, String> verifyLoginTwoFactorAuthentication(@RequestBody Map<String, String> request, Authentication authentication, HttpServletRequest httpServletRequest) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            String totpCode = request.get("totpCode");
            boolean isCodeValid = userService.verifyTwoFactorAuthenticationCode(user, totpCode);
            if (isCodeValid) {
                user.setTwoFactorAuthenticationVerified(true);
                httpServletRequest.getSession().setAttribute("2fa_verified", true);
                return Map.of("status", "success");
            } else {
                return Map.of("status", "error", "message", "Mã không chính xác. Vui lòng thử lại.");
            }
        }
        return Map.of("status", "error", "message", "Unauthorized - Không được phép");
    }


}
