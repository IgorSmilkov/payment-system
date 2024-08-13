package com.system.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.system.payment.dto.TransactionRequestDto;
import com.system.payment.dto.TransactionResponseDto;
import com.system.payment.dto.mapper.TransactionMapper;
import com.system.payment.factory.TransactionFactory;
import com.system.payment.model.Merchant;
import com.system.payment.model.TransactionStatus;
import com.system.payment.model.TransactionType;
import com.system.payment.model.transaction.AuthorizeTransaction;
import com.system.payment.model.transaction.ChargeTransaction;
import com.system.payment.model.transaction.RefundTransaction;
import com.system.payment.model.transaction.ReversalTransaction;
import com.system.payment.model.transaction.Transaction;
import com.system.payment.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private final UUID transactionId = UUID.randomUUID();
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private MerchantService merchantService;
    @Mock
    private TransactionFactory transactionFactory;
    @Mock
    private TransactionValidationService transactionValidationService;
    @Mock
    private TransactionMapper transactionMapper;
    @InjectMocks
    private TransactionService transactionService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    private Merchant merchant;
    private TransactionRequestDto transactionRequestDto;
    private AuthorizeTransaction authorizeTransaction;
    private ChargeTransaction chargeTransaction;
    private RefundTransaction refundTransaction;
    private ReversalTransaction reversalTransaction;

    @BeforeEach
    void setUp() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("merchant@example.com");

        merchant = new Merchant();
        merchant.setId(1L);
        merchant.setTotalTransactionSum(BigDecimal.ZERO);

        authorizeTransaction = new AuthorizeTransaction();
        authorizeTransaction.setId(transactionId);
        authorizeTransaction.setAmount(BigDecimal.valueOf(100));
        authorizeTransaction.setMerchant(merchant);
        authorizeTransaction.setCustomerEmail("customer@example.com");
        authorizeTransaction.setCustomerPhone("123456789");
        authorizeTransaction.setStatus(TransactionStatus.APPROVED);

        chargeTransaction = new ChargeTransaction();
        chargeTransaction.setId(transactionId);
        chargeTransaction.setAmount(BigDecimal.valueOf(100));
        chargeTransaction.setMerchant(merchant);
        chargeTransaction.setCustomerEmail("customer@example.com");
        chargeTransaction.setCustomerPhone("123456789");
        chargeTransaction.setStatus(TransactionStatus.APPROVED);

        refundTransaction = new RefundTransaction();
        refundTransaction.setAmount(BigDecimal.valueOf(50));
        refundTransaction.setReferenceTransaction(chargeTransaction);
        refundTransaction.setMerchant(merchant);
        refundTransaction.setCustomerEmail("customer@example.com");
        refundTransaction.setCustomerPhone("123456789");
        refundTransaction.setStatus(TransactionStatus.APPROVED);

        reversalTransaction = new ReversalTransaction();
        reversalTransaction.setReferenceTransaction(authorizeTransaction);
        reversalTransaction.setMerchant(merchant);
        reversalTransaction.setCustomerEmail("customer@example.com");
        reversalTransaction.setCustomerPhone("123456789");
        reversalTransaction.setStatus(TransactionStatus.APPROVED);

        transactionRequestDto = new TransactionRequestDto(
                "customer@example.com",
                "123456789",
                BigDecimal.valueOf(100),
                null,
                TransactionType.AUTHORIZE
        );
    }

    @Test
    void testProcessTransactionSuccess() {
        when(merchantService.findActiveMerchantByEmail(anyString())).thenReturn(merchant);
        when(transactionFactory.createTransaction(any(TransactionRequestDto.class), any(Merchant.class)))
                .thenReturn(chargeTransaction);
        when(transactionValidationService.isValidTransaction(any(Transaction.class))).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(chargeTransaction);
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(
                new TransactionResponseDto(
                        transactionId,
                        chargeTransaction.getCustomerEmail(),
                        chargeTransaction.getCustomerPhone(),
                        chargeTransaction.getAmount(),
                        null,
                        TransactionType.CHARGE,
                        chargeTransaction.getStatus(),
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );

        TransactionResponseDto response = transactionService.processTransaction(transactionRequestDto);

        assertNotNull(response);
        assertEquals(TransactionStatus.APPROVED, response.status());
        assertEquals(BigDecimal.valueOf(100), merchant.getTotalTransactionSum());
        verify(transactionRepository).save(any(Transaction.class));
        verify(merchantService).saveMerchant(any(Merchant.class));
    }

    @Test
    void testProcessTransactionWithValidationFailure() {
        when(merchantService.findActiveMerchantByEmail(anyString())).thenReturn(merchant);
        when(transactionFactory.createTransaction(any(TransactionRequestDto.class), any(Merchant.class)))
                .thenReturn(chargeTransaction);
        when(transactionValidationService.isValidTransaction(any(Transaction.class))).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(chargeTransaction);
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(
                new TransactionResponseDto(
                        transactionId,
                        chargeTransaction.getCustomerEmail(),
                        chargeTransaction.getCustomerPhone(),
                        chargeTransaction.getAmount(),
                        null,
                        TransactionType.CHARGE,
                        chargeTransaction.getStatus(),
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );

        TransactionResponseDto response = transactionService.processTransaction(transactionRequestDto);

        assertNotNull(response);
        assertEquals(TransactionStatus.ERROR, chargeTransaction.getStatus());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(merchantService, never()).saveMerchant(any(Merchant.class));
    }

    @Test
    void testProcessTransactionOptimisticLockingFailure() {
        when(merchantService.findActiveMerchantByEmail(anyString())).thenReturn(merchant);
        when(transactionFactory.createTransaction(any(TransactionRequestDto.class), any(Merchant.class)))
                .thenReturn(chargeTransaction);
        when(transactionValidationService.isValidTransaction(any(Transaction.class))).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class)))
                .thenThrow(new OptimisticLockingFailureException("Optimistic locking failed"));

        assertThrows(OptimisticLockingFailureException.class, () -> {
            transactionService.processTransaction(transactionRequestDto);
        });

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testProcessRefundTransaction() {
        when(merchantService.findActiveMerchantByEmail(anyString())).thenReturn(merchant);
        when(transactionFactory.createTransaction(any(TransactionRequestDto.class), any(Merchant.class)))
                .thenReturn(refundTransaction);
        when(transactionValidationService.isValidTransaction(any(Transaction.class))).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(refundTransaction);
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(
                new TransactionResponseDto(
                        transactionId,
                        refundTransaction.getCustomerEmail(),
                        refundTransaction.getCustomerPhone(),
                        refundTransaction.getAmount(),
                        chargeTransaction.getId(),
                        TransactionType.REFUND,
                        TransactionStatus.APPROVED,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );

        refundTransaction.setStatus(TransactionStatus.APPROVED);
        merchant.setTotalTransactionSum(BigDecimal.valueOf(100));

        TransactionResponseDto response = transactionService.processTransaction(transactionRequestDto);

        assertNotNull(response);
        assertEquals(TransactionStatus.APPROVED, response.status());
        assertEquals(TransactionStatus.REFUNDED, chargeTransaction.getStatus());
        assertEquals(BigDecimal.valueOf(50), merchant.getTotalTransactionSum());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void testProcessReversalTransaction() {
        when(merchantService.findActiveMerchantByEmail(anyString())).thenReturn(merchant);
        when(transactionFactory.createTransaction(any(TransactionRequestDto.class), any(Merchant.class)))
                .thenReturn(reversalTransaction);
        when(transactionValidationService.isValidTransaction(any(Transaction.class))).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(reversalTransaction);
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(
                new TransactionResponseDto(
                        transactionId,
                        reversalTransaction.getCustomerEmail(),
                        reversalTransaction.getCustomerPhone(),
                        null,
                        authorizeTransaction.getId(),
                        TransactionType.REVERSAL,
                        TransactionStatus.APPROVED,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );

        TransactionResponseDto response = transactionService.processTransaction(transactionRequestDto);

        assertNotNull(response);
        assertEquals(TransactionStatus.APPROVED, response.status());
        assertEquals(TransactionStatus.REVERSED, authorizeTransaction.getStatus());
        verify(transactionRepository, times(2)).save(any(Transaction.class));  // Reversal and referenced transaction
    }

}