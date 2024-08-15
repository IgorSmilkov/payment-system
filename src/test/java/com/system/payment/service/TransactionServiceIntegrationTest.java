package com.system.payment.service;

import static com.system.payment.exception.PaymentServiceErrorCode.REFERENCE_TRANSACTION_NOT_FOUND;
import static com.system.payment.model.TransactionStatus.APPROVED;
import static com.system.payment.model.TransactionStatus.REVERSED;
import static com.system.payment.model.TransactionType.AUTHORIZE;
import static com.system.payment.model.TransactionType.CHARGE;
import static com.system.payment.model.TransactionType.REFUND;
import static com.system.payment.model.TransactionType.REVERSAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import com.system.payment.dto.TransactionRequestDto;
import com.system.payment.dto.TransactionResponseDto;
import com.system.payment.exception.PaymentServiceException;
import com.system.payment.model.Merchant;
import com.system.payment.model.User;
import com.system.payment.model.transaction.AuthorizeTransaction;
import com.system.payment.model.transaction.ChargeTransaction;
import com.system.payment.model.transaction.RefundTransaction;
import com.system.payment.model.transaction.ReversalTransaction;
import com.system.payment.model.transaction.Transaction;
import com.system.payment.repository.MerchantRepository;
import com.system.payment.repository.TransactionRepository;
import com.system.payment.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
@ActiveProfiles("test")
public class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private Merchant merchant;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setName("Test Merchant");
        user.setEmail("merchant@example.com");
        user.setPassword("password");
        user.setStatus(User.UserStatus.ACTIVE);

        user = userRepository.save(user);

        merchant = new Merchant();
        merchant.setUser(user);
        merchant.setDescription("Test Merchant Description");

        merchantRepository.save(merchant);
    }


    @Test
    @WithMockUser(username = "merchant@example.com", roles = "MERCHANT")
    public void testCreateAuthorizeTransaction() {
        // Given
        TransactionRequestDto requestDto = new TransactionRequestDto(
                "customer@example.com",
                "1234567890",
                BigDecimal.valueOf(100.00),
                null,
                AUTHORIZE
        );

        // When
        TransactionResponseDto responseDto = transactionService.processTransaction(requestDto);

        // Then
        assertNotNull(responseDto.id());
        assertEquals(AUTHORIZE, responseDto.type());
        assertEquals(APPROVED, responseDto.status());

        Transaction savedTransaction = transactionRepository.findById(responseDto.id()).orElse(null);
        assertNotNull(savedTransaction);
        assertInstanceOf(AuthorizeTransaction.class, savedTransaction);
        assertEquals(APPROVED, savedTransaction.getStatus());
        assertEquals(BigDecimal.valueOf(100.00), ((AuthorizeTransaction) savedTransaction).getAmount());
        assertEquals(merchant.getId(), savedTransaction.getMerchant().getId());
    }

    @Test
    @WithMockUser(username = "merchant@example.com", roles = "MERCHANT")
    public void testCreateChargeTransaction() {
        // Given
        AuthorizeTransaction authorizeTransaction = new AuthorizeTransaction();
        authorizeTransaction.setAmount(BigDecimal.valueOf(100.00));
        authorizeTransaction.setCustomerEmail("customer@example.com");
        authorizeTransaction.setCustomerPhone("1234567890");
        authorizeTransaction.setMerchant(merchant);
        authorizeTransaction.setStatus(APPROVED);
        transactionRepository.save(authorizeTransaction);

        TransactionRequestDto requestDto = new TransactionRequestDto(
                "customer@example.com",
                "1234567890",
                BigDecimal.valueOf(100.00),
                authorizeTransaction.getId(),
                CHARGE
        );

        // When
        TransactionResponseDto responseDto = transactionService.processTransaction(requestDto);

        // Then
        assertNotNull(responseDto.id());
        assertEquals(CHARGE, responseDto.type());
        assertEquals(APPROVED, responseDto.status());

        Transaction savedTransaction = transactionRepository.findById(responseDto.id()).orElse(null);
        assertNotNull(savedTransaction);
        assertInstanceOf(ChargeTransaction.class, savedTransaction);
        assertEquals(APPROVED, savedTransaction.getStatus());
        assertEquals(BigDecimal.valueOf(100.00), ((ChargeTransaction) savedTransaction).getAmount());
        assertEquals(authorizeTransaction.getId(), savedTransaction.getReferenceTransaction().getId());
    }

    @Test
    @WithMockUser(username = "merchant@example.com", roles = "MERCHANT")
    public void testCreateRefundTransaction() {
        // Given
        ChargeTransaction chargeTransaction = new ChargeTransaction();
        chargeTransaction.setAmount(BigDecimal.valueOf(100.00));
        chargeTransaction.setCustomerEmail("customer@example.com");
        chargeTransaction.setCustomerPhone("1234567890");
        chargeTransaction.setMerchant(merchant);
        chargeTransaction.setStatus(APPROVED);
        transactionRepository.save(chargeTransaction);

        TransactionRequestDto requestDto = new TransactionRequestDto(
                "customer@example.com",
                "1234567890",
                BigDecimal.valueOf(100.00),
                chargeTransaction.getId(),
                REFUND
        );

        // When
        TransactionResponseDto responseDto = transactionService.processTransaction(requestDto);

        // Then
        assertNotNull(responseDto.id());
        assertEquals(REFUND, responseDto.type());
        assertEquals(APPROVED, responseDto.status());

        Transaction savedTransaction = transactionRepository.findById(responseDto.id()).orElse(null);
        assertNotNull(savedTransaction);
        assertInstanceOf(RefundTransaction.class, savedTransaction);
        assertEquals(APPROVED, savedTransaction.getStatus());
        assertEquals(BigDecimal.valueOf(100.00), ((RefundTransaction) savedTransaction).getAmount());
        assertEquals(chargeTransaction.getId(), savedTransaction.getReferenceTransaction().getId());
    }

    @Test
    @WithMockUser(username = "merchant@example.com", roles = "MERCHANT")
    public void testCreateReversalTransaction() {
        // Given
        AuthorizeTransaction authorizeTransaction = new AuthorizeTransaction();
        authorizeTransaction.setAmount(BigDecimal.valueOf(100.00));
        authorizeTransaction.setCustomerEmail("customer@example.com");
        authorizeTransaction.setCustomerPhone("1234567890");
        authorizeTransaction.setMerchant(merchant);
        authorizeTransaction.setStatus(APPROVED);
        transactionRepository.save(authorizeTransaction);

        TransactionRequestDto requestDto = new TransactionRequestDto(
                "customer@example.com",
                "1234567890",
                BigDecimal.valueOf(100.00),
                authorizeTransaction.getId(),
                REVERSAL
        );

        // When
        TransactionResponseDto responseDto = transactionService.processTransaction(requestDto);

        // Then
        assertNotNull(responseDto.id());
        assertEquals(REVERSAL, responseDto.type());
        assertEquals(APPROVED, responseDto.status());

        Transaction savedTransaction = transactionRepository.findById(responseDto.id()).orElse(null);
        assertNotNull(savedTransaction);
        assertInstanceOf(ReversalTransaction.class, savedTransaction);
        assertEquals(APPROVED, savedTransaction.getStatus());
        assertEquals(authorizeTransaction.getId(), savedTransaction.getReferenceTransaction().getId());

        AuthorizeTransaction updatedAuthorizeTransaction = (AuthorizeTransaction) transactionRepository.findById(authorizeTransaction.getId()).orElse(null);
        assertNotNull(updatedAuthorizeTransaction);
        assertEquals(REVERSED, updatedAuthorizeTransaction.getStatus());
    }

    @Test
    @WithMockUser(username = "merchant@example.com", roles = "MERCHANT")
    public void testCreateTransaction_InvalidReference() {
        // Given
        UUID invalidReferenceId = UUID.randomUUID();
        TransactionRequestDto requestDto = new TransactionRequestDto(
                "customer@example.com",
                "1234567890",
                BigDecimal.valueOf(100.00),
                invalidReferenceId,
                CHARGE
        );

        // When & Then
        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                transactionService.processTransaction(requestDto));
        assertEquals(REFERENCE_TRANSACTION_NOT_FOUND.name(), exception.getErrorCode().getKey());
    }

    @Test
    @WithMockUser(username = "merchant@example.com", roles = "MERCHANT")
    public void testCreateTransaction_InvalidAmount() {
        // Given
        TransactionRequestDto requestDto = new TransactionRequestDto(
                "customer@example.com",
                "1234567890",
                BigDecimal.valueOf(-100.00), // Invalid amount
                null,
                AUTHORIZE
        );

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            transactionService.processTransaction(requestDto);
            entityManager.flush();

        });
    }


}