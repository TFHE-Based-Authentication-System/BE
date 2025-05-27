package com.tfheauth.face_auth_server.feature;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeatureCompareRequestDTO {

    private String email;
    private List<Double> c1;
    private List<Double> c2;
}
