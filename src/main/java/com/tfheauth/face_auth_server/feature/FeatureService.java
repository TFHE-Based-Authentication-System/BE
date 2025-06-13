package com.tfheauth.face_auth_server.feature;

import com.tfheauth.face_auth_server.user.User;
import com.tfheauth.face_auth_server.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // 클라이언트로부터 받은 벡터값 비교 판별
    public List<PolynomialCoefficientDTO> generateCoefficientList(FeatureCompareRequestDTO requestDTO) {
        Feature feature = featureRepository.findByUser_Email(requestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자의 feature가 존재하지 않습니다."));

        final BigInteger Q = BigInteger.valueOf(2).pow(120);

        Vector stored = feature.getVector();

        List<String> c1 = requestDTO.getC1();
        List<String> c2 = requestDTO.getC2();
        List<String> db_c1 = stored.getC1();
        List<String> db_c2 = stored.getC2();

        List<PolynomialCoefficientDTO> result = new ArrayList<>();

        for (int i = 0; i < c1.size(); i++) {
            // BigInteger로 변환
            BigInteger tildeC1 = new BigInteger(c1.get(i)).subtract(new BigInteger(db_c1.get(i))).mod(Q);
            BigInteger tildeC2 = new BigInteger(c2.get(i)).subtract(new BigInteger(db_c2.get(i))).mod(Q);

            BigInteger a = tildeC2.pow(2).mod(Q);  // s^2 계수
            BigInteger b = tildeC1.multiply(tildeC2).multiply(BigInteger.TWO).mod(Q);  // s 계수
            BigInteger c = tildeC1.pow(2).mod(Q);  // 상수항

            result.add(new PolynomialCoefficientDTO(a, b, c));
        }

        return result;
    }
}
