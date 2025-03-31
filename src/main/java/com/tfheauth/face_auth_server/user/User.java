package com.tfheauth.face_auth_server.user;

import com.tfheauth.face_auth_server.feature.Feature;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;
    private String password;
    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();

    @ColumnDefault("0")
    private int status;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Feature feature;
}
