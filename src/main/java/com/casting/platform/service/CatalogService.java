package com.casting.platform.service;

import com.casting.platform.dto.common.PageResponse;
import com.casting.platform.dto.request.catalog.CatalogFilterRequest;
import com.casting.platform.dto.response.profile.ProfileResponse;
import com.casting.platform.entity.PerformerProfile;
import com.casting.platform.entity.PerformerType;
import com.casting.platform.repository.PerformerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // ✅ фикс LazyInitializationException
public class CatalogService {

    private final PerformerProfileRepository profileRepository;

    @Value("${app.publicApiUrl:http://localhost:8080}")
    private String publicUrl;

    /* ================= ACTORS ================= */

    public PageResponse<ProfileResponse> getActors(
            CatalogFilterRequest filters,
            Pageable pageable
    ) {

        Page<PerformerProfile> page =
                profileRepository.findActorsWithFilters(
                        PerformerType.ACTOR.name(),
                        emptyToNull(filters.getCity()),
                        filters.getMinAge(),
                        filters.getMaxAge(),
                        emptyToNull(filters.getGender()),
                        emptyToNull(filters.getEthnicity()),
                        filters.getMinRate(),
                        filters.getMaxRate(),
                        emptyToNull(filters.getRateUnit()),
                        pageable
                );

        return buildPage(page);
    }

    /* ================= CREATORS ================= */

    public PageResponse<ProfileResponse> getCreators(
            CatalogFilterRequest filters,
            Pageable pageable
    ) {

        Page<PerformerProfile> page =
                profileRepository.findCreatorsWithFilters(
                        PerformerType.CREATOR.name(),
                        emptyToNull(filters.getCity()),
                        emptyToNull(filters.getActivityType()),
                        pageable
                );

        return buildPage(page);
    }

    /* ================= LOCATIONS ================= */

    public PageResponse<ProfileResponse> getLocations(
            CatalogFilterRequest filters,
            Pageable pageable
    ) {

        Page<PerformerProfile> page =
                profileRepository.findLocationsWithFilters(
                        PerformerType.LOCATION.name(),
                        emptyToNull(filters.getCity()),
                        filters.getMinRentPrice(),
                        filters.getMaxRentPrice(),
                        pageable
                );

        return buildPage(page);
    }

    /* ================= PAGE BUILDER ================= */

    private PageResponse<ProfileResponse> buildPage(Page<PerformerProfile> page) {

        PageResponse<ProfileResponse> response = new PageResponse<>();

        response.setContent(
                page.getContent()
                        .stream()
                        .map(this::mapToPublicProfile)
                        .toList()
        );

        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLast(page.isLast());

        return response;
    }

    /* ================= MAPPING ================= */

    private ProfileResponse mapToPublicProfile(PerformerProfile profile) {

        ProfileResponse dto = new ProfileResponse();

        dto.setId(profile.getId());
        dto.setType(profile.getType());
        dto.setDisplayName(profile.getDisplayName());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setCity(profile.getCity());
        dto.setDescription(profile.getDescription());
        dto.setBio(profile.getBio());

        String mainPhoto = profile.getMainPhotoUrl();

        if (mainPhoto == null &&
                profile.getPhotoUrls() != null &&
                !profile.getPhotoUrls().isEmpty()) {

            mainPhoto = profile.getPhotoUrls().iterator().next();
        }

        dto.setMainPhotoUrl(normalizeUrl(mainPhoto));

        dto.setPhotoUrls(
                profile.getPhotoUrls() == null
                        ? List.of()
                        : profile.getPhotoUrls()
                        .stream()
                        .map(this::normalizeUrl)
                        .toList()
        );

        dto.setVideoUrls(
                profile.getVideoUrls() == null
                        ? List.of()
                        : profile.getVideoUrls()
                        .stream()
                        .map(this::normalizeUrl)
                        .toList()
        );

        dto.setGender(profile.getGender());
        dto.setAge(profile.getAge());
        dto.setEthnicity(profile.getEthnicity());
        dto.setHeightCm(profile.getHeightCm());
        dto.setWeightKg(profile.getWeightKg());
        dto.setBodyType(profile.getBodyType());
        dto.setHairColor(profile.getHairColor());
        dto.setEyeColor(profile.getEyeColor());
        dto.setGameAgeFrom(profile.getGameAgeFrom());
        dto.setGameAgeTo(profile.getGameAgeTo());
        dto.setSkillsJson(profile.getSkillsJson());
        dto.setActivityType(profile.getActivityType());
        dto.setExperienceLevel(profile.getExperienceLevel());
        dto.setProjectFormatsJson(profile.getProjectFormatsJson());
        dto.setLocationName(profile.getLocationName());
        dto.setAddress(profile.getAddress());
        dto.setRentPrice(profile.getRentPrice());
        dto.setRentPriceUnit(profile.getRentPriceUnit());
        dto.setLocationType(profile.getLocationType());
        dto.setMinRate(profile.getMinRate());
        dto.setRateUnit(profile.getRateUnit());
        dto.setPremiumActive(isPremiumActive(profile));
        dto.setPremiumUntil(profile.getPremiumUntil());

        return dto;
    }

    /* ================= HELPERS ================= */

    private String normalizeUrl(String url) {

        if (url == null || url.isBlank()) return null;

        if (url.startsWith("http")) return url;

        return publicUrl + url;
    }

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    private boolean isPremiumActive(PerformerProfile profile) {
        return profile.getPremiumUntil() != null && profile.getPremiumUntil().isAfter(java.time.LocalDateTime.now());
    }
}
