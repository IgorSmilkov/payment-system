package com.system.payment.service;

import static com.system.payment.exception.PaymentServiceErrorCode.MERCHANT_DOES_NOT_EXIST;
import static com.system.payment.exception.PaymentServiceErrorCode.MERCHANT_IS_NOT_ACTIVE;
import org.springframework.stereotype.Service;
import com.system.payment.exception.PaymentServiceException;
import com.system.payment.model.Merchant;
import com.system.payment.model.User;
import com.system.payment.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantRepository merchantRepository;

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
}
