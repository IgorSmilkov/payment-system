package com.system.payment.model.transaction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("REVERSAL")
@NoArgsConstructor
@Getter
@Setter
public class ReversalTransaction extends Transaction {
}
