package com.tfheauth.face_auth_server.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDTO {

    private Long id;
    private String message;

    public LoginResponseDTO(Long userId, String message) {
        this.id = userId;
        this.message = message;
    }
}
