package com.example.board.controller;

import com.example.board.domain.Post;
import com.example.board.domain.User;
import com.example.board.repository.UserRepository;
import com.example.board.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.board.service.UserService;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final UserService userService;
    private final PostService postService;

    public PostController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping
    public String listPosts(HttpSession session, Model model) {
        String loginUser = (String) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        User user = userService.getUserByUsername(loginUser);
        model.addAttribute("name", user.getName());
        model.addAttribute("username", loginUser);
        model.addAttribute("posts", postService.getAllPosts());

        return "post_list";
    }

    // 글쓰기 폼 페이지
    @GetMapping("/new")
    public String showPostForm(HttpSession session, Model model) {
        String loginUser = (String) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("post", new Post());
        return "post_form";
    }

    // 글쓰기 처리
    @PostMapping("/new")
    public String createPost(@ModelAttribute Post post, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        post.setAuthor(loginUser); // 작성자는 세션에서 자동 설정
        postService.savePost(post);
        return "redirect:/posts";
    }

    // 상세보기
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, HttpSession session, Model model) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        User user = userService.getUserByUsername(loginUser);
        Post post = postService.getPostById(id);
        if (post == null) {
            return "redirect:/posts"; // 없는 글이면 목록으로
        }

        model.addAttribute("post", post);
        model.addAttribute("loginUser", loginUser); // 나중에 수정/삭제 조건에 씀
        model.addAttribute("userRole", user.getRole()); // 역할 전달
        return "post_detail";
    }

    // 수정 폼 보기
    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id, HttpSession session, Model model) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        Post post = postService.getPostById(id);
        if (post == null || !loginUser.equals(post.getAuthor())) {
            return "redirect:/posts"; // 존재하지 않거나, 작성자가 아님
        }

        model.addAttribute("post", post);
        return "post_edit";
    }

    // 수정 처리
    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post updatedPost, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        Post post = postService.getPostById(id);
        if (post == null || !loginUser.equals(post.getAuthor())) {
            return "redirect:/posts";
        }

        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        postService.savePost(post); // 업데이트 처리

        return "redirect:/posts/" + id;
    }

    // 삭제
    @GetMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        Post post = postService.getPostById(id);
        User user = userService.getUserByUsername(loginUser);
        // 본인 글 또는 관리자만 삭제 가능
        if (post == null || (!loginUser.equals(post.getAuthor()) && !"ADMIN".equals(user.getRole()))) {
            return "redirect:/posts";
        }

        postService.deletePost(id);
        return "redirect:/posts";
    }


}

