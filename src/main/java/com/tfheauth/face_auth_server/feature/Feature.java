package com.tfheauth.face_auth_server.feature;

import com.tfheauth.face_auth_server.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FEATURE_ID")
    private Long id;

    @Column(nullable = false)
    private String vector;

    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", unique = true)
    private User user;
}
