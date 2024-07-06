package com.project.note.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoadingController {
    @GetMapping("/loading-for-login")
    public String showLoadingPage1() {
        return "loading/index1";
    }

    @GetMapping("/loading-for-register")
    public String showLoadingPage2() {
        return "loading/index2";
    }
}
