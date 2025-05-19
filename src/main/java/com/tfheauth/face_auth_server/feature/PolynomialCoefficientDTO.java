package com.tfheauth.face_auth_server.feature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PolynomialCoefficientDTO {
    private double a; // 상수항
    private double b; // 1차항
    private double c; // 2차항
}
