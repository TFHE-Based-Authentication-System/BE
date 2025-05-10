package com.tfheauth.face_auth_server.feature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfheauth.face_auth_server.user.User;
import com.tfheauth.face_auth_server.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;
    private final UserRepository userRepository;

    public FeatureService(FeatureRepository featureRepository, UserRepository userRepository) {
        this.featureRepository = featureRepository;
        this.userRepository = userRepository;
    }

    // 클라이언트로부터 임베딩 벡터값 수신받아 저장
    @Transactional
    public void saveFeature(FeatureRequestDTO featureRequestDTO) {
        User user = userRepository.findById(featureRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Feature feature = new Feature();
        feature.setUser(user);
        feature.setVector(featureRequestDTO.getVector());
        feature.setCreated_at(LocalDateTime.now());

        featureRepository.save(feature);
    }
}
