package com.system.payment.strategy.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
import com.system.payment.model.transaction.ReversalTransaction;
import com.system.payment.model.transaction.Transaction;

public class ChargeTransactionValidatorTest {

    @InjectMocks
    private ChargeTransactionValidator chargeTransactionValidator;

    @Mock
    private ChargeTransaction chargeTransaction;

    @Mock
    private Transaction referenceTransaction;

    @Mock
    private Merchant merchant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        referenceTransaction = mock(AuthorizeTransaction.class);
    }

    @Test
    void testIsValid_whenMerchantIsConsistentAndTransactionOrderValidAndSpecificValid_thenReturnTrue() {
        // Setup
        Merchant merchant = mock(Merchant.class);
        when(merchant.getId()).thenReturn(1L);

        when(chargeTransaction.getReferenceTransaction()).thenReturn(referenceTransaction);
        when(chargeTransaction.getMerchant()).thenReturn(merchant);
        when(referenceTransaction.getMerchant()).thenReturn(merchant);
        when(referenceTransaction.getStatus()).thenReturn(TransactionStatus.APPROVED);
        when(referenceTransaction.getReferencingTransactions()).thenReturn(new HashSet<>());

        // Execution
        boolean result = chargeTransactionValidator.isValid(chargeTransaction);

        // Assertion
        assertTrue(result);
    }

    @Test
    void testIsValid_whenMerchantIsInconsistent_thenReturnFalse() {
        // Setup
        Merchant differentMerchant = mock(Merchant.class);
        when(differentMerchant.getId()).thenReturn(2L);

        when(chargeTransaction.getReferenceTransaction()).thenReturn(referenceTransaction);
        when(chargeTransaction.getMerchant()).thenReturn(merchant);
        when(referenceTransaction.getMerchant()).thenReturn(differentMerchant);

        // Execution
        boolean result = chargeTransactionValidator.isValid(chargeTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenTransactionOrderInvalid_thenReturnFalse() {
        // Setup
        referenceTransaction = mock(RefundTransaction.class);
        when(chargeTransaction.getReferenceTransaction()).thenReturn(referenceTransaction);

        assertFalse(chargeTransactionValidator.isTransactionOrderValid(chargeTransaction));
    }

    @Test
    void testIsValid_whenReferenceTransactionInvalid_thenReturnFalse() {
        // Setup
        Merchant merchant = mock(Merchant.class);
        when(merchant.getId()).thenReturn(1L);
        when(chargeTransaction.getMerchant()).thenReturn(merchant);
        when(referenceTransaction.getMerchant()).thenReturn(merchant);
        when(chargeTransaction.getReferenceTransaction()).thenReturn(referenceTransaction);
        when(referenceTransaction.getStatus()).thenReturn(TransactionStatus.ERROR);

        // Execution
        boolean result = chargeTransactionValidator.isValid(chargeTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenTransactionAlreadyReferenced_thenReturnFalse() {
        // Setup
        Merchant merchant = mock(Merchant.class);
        when(merchant.getId()).thenReturn(1L);

        Set<Transaction> referencingTransactions = new HashSet<>();
        Transaction anotherTransaction = mock(Transaction.class);
        when(anotherTransaction.getStatus()).thenReturn(TransactionStatus.APPROVED); // Add a valid transaction
        referencingTransactions.add(anotherTransaction);

        when(chargeTransaction.getReferenceTransaction()).thenReturn(referenceTransaction);
        when(chargeTransaction.getMerchant()).thenReturn(merchant);
        when(referenceTransaction.getMerchant()).thenReturn(merchant);
        when(referenceTransaction.getReferencingTransactions()).thenReturn(referencingTransactions);

        // Execution
        boolean result = chargeTransactionValidator.isValid(chargeTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenReferenceTransactionIsNull_thenReturnFalse() {
        // Setup
        when(chargeTransaction.getReferenceTransaction()).thenReturn(null);
        when(chargeTransaction.getMerchant()).thenReturn(merchant);

        // Execution
        boolean result = chargeTransactionValidator.isValid(chargeTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsSpecificValid_whenReferenceTransactionIsNull_thenReturnFalse() {
        // Setup
        when(chargeTransaction.getReferenceTransaction()).thenReturn(null);

        // Execution
        boolean result = chargeTransactionValidator.isSpecificValid(chargeTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsTransactionOrderValid_whenReferenceTransactionIsAuthorizeTransaction_thenReturnTrue() {
        // Setup
        referenceTransaction = mock(AuthorizeTransaction.class);
        when(chargeTransaction.getReferenceTransaction()).thenReturn(referenceTransaction);

        // Execution
        boolean result = chargeTransactionValidator.isTransactionOrderValid(chargeTransaction);

        // Assertion
        assertTrue(result);
    }

    @Test
    void testIsTransactionOrderValid_whenReferenceTransactionIsNotAuthorizeTransaction_thenReturnFalse() {
        // Setup
        referenceTransaction = mock(ReversalTransaction.class);
        when(chargeTransaction.getReferenceTransaction()).thenReturn(referenceTransaction);

        // Execution
        boolean result = chargeTransactionValidator.isTransactionOrderValid(chargeTransaction);

        // Assertion
        assertFalse(result);
    }
}