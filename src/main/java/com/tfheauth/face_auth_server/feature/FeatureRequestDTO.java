package com.tfheauth.face_auth_server.feature;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeatureRequestDTO {

    private List<Double> embedding;
}
