package com.example.board.service;

import com.example.board.domain.User;
import com.example.board.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    // 암호화 기능 사용
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isDuplicateUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /** 회원 저장 비밀번호를 암호화로 저장 */
    public void saveUser(User user) {

        // 비밀번호 암호화
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);

        userRepository.save(user);
    }

    /** 로그인 검증 평문과 암호화 비교 */
    public boolean login(String username, String rawPassword) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) return false;

        User u = opt.get();
        return passwordEncoder.matches(rawPassword, u.getPassword());
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
