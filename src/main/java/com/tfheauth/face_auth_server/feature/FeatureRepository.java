package com.tfheauth.face_auth_server.feature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {
    // user의 email을 기준으로 Feature 조회
    Optional<Feature> findByUser_Email(String email);
}

