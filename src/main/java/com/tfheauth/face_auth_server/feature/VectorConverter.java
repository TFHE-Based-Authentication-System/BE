package com.tfheauth.face_auth_server.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// 객체 ↔ JSON 변환기
@Converter
public class VectorConverter implements AttributeConverter<Vector, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Vector attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Vector 객체를 JSON으로 변환 실패", e);
        }
    }

    @Override
    public Vector convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, Vector.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON을 Vector 객체로 변환 실패", e);
        }
    }
}
