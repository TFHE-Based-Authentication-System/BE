package com.tfheauth.face_auth_server.user;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public String joinUser(@RequestBody UserDTO userDTO) {
        userService.join(userDTO);
        return "회원가입이 완료되었습니다.";
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody UserDTO userDTO) {
        userService.login(userDTO.getEmail(), userDTO.getPassword());
        return "로그인 성공";
    }

    @GetMapping("/main")
    public UserInfoDTO showUserInfo(@RequestParam Long id) {
        return userService.getUserInfo(id);
    }
}
