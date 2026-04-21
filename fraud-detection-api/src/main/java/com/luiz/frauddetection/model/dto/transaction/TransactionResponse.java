package com.luiz.frauddetection.model.dto.transaction;

import com.luiz.frauddetection.model.Enum.Device;
import com.luiz.frauddetection.model.Enum.Status;
import com.luiz.frauddetection.model.dto.fraud.FraudLogResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private String location;
    private Device device;
    private LocalDateTime timestamp;
    private Integer riskScore;
    private Status status;

    private List<FraudLogResponse> fraudLogs = new ArrayList<>();
}
