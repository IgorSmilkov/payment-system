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
import com.system.payment.model.transaction.ReversalTransaction;
import com.system.payment.model.transaction.Transaction;

public class ReversalTransactionValidatorTest {

    @InjectMocks
    private ReversalTransactionValidator reversalTransactionValidator;

    @Mock
    private ReversalTransaction reversalTransaction;

    @Mock
    private AuthorizeTransaction authorizeTransaction;

    @Mock
    private Merchant merchant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsValid_whenMerchantIsConsistentAndTransactionOrderValidAndSpecificValid_thenReturnTrue() {
        // Setup
        when(merchant.getId()).thenReturn(1L);

        when(reversalTransaction.getReferenceTransaction()).thenReturn(authorizeTransaction);
        when(reversalTransaction.getMerchant()).thenReturn(merchant);
        when(authorizeTransaction.getMerchant()).thenReturn(merchant);
        when(authorizeTransaction.getStatus()).thenReturn(TransactionStatus.APPROVED);
        when(authorizeTransaction.getReferencingTransactions()).thenReturn(new HashSet<>());

        // Execution
        boolean result = reversalTransactionValidator.isValid(reversalTransaction);

        // Assertion
        assertTrue(result);
    }

    @Test
    void testIsValid_whenMerchantIsInconsistent_thenReturnFalse() {
        // Setup
        Merchant differentMerchant = mock(Merchant.class);
        when(differentMerchant.getId()).thenReturn(2L);

        when(reversalTransaction.getReferenceTransaction()).thenReturn(authorizeTransaction);
        when(reversalTransaction.getMerchant()).thenReturn(merchant);
        when(authorizeTransaction.getMerchant()).thenReturn(differentMerchant);

        // Execution
        boolean result = reversalTransactionValidator.isValid(reversalTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenTransactionOrderInvalid_thenReturnFalse() {
        // Setup
        ChargeTransaction chargeTransaction = mock(ChargeTransaction.class);
        when(merchant.getId()).thenReturn(1L);
        when(reversalTransaction.getMerchant()).thenReturn(merchant);
        when(chargeTransaction.getMerchant()).thenReturn(merchant);
        when(reversalTransaction.getReferenceTransaction()).thenReturn(chargeTransaction);

        // Execution
        boolean result = reversalTransactionValidator.isValid(reversalTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenReferenceTransactionInvalid_thenReturnFalse() {
        // Setup
        when(merchant.getId()).thenReturn(1L);
        when(reversalTransaction.getMerchant()).thenReturn(merchant);
        when(authorizeTransaction.getMerchant()).thenReturn(merchant);
        when(reversalTransaction.getReferenceTransaction()).thenReturn(authorizeTransaction);
        when(authorizeTransaction.getStatus()).thenReturn(TransactionStatus.ERROR);

        // Execution
        boolean result = reversalTransactionValidator.isValid(reversalTransaction);

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

        when(merchant.getId()).thenReturn(1L);
        when(reversalTransaction.getMerchant()).thenReturn(merchant);
        when(authorizeTransaction.getMerchant()).thenReturn(merchant);
        when(reversalTransaction.getReferenceTransaction()).thenReturn(authorizeTransaction);
        when(authorizeTransaction.getReferencingTransactions()).thenReturn(referencingTransactions);

        // Execution
        boolean result = reversalTransactionValidator.isValid(reversalTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenReferenceTransactionIsNull_thenReturnFalse() {
        // Setup
        when(reversalTransaction.getReferenceTransaction()).thenReturn(null);

        // Execution
        boolean result = reversalTransactionValidator.isValid(reversalTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsSpecificValid_whenReferenceTransactionIsNull_thenReturnFalse() {
        // Setup
        when(reversalTransaction.getReferenceTransaction()).thenReturn(null);

        // Execution
        boolean result = reversalTransactionValidator.isSpecificValid(reversalTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsTransactionOrderValid_whenReferenceTransactionIsAuthorizeTransaction_thenReturnTrue() {
        // Setup
        when(reversalTransaction.getReferenceTransaction()).thenReturn(authorizeTransaction);

        // Execution
        boolean result = reversalTransactionValidator.isTransactionOrderValid(reversalTransaction);

        // Assertion
        assertTrue(result);
    }

    @Test
    void testIsTransactionOrderValid_whenReferenceTransactionIsNotAuthorizeTransaction_thenReturnFalse() {
        // Setup
        ChargeTransaction chargeTransaction = mock(ChargeTransaction.class);
        when(reversalTransaction.getReferenceTransaction()).thenReturn(chargeTransaction);

        // Execution
        boolean result = reversalTransactionValidator.isTransactionOrderValid(reversalTransaction);

        // Assertion
        assertFalse(result);
    }
}
