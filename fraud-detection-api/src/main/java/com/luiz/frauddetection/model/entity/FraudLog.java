package com.luiz.frauddetection.model.entity;

import com.luiz.frauddetection.model.Enum.FraudReason;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "fraud_logs")
@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor
@Entity
public class FraudLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private final Long id;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    @NotNull(message = "A transaction não pode ter user null")
    @NonNull
    private Transaction transaction;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "A reason não pode ter user null")
    @NonNull
    private FraudReason reason;

    @Getter @Setter
    @CreatedDate
    @Column(nullable = false, updatable = false)
    @NonNull
    private LocalDateTime createdAt;
}
