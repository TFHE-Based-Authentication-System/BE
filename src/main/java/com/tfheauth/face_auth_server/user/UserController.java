package com.tfheauth.face_auth_server.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> joinUser(@RequestBody SignupRequestDTO signupRequestDTO) {
        userService.join(signupRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        User user = userService.login(loginRequestDTO);
        LoginResponseDTO response = new LoginResponseDTO(user.getId(), "로그인 성공");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/main")
    public UserResponseDTO showUserInfo(@RequestParam Long id) {
        return userService.getUserInfo(id);
    }

    @PatchMapping("/update")
    public UserResponseDTO updateUserName(@RequestParam Long id, @RequestBody UserResponseDTO userResponseDTO) {
        return userService.updateUserName(id, userResponseDTO);
    }
}
