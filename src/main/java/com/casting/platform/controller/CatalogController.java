package com.casting.platform.controller;

import com.casting.platform.dto.common.PageResponse;
import com.casting.platform.dto.request.catalog.CatalogFilterRequest;
import com.casting.platform.dto.response.profile.ProfileResponse;
import com.casting.platform.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/actors")
    public ResponseEntity<PageResponse<ProfileResponse>> getActors(
            CatalogFilterRequest filters,
            Pageable pageable) {
        var result = catalogService.getActors(filters, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/creators")
    public ResponseEntity<PageResponse<ProfileResponse>> getCreators(
            CatalogFilterRequest filters,
            Pageable pageable) {
        var result = catalogService.getCreators(filters, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/locations")
    public ResponseEntity<PageResponse<ProfileResponse>> getLocations(
            CatalogFilterRequest filters,
            Pageable pageable) {
        var result = catalogService.getLocations(filters, pageable);
        return ResponseEntity.ok(result);
    }
}
