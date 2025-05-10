package com.tfheauth.face_auth_server.feature;

import com.tfheauth.face_auth_server.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FEATURE_ID")
    private Long id;

    @Convert(converter = VectorConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private Vector vector;

    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at;

    @OneToOne
    @JoinColumn(name = "USER_EMAIL", referencedColumnName = "EMAIL", unique = true)
    private User user;
}
