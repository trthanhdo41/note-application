package com.project.note.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/showPageError")
    public String error() {
        return "error/index";
    }
}
