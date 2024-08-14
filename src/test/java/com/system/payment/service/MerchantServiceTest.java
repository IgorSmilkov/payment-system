package com.system.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.system.payment.dto.MerchantDto;
import com.system.payment.dto.UpdateMerchantDto;
import com.system.payment.dto.mapper.MerchantMapper;
import com.system.payment.exception.PaymentServiceErrorCode;
import com.system.payment.exception.PaymentServiceException;
import com.system.payment.model.Merchant;
import com.system.payment.model.User;
import com.system.payment.repository.MerchantRepository;
import com.system.payment.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private MerchantMapper merchantMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MerchantService merchantService;

    private Merchant merchant;
    private User user;
    private MerchantDto merchantDto;
    private UpdateMerchantDto updateMerchantDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Merchant Name");
        user.setStatus(User.UserStatus.ACTIVE);

        merchant = new Merchant();
        merchant.setId(1L);
        merchant.setDescription("Test Merchant");
        merchant.setUser(user);

        merchantDto = new MerchantDto(
                merchant.getId(),
                merchant.getDescription(),
                merchant.getTotalTransactionSum(),
                user.getStatus(),
                user.getId(),
                user.getName(),
                merchant.getCreatedDate(),
                merchant.getModifiedDate()
        );

        updateMerchantDto = new UpdateMerchantDto(
                "Updated Merchant",
                User.UserStatus.INACTIVE,
                "Updated Name"
        );
    }

    @Test
    void findActiveMerchantByEmail_ShouldReturnMerchant_WhenMerchantIsActive() {
        when(merchantRepository.findByUserEmail(anyString())).thenReturn(Optional.of(merchant));

        Merchant result = merchantService.findActiveMerchantByEmail("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getUser().getStatus()).isEqualTo(User.UserStatus.ACTIVE);
        verify(merchantRepository, times(1)).findByUserEmail(anyString());
    }

    @Test
    void findActiveMerchantByEmail_ShouldThrowException_WhenMerchantDoesNotExist() {
        when(merchantRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());

        PaymentServiceException exception = assertThrows(PaymentServiceException.class,
                () -> merchantService.findActiveMerchantByEmail("test@example.com"));

        assertThat(exception.getErrorCode()).isEqualTo(PaymentServiceErrorCode.MERCHANT_DOES_NOT_EXIST);
    }

    @Test
    void findActiveMerchantByEmail_ShouldThrowException_WhenMerchantIsNotActive() {
        user.setStatus(User.UserStatus.INACTIVE);
        when(merchantRepository.findByUserEmail(anyString())).thenReturn(Optional.of(merchant));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class,
                () -> merchantService.findActiveMerchantByEmail("test@example.com"));

        assertThat(exception.getErrorCode()).isEqualTo(PaymentServiceErrorCode.MERCHANT_IS_NOT_ACTIVE);
    }

    @Test
    void saveMerchant_ShouldReturnSavedMerchant() {
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);

        Merchant result = merchantService.saveMerchant(merchant);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(merchant.getId());
        verify(merchantRepository, times(1)).save(any(Merchant.class));
    }

    @Test
    void getAllMerchants_ShouldReturnPageOfMerchants() {
        Pageable pageable = PageRequest.of(0, 10);
        when(merchantRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(merchant)));
        when(merchantMapper.toMerchantDto(any(Merchant.class))).thenReturn(merchantDto);

        Page<MerchantDto> result = merchantService.getAllMerchants(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(merchantRepository, times(1)).findAll(pageable);
    }

    @Test
    void getMerchantById_ShouldReturnMerchantDto_WhenMerchantExists() {
        when(merchantRepository.findById(anyLong())).thenReturn(Optional.of(merchant));
        when(merchantMapper.toMerchantDto(any(Merchant.class))).thenReturn(merchantDto);

        MerchantDto result = merchantService.getMerchantById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(merchant.getId());
        verify(merchantRepository, times(1)).findById(anyLong());
    }

    @Test
    void getMerchantById_ShouldThrowException_WhenMerchantDoesNotExist() {
        when(merchantRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> merchantService.getMerchantById(1L));

        assertThat(exception.getMessage()).isEqualTo("Merchant not found with id 1");
    }

    @Test
    void updateMerchant_shouldUpdateMerchantSuccessfully() {
        when(merchantRepository.findById(anyLong())).thenReturn(Optional.of(merchant));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);
        when(merchantMapper.toMerchantDto(any(Merchant.class))).thenAnswer(invocation -> {
            Merchant savedMerchant = invocation.getArgument(0);
            return new MerchantDto(
                    savedMerchant.getId(),
                    savedMerchant.getDescription(),
                    savedMerchant.getTotalTransactionSum(),
                    savedMerchant.getUser().getStatus(),
                    savedMerchant.getUser().getId(),
                    savedMerchant.getUser().getName(),
                    savedMerchant.getCreatedDate(),
                    savedMerchant.getModifiedDate()
            );
        });

        MerchantDto result = merchantService.updateMerchant(1L, updateMerchantDto);

        assertNotNull(result);
        assertEquals("Updated Merchant", result.description());
        assertEquals("Updated Name", result.name());
        verify(merchantRepository).findById(1L);
        verify(userRepository).save(user);
        verify(merchantRepository).save(merchant);
        verify(merchantMapper).toMerchantDto(merchant);
    }

    @Test
    void deleteMerchant_ShouldDeleteMerchant_WhenNoTransactionsExist() {
        when(merchantRepository.countTransactionsByMerchantId(anyLong())).thenReturn(0L);

        merchantService.deleteMerchant(1L);

        verify(merchantRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteMerchant_ShouldThrowException_WhenTransactionsExist() {
        when(merchantRepository.countTransactionsByMerchantId(anyLong())).thenReturn(5L);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class,
                () -> merchantService.deleteMerchant(1L));

        assertThat(exception.getErrorCode()).isEqualTo(PaymentServiceErrorCode.MERCHANT_HAS_TRANSACTIONS);
        verify(merchantRepository, never()).deleteById(anyLong());
    }
}
