package com.casting.platform.service;

import com.casting.platform.dto.response.admin.AdminStatsResponse;
import com.casting.platform.repository.CastingPostRepository;
import com.casting.platform.repository.ContactViewRepository;
import com.casting.platform.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStatsService {

    private final ContactViewRepository contactViewRepository;
    private final PaymentRepository paymentRepository;
    private final CastingPostRepository castingPostRepository;

    public AdminStatsResponse getStats() {
        long views = contactViewRepository.count();
        long payments = paymentRepository.count();
        long castings = castingPostRepository.count();
        return new AdminStatsResponse(views, payments, castings);
    }
}
