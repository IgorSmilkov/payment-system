package com.system.payment.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.system.payment.dto.TransactionRequestDto;
import com.system.payment.dto.TransactionResponseDto;
import com.system.payment.dto.mapper.TransactionMapper;
import com.system.payment.factory.TransactionFactory;
import com.system.payment.model.CustomUserDetails;
import com.system.payment.model.Merchant;
import com.system.payment.model.TransactionStatus;
import com.system.payment.model.transaction.AuthorizeTransaction;
import com.system.payment.model.transaction.ChargeTransaction;
import com.system.payment.model.transaction.RefundTransaction;
import com.system.payment.model.transaction.ReversalTransaction;
import com.system.payment.model.transaction.Transaction;
import com.system.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final MerchantService merchantService;
    private final TransactionFactory transactionFactory;
    private final TransactionValidationService transactionValidationService;
    private final TransactionMapper transactionMapper;

    /**
     * Processes a transaction request, including validation and persistence.
     * <p>
     * This method handles the entire lifecycle of a transaction request:
     * <ul>
     *   <li>Identifies the merchant based on the currently authenticated user.</li>
     *   <li>Creates a transaction using a strategy specific to the transaction type.</li>
     *   <li>Validates the transaction.</li>
     *   <li>Saves the transaction to the database.</li>
     *   <li>Returns a DTO representing the processed transaction.</li>
     * </ul>
     * <p>
     * The method is retryable, with up to 5 attempts, to handle potential optimistic locking failures
     * that might occur due to concurrent updates.
     */
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000)
    )
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TransactionResponseDto processTransaction(TransactionRequestDto transactionDto) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Merchant merchant = merchantService.findActiveMerchantByEmail(userEmail);

        Transaction transaction = transactionFactory.createTransaction(transactionDto, merchant);
        validateAndProcessTransaction(transaction);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }


    public Page<TransactionResponseDto> getTransactionsForUser(CustomUserDetails userDetails, Pageable pageable) {
        if (userDetails.hasRole("ROLE_ADMIN")) {
            return transactionRepository.findAll(pageable)
                    .map(transactionMapper::toDto);
        }

        return transactionRepository.findByMerchantUserId(userDetails.getUserId(), pageable)
                .map(transactionMapper::toDto);
    }

    private void validateAndProcessTransaction(Transaction transaction) {
        if (!transactionValidationService.isValidTransaction(transaction)) {
            transaction.setStatus(TransactionStatus.ERROR);
            return;
        }

        updateReferencedTransactionStatus(transaction);
        transaction.setStatus(TransactionStatus.APPROVED);
        updateMerchantTransactionSum(transaction);
    }

    /**
     * Updates the status of a referenced transaction based on the type of the current transaction.
     * <p>
     * This method is specifically responsible for updating the status of transactions
     * that are referenced by refund or reversal transactions. It ensures that the referenced
     * transactions reflect the correct status after the refund or reversal operation.
     *
     * @param transaction the transaction whose referenced transaction status needs to be updated.
     */
    private void updateReferencedTransactionStatus(Transaction transaction) {
        if (transaction instanceof RefundTransaction refundTransaction) {
            updateRefundTransactionStatus(refundTransaction);
        } else if (transaction instanceof ReversalTransaction reversalTransaction) {
            updateReversalTransactionStatus(reversalTransaction);
        }
    }

    private void updateRefundTransactionStatus(RefundTransaction refundTransaction) {
        ChargeTransaction referencedTransaction = (ChargeTransaction) refundTransaction.getReferenceTransaction();
        referencedTransaction.setStatus(TransactionStatus.REFUNDED);
        transactionRepository.save(referencedTransaction);
    }

    private void updateReversalTransactionStatus(ReversalTransaction reversalTransaction) {
        AuthorizeTransaction referencedTransaction = (AuthorizeTransaction) reversalTransaction.getReferenceTransaction();
        referencedTransaction.setStatus(TransactionStatus.REVERSED);
        transactionRepository.save(referencedTransaction);
    }

    /**
     * Updates the total transaction sum for the merchant based on the type of the current transaction.
     * <p>
     * This method handles the adjustment of the merchant's total transaction sum by adding
     * the amount for a successful {@code ChargeTransaction} or subtracting the amount for a
     * successful {@code RefundTransaction}.
     *
     * @param transaction the transaction whose amount needs to update the merchant's total sum.
     */
    private void updateMerchantTransactionSum(Transaction transaction) {
        Merchant merchant = transaction.getMerchant();

        if (transaction instanceof ChargeTransaction chargeTransaction && transaction.getStatus() == TransactionStatus.APPROVED) {
            merchant.setTotalTransactionSum(
                    merchant.getTotalTransactionSum().add(chargeTransaction.getAmount())
            );
        } else if (transaction instanceof RefundTransaction refundTransaction && transaction.getStatus() == TransactionStatus.APPROVED) {
            merchant.setTotalTransactionSum(
                    merchant.getTotalTransactionSum().subtract(refundTransaction.getAmount())
            );
        }

        merchantService.saveMerchant(merchant);
    }
}