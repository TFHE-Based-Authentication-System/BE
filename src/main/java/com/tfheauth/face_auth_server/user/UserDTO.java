package com.tfheauth.face_auth_server.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDTO {

    private Long id;
    private String email;
    private String name;
    private String password;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private int status;
}
