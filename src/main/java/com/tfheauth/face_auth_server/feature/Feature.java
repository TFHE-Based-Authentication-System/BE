package com.tfheauth.face_auth_server.feature;

import com.tfheauth.face_auth_server.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FEATURE_ID")
    private Long id;

    @Column(nullable = false)
    private String vector;

    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at;

    @OneToOne
    @JoinColumn(name = "USER_EMAIL", referencedColumnName = "EMAIL", unique = true)
    private User user;
}
