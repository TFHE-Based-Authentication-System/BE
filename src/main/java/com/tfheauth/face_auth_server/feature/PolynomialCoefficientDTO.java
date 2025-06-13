package com.tfheauth.face_auth_server.feature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
public class PolynomialCoefficientDTO {
    private BigInteger a; // 상수항
    private BigInteger b; // 1차항
    private BigInteger c; // 2차항
}
