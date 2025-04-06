package com.tfheauth.face_auth_server.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    private String email;
    private String name;
    private String password;
}
