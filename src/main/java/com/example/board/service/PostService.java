package com.example.board.service;

import com.example.board.domain.Post;
import com.example.board.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 전체 게시글 목록 조회
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 게시글 저장
    public void savePost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    // 게시글 하나 조회 (수정/상세용)
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    // 게시글 삭제
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
