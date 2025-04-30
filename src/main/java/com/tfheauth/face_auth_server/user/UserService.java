package com.tfheauth.face_auth_server.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    public void join(SignupRequestDTO signupRequestDTO) {
        if (userRepository.findByEmail(signupRequestDTO.getEmail()).isPresent()) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        User user = new User();
        user.setEmail(signupRequestDTO.getEmail());
        user.setName(signupRequestDTO.getName());
        user.setPassword(passwordEncoder.encode(signupRequestDTO.getPassword()));
        userRepository.save(user);
    }

    // 로그인
    public void login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 잘못되었습니다."));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 잘못되었습니다.");
        }
    }

    // 사용자 정보 조회 (name, email만 반환)
    public UserResponseDTO getUserInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());

        return userDTO;
    }

    //사용자 이름 수정
    public UserResponseDTO updateUserName(Long id, UserResponseDTO userResponseDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        user.setName(userResponseDTO.getName());
        userRepository.save(user);

        return userResponseDTO;
    }

}
