package com.project.note.controller;

import com.project.note.model.AccountActivationToken;
import com.project.note.model.User;
import com.project.note.repository.AccountActivationTokenRepository;
import com.project.note.repository.PasswordResetTokenRepository;
import com.project.note.repository.UserRepository;
import com.project.note.service.Interface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountActivationTokenRepository accountActivationTokenRepository;

    @GetMapping("/login")
    public ModelAndView login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return new ModelAndView("redirect:/home");
        }
        ModelAndView modelAndView = new ModelAndView("auth/login");
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DELETED"))) {
            modelAndView.addObject("error", "Your account has been deleted.");
        }
        return modelAndView;
    }

    @GetMapping("/register/create")
    public String showRegistrationForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser"))) {
            return "redirect:/home";
        }
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register/save")
    public String registerAccount(@ModelAttribute("user") User user) {
        try {
            userService.registerUser(user);
            return "redirect:/loading-for-register";
        } catch (RuntimeException e) {
            return "redirect:/auth/register/create?error=true";
        }
    }

    @GetMapping("/register/confirm")
    public String showConfirmRegistration() {
        return "auth/confirmRegistration";
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam("token") String token, Model model) {
        Optional<AccountActivationToken> optionalActivationToken = accountActivationTokenRepository.findByToken(token);

        if (optionalActivationToken.isEmpty() || optionalActivationToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Token không hợp lệ hoặc đã hết hạn.");
        } else {
            AccountActivationToken activationToken = optionalActivationToken.get();
            User user = activationToken.getUser();
            user.setActive(1);
            userRepository.save(user);
            accountActivationTokenRepository.delete(activationToken);
            model.addAttribute("message", "Tài khoản của bạn đã được kích hoạt thành công.");
        }
        return "auth/activation";
    }

    @GetMapping("/register/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        return userService.existsByUsername(username);
    }

    @GetMapping("/register/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/note")
    public String success(Model model) {
        return "note/index";
    }

    @GetMapping("/reset-password")
    public String resetPassword(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            User user = userService.findByUsername(username);
            model.addAttribute("user", user);
            return "auth/resetPassword";
        }
        return "redirect:/auth/login";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("currentPassword") String currentPassword,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword,
                                RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();
        if (user != null) {
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu hiện tại không chính xác.");
                return "redirect:/auth/reset-password";
            }
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới và xác nhận mật khẩu không khớp nhau.");
                return "redirect:/auth/reset-password";
            }
            user.setPassword(passwordEncoder.encode(password));
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật mật khẩu thành công!");
            return "redirect:/user/information";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng.");
            return "redirect:/auth/reset-password";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "auth/forgot";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            userService.forgotPasswordUseEmail(email);
            model.addAttribute("message", "Mã xác nhận đã được gửi đến email của bạn.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "auth/forgot";
    }

    @GetMapping("/reset-password-form")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "auth/resetPasswordConfirm";
    }

    @PostMapping("/reset-password-confirm")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       Model model) {
        try {
            userService.resetPasswordUseEmail(token, password);
            model.addAttribute("message", "Mật khẩu đã được đặt lại thành công.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "auth/resetPasswordConfirm";
    }
}
