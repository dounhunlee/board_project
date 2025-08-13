package com.example.board.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        String loginUser = (String) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", loginUser);
        return "home";
    }
}
