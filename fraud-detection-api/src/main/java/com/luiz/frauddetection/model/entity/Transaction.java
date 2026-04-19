package com.luiz.frauddetection.model.entity;

import com.luiz.frauddetection.model.Enum.Device;
import com.luiz.frauddetection.model.Enum.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "A transaction não pode ter user null")
    private User user;

    @Getter @Setter
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Getter @Setter
    @Column(nullable = false, length = 2)
    private String location;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Device device;

    @Getter @Setter
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime transactionTime;

    @Getter @Setter
    @Column(nullable = false)
    @Min(value = 0, message = "O score de risco não pode ser menor que 0")
    @Max(value = 100, message = "O score de risco não pode ser maior que 100")
    @NonNull
    private Integer riskScore;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private Status status;
}
