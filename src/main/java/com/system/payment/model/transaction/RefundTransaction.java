package com.system.payment.model.transaction;

import java.math.BigDecimal;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("REFUND")
@NoArgsConstructor
@Getter
@Setter
public class RefundTransaction extends Transaction {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "reference_id")
    private Transaction referenceTransaction;

}
