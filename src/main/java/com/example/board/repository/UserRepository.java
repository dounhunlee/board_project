package com.example.board.repository;

import com.example.board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 로그인 시 사용할 메서드
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
