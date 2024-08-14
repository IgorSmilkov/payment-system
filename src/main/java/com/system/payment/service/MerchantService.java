package com.system.payment.service;

import static com.system.payment.exception.PaymentServiceErrorCode.MERCHANT_DOES_NOT_EXIST;
import static com.system.payment.exception.PaymentServiceErrorCode.MERCHANT_HAS_TRANSACTIONS;
import static com.system.payment.exception.PaymentServiceErrorCode.MERCHANT_IS_NOT_ACTIVE;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.system.payment.dto.MerchantDto;
import com.system.payment.dto.UpdateMerchantDto;
import com.system.payment.dto.mapper.MerchantMapper;
import com.system.payment.exception.PaymentServiceException;
import com.system.payment.model.Merchant;
import com.system.payment.model.User;
import com.system.payment.repository.MerchantRepository;
import com.system.payment.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;
    private final UserRepository userRepository;

    public Merchant findActiveMerchantByEmail(String email) {
        Merchant merchant = merchantRepository.findByUserEmail(email).orElseThrow(() ->
                new PaymentServiceException(MERCHANT_DOES_NOT_EXIST));
        if (merchant.getUser().getStatus() != User.UserStatus.ACTIVE) {
            throw new PaymentServiceException(MERCHANT_IS_NOT_ACTIVE);
        }
        return merchant;
    }

    public Merchant saveMerchant(Merchant merchant) {
        return merchantRepository.save(merchant);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<MerchantDto> getAllMerchants(Pageable pageable) {
        return merchantRepository.findAll(pageable)
                .map(merchantMapper::toMerchantDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public MerchantDto getMerchantById(Long id) {
        Merchant merchant = getMerchant(id);
        return merchantMapper.toMerchantDto(merchant);
    }


    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public MerchantDto updateMerchant(Long id, UpdateMerchantDto updateMerchantDto) {
        Merchant merchant = getMerchant(id);

        merchant.setDescription(updateMerchantDto.description());

        User user = merchant.getUser();
        user.setStatus(updateMerchantDto.status());
        user.setName(updateMerchantDto.name());
        userRepository.save(user);

        return merchantMapper.toMerchantDto(saveMerchant(merchant));
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteMerchant(Long id) {
        Long transactionCount = merchantRepository.countTransactionsByMerchantId(id);
        if (transactionCount > 0) {
            throw new PaymentServiceException(MERCHANT_HAS_TRANSACTIONS);
        }
        merchantRepository.deleteById(id);
    }

    private Merchant getMerchant(Long id) {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Merchant not found with id " + id));
    }
}
