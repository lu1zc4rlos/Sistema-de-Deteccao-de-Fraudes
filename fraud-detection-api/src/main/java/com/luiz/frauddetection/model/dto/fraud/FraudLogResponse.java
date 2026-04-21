package com.luiz.frauddetection.model.dto.fraud;

import com.luiz.frauddetection.model.Enum.FraudReason;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class FraudLogResponse {

    private FraudReason reason;
    private LocalDateTime createdAt;
}
