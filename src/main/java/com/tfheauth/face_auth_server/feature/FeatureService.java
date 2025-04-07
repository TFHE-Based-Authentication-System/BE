package com.tfheauth.face_auth_server.feature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfheauth.face_auth_server.user.User;
import com.tfheauth.face_auth_server.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public FeatureService(FeatureRepository featureRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.featureRepository = featureRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    // 클라이언트로부터 임베딩 벡터값 수신받아 저장
    public void saveFeature(Long userId, FeatureRequestDTO featureRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 임베딩을 JSON 형태로 저장
        String vectorJson;
        try {
            vectorJson = objectMapper.writeValueAsString(featureRequestDTO.getEmbedding());
        } catch (Exception e) {
            throw new RuntimeException("Vector 변환 실패", e);
        }

        Feature feature = Feature.builder()
                .user(user)
                .vector(vectorJson)
                .build();

        featureRepository.save(feature);
    }
}
