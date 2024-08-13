package com.system.payment.strategy.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.system.payment.model.Merchant;
import com.system.payment.model.transaction.AuthorizeTransaction;
import com.system.payment.model.transaction.Transaction;

public class AuthorizeTransactionValidatorTest {

    @InjectMocks
    private AuthorizeTransactionValidator authorizeTransactionValidator;

    @Mock
    private AuthorizeTransaction authorizeTransaction;

    @Mock
    private Transaction referenceTransaction;

    @Mock
    private Merchant merchant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsValid_whenMerchantIsConsistentAndTransactionOrderValid_thenReturnTrue() {
        // Setup
        when(merchant.getId()).thenReturn(1L);
        when(authorizeTransaction.getMerchant()).thenReturn(merchant);
        when(authorizeTransaction.getReferenceTransaction()).thenReturn(null);

        // Execution
        boolean result = authorizeTransactionValidator.isValid(authorizeTransaction);

        // Assertion
        assertTrue(result);
    }

    @Test
    void testIsValid_whenMerchantIsInconsistent_thenReturnFalse() {
        // Setup
        Merchant differentMerchant = mock(Merchant.class);
        when(differentMerchant.getId()).thenReturn(2L);

        when(referenceTransaction.getMerchant()).thenReturn(differentMerchant);
        when(authorizeTransaction.getMerchant()).thenReturn(merchant);
        when(authorizeTransaction.getReferenceTransaction()).thenReturn(referenceTransaction);

        // Execution
        boolean result = authorizeTransactionValidator.isValid(authorizeTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenTransactionOrderInvalid_thenReturnFalse() {
        // Setup
        Merchant merchant = mock(Merchant.class);
        when(merchant.getId()).thenReturn(1L);
        when(authorizeTransaction.getMerchant()).thenReturn(merchant);
        when(referenceTransaction.getMerchant()).thenReturn(merchant);
        when(authorizeTransaction.getReferenceTransaction()).thenReturn(referenceTransaction);

        // Execution
        boolean result = authorizeTransactionValidator.isValid(authorizeTransaction);

        // Assertion
        assertFalse(result);
    }

    @Test
    void testIsValid_whenReferenceTransactionIsNull_thenReturnTrue() {
        // Setup
        when(merchant.getId()).thenReturn(1L);
        when(authorizeTransaction.getMerchant()).thenReturn(merchant);
        when(authorizeTransaction.getReferenceTransaction()).thenReturn(null);

        // Execution
        boolean result = authorizeTransactionValidator.isValid(authorizeTransaction);

        // Assertion
        assertTrue(result);
    }

    @Test
    void testIsSpecificValid_alwaysReturnsTrue() {
        // Execution
        boolean result = authorizeTransactionValidator.isSpecificValid(authorizeTransaction);

        // Assertion
        assertTrue(result);
    }

    @Test
    void testIsTransactionOrderValid_whenNoReferenceTransaction_thenReturnTrue() {
        // Setup
        when(authorizeTransaction.getReferenceTransaction()).thenReturn(null);

        // Execution
        boolean result = authorizeTransactionValidator.isTransactionOrderValid(authorizeTransaction);

        // Assertion
        assertTrue(result);
    }

    @Test
    void testIsTransactionOrderValid_whenReferenceTransactionExists_thenReturnFalse() {
        // Setup
        when(authorizeTransaction.getReferenceTransaction()).thenReturn(referenceTransaction);

        // Execution
        boolean result = authorizeTransactionValidator.isTransactionOrderValid(authorizeTransaction);

        // Assertion
        assertFalse(result);
    }
}
