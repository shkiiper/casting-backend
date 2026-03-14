package com.casting.platform.controller;

import com.casting.platform.dto.common.PageResponse;
import com.casting.platform.dto.request.casting.CastingFilterRequest;
import com.casting.platform.dto.request.casting.CreateCastingRequest;
import com.casting.platform.dto.response.casting.CastingResponse;
import com.casting.platform.service.CastingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/castings")
@RequiredArgsConstructor
public class CastingController {

    private final CastingService castingService;

    // Создать кастинг (текущий пользователь как customer)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CastingResponse createCasting(@RequestBody CreateCastingRequest request) {
        return castingService.createCasting(request);
    }

    // Активные кастинги (общий список, с фильтрами)
    @GetMapping("/active")
    public PageResponse<CastingResponse> getActiveCastings(CastingFilterRequest filters,
                                                           Pageable pageable) {
        return castingService.getActiveCastings(filters, pageable);
    }

    // Мои кастинги (для текущего кастомера)
    @GetMapping("/my")
    public PageResponse<CastingResponse> getMyCastings(Pageable pageable) {
        return castingService.getMyCastings(pageable);
    }

    // Один кастинг по id
    @GetMapping("/{id}")
    public CastingResponse getCasting(@PathVariable Long id) {
        return castingService.getCasting(id);
    }

    // Закрыть кастинг (только владелец)
    @PostMapping("/{id}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeCasting(@PathVariable Long id) {
        castingService.closeCasting(id);
    }
}
