package com.tfheauth.face_auth_server.feature;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Embeddable
public class Vector {

    private List<Double> c1;
    private List<Double> c2;
}
