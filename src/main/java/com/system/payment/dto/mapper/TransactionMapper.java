package com.system.payment.dto.mapper;

import org.mapstruct.Builder;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import com.system.payment.dto.TransactionResponseDto;
import com.system.payment.model.TransactionType;
import com.system.payment.model.transaction.AuthorizeTransaction;
import com.system.payment.model.transaction.ChargeTransaction;
import com.system.payment.model.transaction.RefundTransaction;
import com.system.payment.model.transaction.ReversalTransaction;
import com.system.payment.model.transaction.Transaction;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        builder = @Builder(disableBuilder = true))
public interface TransactionMapper {

    @Mapping(target = "referenceTransactionId", expression = "java(transaction.getReferenceTransaction() != null ? transaction.getReferenceTransaction().getId() : null)")
    @Mapping(target = "type", expression = "java(determineTransactionType(transaction))")
    @Mapping(target = "merchant.id", source = "transaction.merchant.id")
    @Mapping(target = "merchant.name", source = "transaction.merchant.user.name")
    @Mapping(target = "merchant.email", source = "transaction.merchant.user.email")
    @Mapping(target = "merchant.status", source = "transaction.merchant.user.status")
    TransactionResponseDto toDto(Transaction transaction);

    default TransactionType determineTransactionType(Transaction transaction) {
        if (transaction instanceof AuthorizeTransaction) {
            return TransactionType.AUTHORIZE;
        } else if (transaction instanceof ChargeTransaction) {
            return TransactionType.CHARGE;
        } else if (transaction instanceof RefundTransaction) {
            return TransactionType.REFUND;
        } else if (transaction instanceof ReversalTransaction) {
            return TransactionType.REVERSAL;
        }
        throw new IllegalArgumentException("Unknown transaction type");
    }
}

