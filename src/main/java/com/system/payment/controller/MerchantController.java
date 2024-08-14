package com.system.payment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.system.payment.dto.MerchantDto;
import com.system.payment.dto.UpdateMerchantDto;
import com.system.payment.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping
    public Page<MerchantDto> getAllMerchants(
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return merchantService.getAllMerchants(pageable);
    }

    @GetMapping("/{id}")
    public MerchantDto getMerchantById(@PathVariable Long id) {
        return merchantService.getMerchantById(id);
    }

    @PutMapping("/{id}")
    public MerchantDto updateMerchant(@PathVariable Long id, @Valid @RequestBody UpdateMerchantDto updateMerchantDTO) {
        return merchantService.updateMerchant(id, updateMerchantDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteMerchant(@PathVariable Long id) {
        merchantService.deleteMerchant(id);
    }
}
