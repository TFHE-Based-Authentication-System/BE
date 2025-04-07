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

    @PostMapping("/{userId}")
    public ResponseEntity<String> saveFeature(@PathVariable Long userId, @RequestBody FeatureRequestDTO featureRequestDTO) {
        featureService.saveFeature(userId, featureRequestDTO);
        return ResponseEntity.ok("Feature 저장 완료");
    }

}
