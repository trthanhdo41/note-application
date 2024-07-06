package com.project.note.controller;

import com.project.note.model.User;
import com.project.note.service.Interface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PaymentController {

    @Autowired
    private UserService userService;

    @GetMapping("user/register-vip")
    public String registerVip(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User user = userService.findByUsername(username);
        String role = user.getRole();

        boolean isVip = role.contains("ROLE_VIPMEMBER") || role.contains("ROLE_ADMIN") || role.contains("ROLE_MANAGER");

        model.addAttribute("isVip", isVip);
        model.addAttribute("username", username);
        model.addAttribute("selectedPackage", "goi1");

        return "payment/register-vip";
    }


    @GetMapping("/user/success")
    public String success() {
        return "payment/vipmember-success";
    }

    @PostMapping("/user/update-role")
    public String updateRole(@RequestParam String username, @RequestParam String role) {
        userService.updateUserRole(username, role);
        return "redirect:/user/success";
    }



}