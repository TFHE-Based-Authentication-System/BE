package com.tfheauth.face_auth_server.feature;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/features")
public class FeatureController {

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @PostMapping
    public ResponseEntity<String> saveFeature(@RequestBody FeatureRequestDTO featureRequestDTO) {
        featureService.saveFeature(featureRequestDTO);
        return ResponseEntity.ok("Feature 저장 완료");
    }
}
