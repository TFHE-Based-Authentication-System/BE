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

    // 클라이언트로부터 받은 암호문과 DB에 저장된 암호문을 비교
    // 거리 다항식의 계수들 (s², s, 상수항)을 계산해 반환
    public List<PolynomialCoefficientDTO> generateCoefficientList(FeatureCompareRequestDTO requestDTO) {
        // 1. 사용자의 저장된 feature(암호문) 조회
        Feature feature = featureRepository.findByUser_Email(requestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자의 feature가 존재하지 않습니다."));

        final BigInteger Q = BigInteger.valueOf(2).pow(120); // 암호 연산 모듈러 Q (2^120)
        int N = requestDTO.getC1().size();  // 다항식 차수

        // 2. 클라이언트에서 전달된 암호문 (c1, c2) 및 DB에 저장된 암호문 변환
        List<BigInteger> c1 = requestDTO.getC1().stream().map(BigInteger::new).toList();
        List<BigInteger> c2 = requestDTO.getC2().stream().map(BigInteger::new).toList();
        List<BigInteger> db_c1 = feature.getVector().getC1().stream().map(BigInteger::new).toList();
        List<BigInteger> db_c2 = feature.getVector().getC2().stream().map(BigInteger::new).toList();

        // 3. 암호문 간 차이 (tilde c1, tilde c2) 계산
        List<BigInteger> tildeC1 = new ArrayList<>();
        List<BigInteger> tildeC2 = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            tildeC1.add(c1.get(i).subtract(db_c1.get(i)).mod(Q));
            tildeC2.add(c2.get(i).subtract(db_c2.get(i)).mod(Q));
        }

        // 4. 거리 다항식 계수 계산

        // a = (tildeC2)^2 → s² 계수
        List<BigInteger> a = polyMulMod(tildeC2, tildeC2, Q);

        // b = 2 * (tildeC1 * tildeC2) → s 계수
        List<BigInteger> b = polyMulMod(tildeC1, tildeC2, Q).stream()
                .map(coef -> coef.multiply(BigInteger.TWO).mod(Q)).toList();

        // c = (tildeC1)^2 → 상수항
        List<BigInteger> c = polyMulMod(tildeC1, tildeC1, Q);

        // 5. 위치별로 (a_i, b_i, c_i)를 담은 DTO 생성하여 리스트로 반환
        List<PolynomialCoefficientDTO> result = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            result.add(new PolynomialCoefficientDTO(a.get(i), b.get(i), c.get(i)));
        }

        return result;
    }

    // 다항식 곱셈 함수
    // [동형암호 전용] 다항식 곱셈 후 x^N ≡ -1을 적용하고 Q로 모듈로 연산하는 함수
    // 입력: 두 다항식 a, b (길이 N), 모듈러스 Q
    // 출력: 길이 N의 결과 다항식 (a * b) mod (x^N + 1, Q)
    public static List<BigInteger> polyMulMod(List<BigInteger> a, List<BigInteger> b, BigInteger Q) {
        int N = a.size();

        // 1. 일반적인 다항식 곱 (길이 2N - 1): full[i+j] += a[i] * b[j]
        List<BigInteger> full = new ArrayList<>(Collections.nCopies(2 * N - 1, BigInteger.ZERO));
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int idx = i + j;
                full.set(idx, full.get(idx).add(a.get(i).multiply(b.get(j))));
            }
        }

        // 2. x^N ≡ -1 을 적용하여 길이 N으로 줄이기
        List<BigInteger> reduced = new ArrayList<>(Collections.nCopies(N, BigInteger.ZERO));
        for (int i = 0; i < full.size(); i++) {
            int idx = i % N;
            if (i < N) {
                reduced.set(idx, reduced.get(idx).add(full.get(i))); // i < N이면 그냥 더함
            } else {
                reduced.set(idx, reduced.get(idx).subtract(full.get(i))); // i ≥ N이면 부호 반대로 빼줌
            }
        }

        // 3. Q로 모듈로 연산하여 양의 정수로 정리 (mod Q)
        // 3. [변경] signed modulus 적용: 결과를 -Q/2 ~ Q/2 범위로 정규화
        for (int i = 0; i < N; i++) {
            BigInteger val = reduced.get(i).mod(Q);
            if (val.compareTo(Q.shiftRight(1)) > 0) { // if val > Q/2
                val = val.subtract(Q); // signed 값으로 바꿈
            }
            reduced.set(i, val);
        }

        return reduced;
    }
}
