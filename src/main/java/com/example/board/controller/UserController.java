package com.example.board.controller;

import com.example.board.domain.User;
import com.example.board.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    // 회원가입 폼
    @GetMapping("/signup")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("user")) model.addAttribute("user", new User());
        return "signup";
    }

    // 아이디 중복확인
    @PostMapping("/signup/check")
    @ResponseBody
    public Map<String, Object> checkUsername(@RequestParam String username) {
        boolean exists = userService.isDuplicateUsername(username);
        return Map.of("exists", exists); // { "exists": true/false }
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String register(@ModelAttribute("user") User user, Model model) {
        if (userService.isDuplicateUsername(user.getUsername())) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            user.setUsername("");                // 아이디 비우기
            model.addAttribute("user", user);    // 이름/비번 유지
            return "signup";
        }
        user.setRole("user");
        userService.saveUser(user);
        model.addAttribute("success", true);   // 성공
        model.addAttribute("user", new User()); // 폼 초기화
        return "signup";
    }

    // 로그인 폼
    @GetMapping("/login")
    public String showLoginForm() { return "login"; }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        if (userService.login(username, password)) {
            session.setAttribute("loginUser", username);

            // 사용자 객체에서 role을 가져와 세션에 저장
            String role = userService.getUserByUsername(username).getRole();
            session.setAttribute("userRole", role); // "ADMIN" or "USER"

            return "redirect:/posts";
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
            return "login";
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
