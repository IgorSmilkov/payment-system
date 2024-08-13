package com.system.payment.strategy.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.system.payment.model.Merchant;
import com.system.payment.model.TransactionStatus;
import com.system.payment.model.transaction.AuthorizeTransaction;
import com.system.payment.model.transaction.ChargeTransaction;
import com.system.payment.model.transaction.RefundTransaction;
import com.system.payment.model.transaction.Transaction;

public class RefundTransactionValidatorTest {

    @InjectMocks
    private RefundTransactionValidator refundTransactionValidator;

    @Mock
    private RefundTransaction refundTransaction;

    @Mock
    private ChargeTransaction chargeTransaction;

    @Mock
    private Merchant merchant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsValid_whenMerchantIsConsistentAndTransactionOrderValidAndSpecificValid_thenReturnTrue() {
        // Setup
        Merchant merchant = mock(Merchant.class);
        when(merchant.getId()).thenReturn(1L);

        chargeTransaction = mock(ChargeTransaction.class);
        when(refundTransaction.getReferenceTransaction()).thenReturn(chargeTransaction);
        when(refundTransaction.getMerchant()).thenReturn(merchant);
        when(chargeTransaction.getMerchant()).thenReturn(merchant);
        when(chargeTransaction.getStatus()).thenReturn(TransactionStatus.APPROVED);
        when(chargeTransaction.getAmount()).thenReturn(new BigDecimal("100.00"));
        when(refundTransaction.getAmount()).thenReturn(new BigDecimal("100.00"));
        when(chargeTransaction.getReferencingTransactions()).thenReturn(new HashSet<>());

        // Execution
        boolean result = refundTransactionValidator.isValid(refundTransaction);

        // Assertion
        assertTrue(result);
    }

    @Test
    void testIsValid_whenMerchantIsInconsistent_thenReturnFalse() {
        // Setup
        Merchant differentMerchant = mock(Merchant.class);
        when(differentMerchant.getId()).thenReturn(2L);

        when(refundTransaction.getReferenceTransaction()).thenReturn(chargeTransaction);
        when(refundTransaction.getMerchant()).thenReturn(merchant);
        when(chargeTransaction.getMerchant()).thenReturn(differentMerchant);

        // Execution
        boolean result = refundTransactionValidator.isValid(refundTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenTransactionOrderInvalid_thenReturnFalse() {
        // Setup
        AuthorizeTransaction authorizeTransaction = mock(AuthorizeTransaction.class);
        Merchant merchant = mock(Merchant.class);
        when(merchant.getId()).thenReturn(1L);
        when(refundTransaction.getMerchant()).thenReturn(merchant);
        when(authorizeTransaction.getMerchant()).thenReturn(merchant);
        when(refundTransaction.getReferenceTransaction()).thenReturn(authorizeTransaction);

        // Execution
        boolean result = refundTransactionValidator.isValid(refundTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenReferenceTransactionInvalid_thenReturnFalse() {
        // Setup
        Merchant merchant = mock(Merchant.class);
        when(merchant.getId()).thenReturn(1L);
        when(refundTransaction.getMerchant()).thenReturn(merchant);
        when(chargeTransaction.getMerchant()).thenReturn(merchant);
        when(refundTransaction.getReferenceTransaction()).thenReturn(chargeTransaction);
        when(chargeTransaction.getStatus()).thenReturn(TransactionStatus.ERROR);

        // Execution
        boolean result = refundTransactionValidator.isValid(refundTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenTransactionAlreadyReferenced_thenReturnFalse() {
        // Setup
        Set<Transaction> referencingTransactions = new HashSet<>();
        Transaction anotherTransaction = mock(Transaction.class);
        when(anotherTransaction.getStatus()).thenReturn(TransactionStatus.APPROVED);
        referencingTransactions.add(anotherTransaction);
        Merchant merchant = mock(Merchant.class);
        when(merchant.getId()).thenReturn(1L);
        when(refundTransaction.getMerchant()).thenReturn(merchant);
        when(chargeTransaction.getMerchant()).thenReturn(merchant);
        when(refundTransaction.getReferenceTransaction()).thenReturn(chargeTransaction);
        when(chargeTransaction.getReferencingTransactions()).thenReturn(referencingTransactions);

        // Execution
        boolean result = refundTransactionValidator.isValid(refundTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenRefundAmountDoesNotMatchChargeAmount_thenReturnFalse() {
        // Setup
        Merchant merchant = mock(Merchant.class);
        when(merchant.getId()).thenReturn(1L);
        when(refundTransaction.getMerchant()).thenReturn(merchant);
        when(chargeTransaction.getMerchant()).thenReturn(merchant);
        when(refundTransaction.getReferenceTransaction()).thenReturn(chargeTransaction);
        when(chargeTransaction.getStatus()).thenReturn(TransactionStatus.APPROVED);
        when(chargeTransaction.getAmount()).thenReturn(new BigDecimal("100.00"));
        when(refundTransaction.getAmount()).thenReturn(new BigDecimal("50.00"));

        // Execution
        boolean result = refundTransactionValidator.isValid(refundTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenReferenceTransactionIsNull_thenReturnFalse() {
        // Setup
        when(refundTransaction.getReferenceTransaction()).thenReturn(null);

        // Execution
        boolean result = refundTransactionValidator.isValid(refundTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsSpecificValid_whenReferenceTransactionIsNull_thenReturnFalse() {
        // Setup
        when(refundTransaction.getReferenceTransaction()).thenReturn(null);

        // Execution
        boolean result = refundTransactionValidator.isSpecificValid(refundTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsTransactionOrderValid_whenReferenceTransactionIsChargeTransaction_thenReturnTrue() {
        // Setup
        when(refundTransaction.getReferenceTransaction()).thenReturn(chargeTransaction);

        // Execution
        boolean result = refundTransactionValidator.isTransactionOrderValid(refundTransaction);

        // Assertion
        assertTrue(result);
    }

    @Test
    void testIsTransactionOrderValid_whenReferenceTransactionIsNotChargeTransaction_thenReturnFalse() {
        // Setup
        AuthorizeTransaction authorizeTransaction = mock(AuthorizeTransaction.class);
        when(refundTransaction.getReferenceTransaction()).thenReturn(authorizeTransaction);

        // Execution
        boolean result = refundTransactionValidator.isTransactionOrderValid(refundTransaction);

        // Assertion
        assertFalse(result);
    }
}
