package com.tfheauth.face_auth_server.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDTO {

    private String email;
    private String name;
    private String password;
}
