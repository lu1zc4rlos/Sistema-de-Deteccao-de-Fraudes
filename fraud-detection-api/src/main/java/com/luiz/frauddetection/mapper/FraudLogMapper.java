package com.luiz.frauddetection.mapper;

import com.luiz.frauddetection.model.dto.fraud.FraudLogResponse;
import com.luiz.frauddetection.model.entity.FraudLog;
import org.springframework.stereotype.Component;

@Component
public class FraudLogMapper {

    public FraudLogResponse toResponse(FraudLog log){

        FraudLogResponse fraudLogResponse = new FraudLogResponse();
        fraudLogResponse.setReason(log.getReason());
        fraudLogResponse.setCreatedAt(log.getCreatedAt());

        return fraudLogResponse;
    }
}
