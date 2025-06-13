package com.tfheauth.face_auth_server.feature;

import com.tfheauth.face_auth_server.user.User;
import com.tfheauth.face_auth_server.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        int N = requestDTO.getC1().size();  // 다항식 차수

        List<BigInteger> c1 = requestDTO.getC1().stream().map(BigInteger::new).toList();
        List<BigInteger> c2 = requestDTO.getC2().stream().map(BigInteger::new).toList();

        List<BigInteger> db_c1 = feature.getVector().getC1().stream().map(BigInteger::new).toList();
        List<BigInteger> db_c2 = feature.getVector().getC2().stream().map(BigInteger::new).toList();

        // tilde c1, c2
        List<BigInteger> tildeC1 = new ArrayList<>();
        List<BigInteger> tildeC2 = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            tildeC1.add(c1.get(i).subtract(db_c1.get(i)).mod(Q));
            tildeC2.add(c2.get(i).subtract(db_c2.get(i)).mod(Q));
        }

        // 다항식 곱 연산
        List<BigInteger> a = polyMulMod(tildeC2, tildeC2, Q); // s^2 계수
        List<BigInteger> b = polyMulMod(tildeC1, tildeC2, Q).stream()
                .map(coef -> coef.multiply(BigInteger.TWO).mod(Q)).toList(); // s 계수
        List<BigInteger> c = polyMulMod(tildeC1, tildeC1, Q); // 상수항

        // 각 계수의 위치별로 DTO 생성
        List<PolynomialCoefficientDTO> result = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            result.add(new PolynomialCoefficientDTO(a.get(i), b.get(i), c.get(i)));
        }

        return result;
    }

    // 다항식 곱셈 함수
    public static List<BigInteger> polyMulMod(List<BigInteger> a, List<BigInteger> b, BigInteger Q) {
        int N = a.size();
        List<BigInteger> full = new ArrayList<>(Collections.nCopies(2 * N - 1, BigInteger.ZERO));

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int idx = i + j;
                full.set(idx, full.get(idx).add(a.get(i).multiply(b.get(j))));
            }
        }

        // x^N ≡ -1 줄이기
        List<BigInteger> reduced = new ArrayList<>(Collections.nCopies(N, BigInteger.ZERO));
        for (int i = 0; i < full.size(); i++) {
            int idx = i % N;
            if (i < N) {
                reduced.set(idx, reduced.get(idx).add(full.get(i)));
            } else {
                reduced.set(idx, reduced.get(idx).subtract(full.get(i)));
            }
        }

        // mod Q 처리
        for (int i = 0; i < N; i++) {
            BigInteger val = reduced.get(i).mod(Q);
            if (val.signum() < 0) val = val.add(Q);
            reduced.set(i, val);
        }

        return reduced;
    }
}
