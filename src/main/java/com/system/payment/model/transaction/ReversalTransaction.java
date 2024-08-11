package com.system.payment.model.transaction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("REVERSAL")
@NoArgsConstructor
@Getter
@Setter
public class ReversalTransaction extends Transaction {

    @ManyToOne
    @JoinColumn(name = "reference_id")
    private Transaction referenceTransaction;
}
