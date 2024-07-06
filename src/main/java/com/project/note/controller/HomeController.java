package com.project.note.controller;

import com.project.note.dto.UserDto;
import com.project.note.model.User;
import com.project.note.service.Interface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping()
public class HomeController {

    @Autowired
    private UserService userService;

    //home
    @GetMapping("/home")
    public String home(Principal principal, Model model) {
        if (principal != null) {
            String username = principal.getName();
            User user = userService.findByUsername(username);
            model.addAttribute("fullName", user.getFullName());
            UserDto userDto = UserDto.from(user);
            model.addAttribute("user", userDto);
        }
        return "home/index";
    }

    @GetMapping("/")
    public String homePage() {
        return "home/index";
    }
}
