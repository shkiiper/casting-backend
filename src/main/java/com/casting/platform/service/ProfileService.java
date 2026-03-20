package com.casting.platform.service;

import com.casting.platform.dto.request.customer.UpdateCustomerProfileRequest;
import com.casting.platform.dto.request.profile.*;
import com.casting.platform.dto.response.customer.CustomerProfileResponse;
import com.casting.platform.dto.response.profile.ProfileResponse;
import com.casting.platform.entity.*;
import com.casting.platform.exception.BadRequestException;
import com.casting.platform.exception.ForbiddenException;
import com.casting.platform.exception.NotFoundException;
import com.casting.platform.repository.CustomerSubscriptionPlanRepository;
import com.casting.platform.repository.PerformerProfileRepository;
import com.casting.platform.repository.UserRepository;
import com.casting.platform.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final PerformerProfileRepository profileRepository;
    private final CustomerSubscriptionPlanRepository planRepository;

    @Value("${app.publicApiUrl:http://localhost:8080}")
    private String publicUrl;

    /* ================= CREATE ================= */

    public ProfileResponse createProfileForCurrentUser(PerformerType type, Object request) {

        User owner = getCurrentUser();

        if (owner.getPerformerProfile() != null) {
            throw new BadRequestException("Profile already exists");
        }

        if (!isAllowed(owner.getRole(), type)) {
            throw new ForbiddenException("User role cannot create this profile type");
        }

        PerformerProfile profile = new PerformerProfile();
        profile.setOwner(owner);
        profile.setType(type);
        profile.setPublished(false);

        switch (type) {
            case ACTOR -> fillActor(profile, (CreateActorProfileRequest) request);
            case CREATOR -> fillCreator(profile, (CreateCreatorProfileRequest) request);
            case LOCATION -> fillLocation(profile, (CreateLocationProfileRequest) request);
            default -> throw new BadRequestException("Unsupported performer type");
        }

        applyDefaultContactsFromUser(profile, owner);

        profileRepository.save(profile);

        owner.setPerformerProfile(profile);
        userRepository.save(owner);

        return map(profile, true);
    }

    /* ================= GET ================= */

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {
        return map(getMyProfileOrThrow(), true);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileById(Long id) {

        PerformerProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        if (!profile.isPublished()) {
            throw new NotFoundException("Profile not found");
        }

        return map(profile, false);
    }

    /* ================= CUSTOMER ================= */

    @Transactional(readOnly = true)
    public CustomerProfileResponse getMyCustomerProfile() {
        return mapCustomer(getCurrentUser());
    }

    public CustomerProfileResponse updateCustomerProfile(UpdateCustomerProfileRequest request) {

        User user = getCurrentUser();

        if (request.getContactEmail() != null) user.setEmail(request.getContactEmail());
        if (request.getContactPhone() != null) user.setPhone(request.getContactPhone());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getDescription() != null) user.setDescription(request.getDescription());
        if (request.getContactTelegram() != null) user.setTelegram(request.getContactTelegram());
        if (request.getMainPhotoUrl() != null) user.setAvatarUrl(request.getMainPhotoUrl());

        userRepository.save(user);

        return mapCustomer(user);
    }

    /* ================= UPDATE PERFORMER ================= */

    public ProfileResponse updateActorProfile(UpdateActorProfileRequest request) {

        PerformerProfile profile = getMyProfileOrThrow();

        if (profile.getType() != PerformerType.ACTOR) {
            throw new BadRequestException("Unsupported performer type");
        }

        fillActor(profile, request);
        profileRepository.save(profile);

        return map(profile, true);
    }

    public ProfileResponse updateActorVisibility(boolean published) {
        PerformerProfile profile = getMyProfileOrThrow();

        if (profile.getType() != PerformerType.ACTOR) {
            throw new BadRequestException("Unsupported performer type");
        }

        handlePublishToggle(profile, published);
        profileRepository.save(profile);

        return map(profile, true);
    }

    public ProfileResponse updateCreatorProfile(UpdateCreatorProfileRequest request) {

        PerformerProfile profile = getMyProfileOrThrow();

        if (profile.getType() != PerformerType.CREATOR) {
            throw new BadRequestException("Unsupported performer type");
        }

        fillCreator(profile, request);
        profileRepository.save(profile);

        return map(profile, true);
    }

    public ProfileResponse updateCreatorVisibility(boolean published) {
        PerformerProfile profile = getMyProfileOrThrow();

        if (profile.getType() != PerformerType.CREATOR) {
            throw new BadRequestException("Unsupported performer type");
        }

        handlePublishToggle(profile, published);
        profileRepository.save(profile);

        return map(profile, true);
    }

    public ProfileResponse updateLocationProfile(UpdateLocationProfileRequest request) {

        PerformerProfile profile = getMyProfileOrThrow();

        if (profile.getType() != PerformerType.LOCATION) {
            throw new BadRequestException("Unsupported performer type");
        }

        fillLocation(profile, request);
        profileRepository.save(profile);

        return map(profile, true);
    }

    public ProfileResponse updateLocationVisibility(boolean published) {
        PerformerProfile profile = getMyProfileOrThrow();

        if (profile.getType() != PerformerType.LOCATION) {
            throw new BadRequestException("Unsupported performer type");
        }

        handlePublishToggle(profile, published);
        profileRepository.save(profile);

        return map(profile, true);
    }

    /* ================= INTERNAL ================= */

    private PerformerProfile getMyProfileOrThrow() {

        User me = getCurrentUser();

        return profileRepository
                .findByOwnerId(me.getId())
                .orElseThrow(() -> new NotFoundException("Profile not found"));
    }

    /* ================= FILL CREATE ================= */

    private void fillActor(PerformerProfile p, CreateActorProfileRequest r) {
        p.setFirstName(r.getFirstName());
        p.setLastName(r.getLastName());
        p.setCity(r.getCity());
        p.setMainPhotoUrl(r.getMainPhotoUrl());
        p.setDescription(r.getDescription());
        p.setBio(r.getBio());
        p.setExperienceText(r.getExperienceText());
        p.setGender(parseEnum(r.getGender(), Gender.class));
        p.setAge(r.getAge());
        p.setEthnicity(r.getEthnicity());
        p.setHeightCm(r.getHeightCm());
        p.setWeightKg(r.getWeightKg());
        p.setBodyType(r.getBodyType());
        p.setHairColor(r.getHairColor());
        p.setEyeColor(r.getEyeColor());
        p.setGameAgeFrom(r.getGameAgeFrom());
        p.setGameAgeTo(r.getGameAgeTo());
        p.setSkillsJson(r.getSkillsJson());
        p.setMinRate(r.getMinRate());
        p.setRateUnit(r.getRateUnit());
        p.setIntroVideoUrl(r.getIntroVideoUrl());
        p.setMonologueVideoUrl(r.getMonologueVideoUrl());
        p.setSelfTapeVideoUrl(r.getSelfTapeVideoUrl());
        setContacts(p, r);
        p.setPhotoUrls(toSet(r.getPhotoUrls()));
        p.setVideoUrls(toSet(r.getVideoUrls()));
        syncMainPhotoWithGallery(p);
    }

    private void fillCreator(PerformerProfile p, CreateCreatorProfileRequest r) {
        p.setFirstName(r.getFirstName());
        p.setLastName(r.getLastName());
        p.setDisplayName(r.getDisplayName());
        p.setCity(r.getCity());
        p.setMainPhotoUrl(r.getMainPhotoUrl());
        p.setDescription(r.getDescription());
        p.setBio(r.getBio());
        p.setExperienceText(r.getExperienceText());
        p.setActivityType(r.getActivityType());
        p.setExperienceLevel(r.getExperienceLevel());
        p.setProjectFormatsJson(r.getProjectFormatsJson());
        p.setAchievements(r.getAchievements());
        p.setSkillsJson(r.getSkillsJson());
        p.setMinRate(r.getMinRate());
        p.setRateUnit(r.getRateUnit());
        p.setSocialLinksJson(r.getSocialLinksJson());
        setContacts(p, r);
        p.setPhotoUrls(toSet(r.getPhotoUrls()));
        p.setVideoUrls(toSet(r.getVideoUrls()));
        syncMainPhotoWithGallery(p);
    }

    private void fillLocation(PerformerProfile p, CreateLocationProfileRequest r) {
        p.setLocationName(r.getLocationName());
        p.setAddress(r.getAddress());
        p.setCity(r.getCity());
        p.setMainPhotoUrl(r.getMainPhotoUrl());
        p.setDescription(r.getDescription());
        p.setRentPrice(r.getRentPrice());
        p.setRentPriceUnit(r.getRentPriceUnit());
        p.setFloor(r.getFloor());
        p.setLocationType(r.getLocationType());
        p.setAvailabilityFrom(r.getAvailabilityFrom());
        p.setAvailabilityTo(r.getAvailabilityTo());
        p.setRentalTerms(r.getRentalTerms());
        p.setExtraConditions(r.getExtraConditions());
        setContacts(p, r);
        p.setPhotoUrls(toSet(r.getPhotoUrls()));
        p.setVideoUrls(toSet(r.getVideoUrls()));
        syncMainPhotoWithGallery(p);
    }

    /* ================= FILL UPDATE ================= */

    private void fillActor(PerformerProfile p, UpdateActorProfileRequest r) {

        if (r.getFirstName() != null) p.setFirstName(r.getFirstName());
        if (r.getLastName() != null) p.setLastName(r.getLastName());
        if (r.getCity() != null) p.setCity(r.getCity());
        if (r.getMainPhotoUrl() != null) p.setMainPhotoUrl(r.getMainPhotoUrl());
        if (r.getDescription() != null) p.setDescription(r.getDescription());
        if (r.getBio() != null) p.setBio(r.getBio());
        if (r.getExperienceText() != null) p.setExperienceText(r.getExperienceText());
        if (r.getGender() != null) p.setGender(parseEnum(r.getGender(), Gender.class));
        if (r.getAge() != null) p.setAge(r.getAge());
        if (r.getEthnicity() != null) p.setEthnicity(r.getEthnicity());
        if (r.getHeightCm() != null) p.setHeightCm(r.getHeightCm());
        if (r.getWeightKg() != null) p.setWeightKg(r.getWeightKg());
        if (r.getBodyType() != null) p.setBodyType(r.getBodyType());
        if (r.getHairColor() != null) p.setHairColor(r.getHairColor());
        if (r.getEyeColor() != null) p.setEyeColor(r.getEyeColor());
        if (r.getGameAgeFrom() != null) p.setGameAgeFrom(r.getGameAgeFrom());
        if (r.getGameAgeTo() != null) p.setGameAgeTo(r.getGameAgeTo());
        if (r.getSkillsJson() != null) p.setSkillsJson(r.getSkillsJson());
        if (r.getMinRate() != null) p.setMinRate(r.getMinRate());
        if (r.getRateUnit() != null) p.setRateUnit(r.getRateUnit());
        if (r.getIntroVideoUrl() != null) p.setIntroVideoUrl(r.getIntroVideoUrl());
        if (r.getMonologueVideoUrl() != null) p.setMonologueVideoUrl(r.getMonologueVideoUrl());
        if (r.getSelfTapeVideoUrl() != null) p.setSelfTapeVideoUrl(r.getSelfTapeVideoUrl());

        setContacts(p, r);

        if (r.getPhotoUrls() != null) p.setPhotoUrls(toSet(r.getPhotoUrls()));
        if (r.getVideoUrls() != null) p.setVideoUrls(toSet(r.getVideoUrls()));
        syncMainPhotoWithGallery(p);

        handlePublishToggle(p, r.getPublished());
    }

    private void fillCreator(PerformerProfile p, UpdateCreatorProfileRequest r) {

        if (r.getFirstName() != null) p.setFirstName(r.getFirstName());
        if (r.getLastName() != null) p.setLastName(r.getLastName());
        if (r.getDisplayName() != null) p.setDisplayName(r.getDisplayName());
        if (r.getCity() != null) p.setCity(r.getCity());
        if (r.getMainPhotoUrl() != null) p.setMainPhotoUrl(r.getMainPhotoUrl());
        if (r.getDescription() != null) p.setDescription(r.getDescription());
        if (r.getBio() != null) p.setBio(r.getBio());
        if (r.getExperienceText() != null) p.setExperienceText(r.getExperienceText());
        if (r.getActivityType() != null) p.setActivityType(r.getActivityType());
        if (r.getExperienceLevel() != null) p.setExperienceLevel(r.getExperienceLevel());
        if (r.getProjectFormatsJson() != null) p.setProjectFormatsJson(r.getProjectFormatsJson());
        if (r.getAchievements() != null) p.setAchievements(r.getAchievements());
        if (r.getSkillsJson() != null) p.setSkillsJson(r.getSkillsJson());
        if (r.getMinRate() != null) p.setMinRate(r.getMinRate());
        if (r.getRateUnit() != null) p.setRateUnit(r.getRateUnit());
        if (r.getSocialLinksJson() != null) p.setSocialLinksJson(r.getSocialLinksJson());

        setContacts(p, r);

        if (r.getPhotoUrls() != null) p.setPhotoUrls(toSet(r.getPhotoUrls()));
        if (r.getVideoUrls() != null) p.setVideoUrls(toSet(r.getVideoUrls()));
        syncMainPhotoWithGallery(p);

        handlePublishToggle(p, r.getPublished());
    }

    private void fillLocation(PerformerProfile p, UpdateLocationProfileRequest r) {

        if (r.getLocationName() != null) p.setLocationName(r.getLocationName());
        if (r.getAddress() != null) p.setAddress(r.getAddress());
        if (r.getCity() != null) p.setCity(r.getCity());
        if (r.getMainPhotoUrl() != null) p.setMainPhotoUrl(r.getMainPhotoUrl());
        if (r.getDescription() != null) p.setDescription(r.getDescription());
        if (r.getRentPrice() != null) p.setRentPrice(r.getRentPrice());
        if (r.getRentPriceUnit() != null) p.setRentPriceUnit(r.getRentPriceUnit());
        if (r.getFloor() != null) p.setFloor(r.getFloor());
        if (r.getLocationType() != null) p.setLocationType(r.getLocationType());
        if (r.getAvailabilityFrom() != null) p.setAvailabilityFrom(r.getAvailabilityFrom());
        if (r.getAvailabilityTo() != null) p.setAvailabilityTo(r.getAvailabilityTo());
        if (r.getRentalTerms() != null) p.setRentalTerms(r.getRentalTerms());
        if (r.getExtraConditions() != null) p.setExtraConditions(r.getExtraConditions());

        setContacts(p, r);

        if (r.getPhotoUrls() != null) p.setPhotoUrls(toSet(r.getPhotoUrls()));
        if (r.getVideoUrls() != null) p.setVideoUrls(toSet(r.getVideoUrls()));
        syncMainPhotoWithGallery(p);

        handlePublishToggle(p, r.getPublished());
    }

    private void handlePublishToggle(PerformerProfile p, Boolean published) {

        if (published == null) return;

        if (published) validateBeforePublish(p);

        p.setPublished(published);
    }

    /* ================= CONTACTS ================= */

    private void setContacts(PerformerProfile p, Object r) {

        if (r instanceof CreateActorProfileRequest a) {
            p.setContactPhone(a.getContactPhone());
            p.setContactEmail(a.getContactEmail());
            p.setContactWhatsapp(a.getContactWhatsapp());
            p.setContactTelegram(a.getContactTelegram());
            p.setContactInstagram(a.getContactInstagram());
        }

        if (r instanceof UpdateActorProfileRequest a) {
            if (a.getContactPhone() != null) p.setContactPhone(a.getContactPhone());
            if (a.getContactEmail() != null) p.setContactEmail(a.getContactEmail());
            if (a.getContactWhatsapp() != null) p.setContactWhatsapp(a.getContactWhatsapp());
            if (a.getContactTelegram() != null) p.setContactTelegram(a.getContactTelegram());
            if (a.getContactInstagram() != null) p.setContactInstagram(a.getContactInstagram());
        }

        if (r instanceof CreateCreatorProfileRequest c) {
            p.setContactPhone(c.getContactPhone());
            p.setContactEmail(c.getContactEmail());
            p.setContactWhatsapp(c.getContactWhatsapp());
            p.setContactTelegram(c.getContactTelegram());
            p.setContactInstagram(c.getContactInstagram());
        }

        if (r instanceof UpdateCreatorProfileRequest c) {
            if (c.getContactPhone() != null) p.setContactPhone(c.getContactPhone());
            if (c.getContactEmail() != null) p.setContactEmail(c.getContactEmail());
            if (c.getContactWhatsapp() != null) p.setContactWhatsapp(c.getContactWhatsapp());
            if (c.getContactTelegram() != null) p.setContactTelegram(c.getContactTelegram());
            if (c.getContactInstagram() != null) p.setContactInstagram(c.getContactInstagram());
        }

        if (r instanceof CreateLocationProfileRequest l) {
            p.setContactPhone(l.getContactPhone());
            p.setContactEmail(l.getContactEmail());
            p.setContactWhatsapp(l.getContactWhatsapp());
            p.setContactTelegram(l.getContactTelegram());
        }

        if (r instanceof UpdateLocationProfileRequest l) {
            if (l.getContactPhone() != null) p.setContactPhone(l.getContactPhone());
            if (l.getContactEmail() != null) p.setContactEmail(l.getContactEmail());
            if (l.getContactWhatsapp() != null) p.setContactWhatsapp(l.getContactWhatsapp());
            if (l.getContactTelegram() != null) p.setContactTelegram(l.getContactTelegram());
        }
    }

    private void applyDefaultContactsFromUser(PerformerProfile profile, User owner) {

        if (isBlank(profile.getContactEmail())) profile.setContactEmail(owner.getEmail());
        if (isBlank(profile.getContactPhone())) profile.setContactPhone(owner.getPhone());
        if (isBlank(profile.getContactTelegram())) profile.setContactTelegram(owner.getTelegram());
    }

    /* ================= MAPPING ================= */

    private ProfileResponse map(PerformerProfile p, boolean includePremiumOffer) {

        ProfileResponse r = new ProfileResponse();

        r.setId(p.getId());
        r.setType(p.getType());
        r.setPublished(p.isPublished());
        r.setFirstName(p.getFirstName());
        r.setLastName(p.getLastName());
        r.setDisplayName(p.getDisplayName());
        r.setCity(p.getCity());

        String mainPhoto = p.getMainPhotoUrl();

        if (mainPhoto == null && p.getPhotoUrls() != null && !p.getPhotoUrls().isEmpty()) {
            mainPhoto = p.getPhotoUrls().iterator().next();
        }

        r.setMainPhotoUrl(normalizeUrl(mainPhoto));

        r.setPhotoUrls(
                p.getPhotoUrls() == null
                        ? List.of()
                        : p.getPhotoUrls().stream().map(this::normalizeUrl).toList()
        );

        r.setVideoUrls(
                p.getVideoUrls() == null
                        ? List.of()
                        : p.getVideoUrls().stream().map(this::normalizeUrl).toList()
        );

        r.setDescription(p.getDescription());
        r.setBio(p.getBio());

        r.setGender(p.getGender());
        r.setAge(p.getAge());
        r.setEthnicity(p.getEthnicity());
        r.setHeightCm(p.getHeightCm());
        r.setWeightKg(p.getWeightKg());
        r.setBodyType(p.getBodyType());
        r.setHairColor(p.getHairColor());
        r.setEyeColor(p.getEyeColor());
        r.setGameAgeFrom(p.getGameAgeFrom());
        r.setGameAgeTo(p.getGameAgeTo());
        r.setSkillsJson(p.getSkillsJson());
        r.setIntroVideoUrl(normalizeUrl(p.getIntroVideoUrl()));
        r.setMonologueVideoUrl(normalizeUrl(p.getMonologueVideoUrl()));
        r.setSelfTapeVideoUrl(normalizeUrl(p.getSelfTapeVideoUrl()));

        r.setActivityType(p.getActivityType());
        r.setExperienceText(p.getExperienceText());
        r.setExperienceLevel(p.getExperienceLevel());
        r.setProjectFormatsJson(p.getProjectFormatsJson());
        r.setAchievements(p.getAchievements());
        r.setSocialLinksJson(p.getSocialLinksJson());

        r.setLocationName(p.getLocationName());
        r.setAddress(p.getAddress());
        r.setRentPrice(p.getRentPrice());
        r.setRentPriceUnit(p.getRentPriceUnit());
        r.setFloor(p.getFloor());
        r.setLocationType(p.getLocationType());
        r.setAvailabilityFrom(p.getAvailabilityFrom());
        r.setAvailabilityTo(p.getAvailabilityTo());
        r.setRentalTerms(p.getRentalTerms());
        r.setExtraConditions(p.getExtraConditions());

        r.setMinRate(p.getMinRate());
        r.setRateUnit(p.getRateUnit());
        r.setPremiumActive(isPremiumActive(p));
        r.setPremiumUntil(p.getPremiumUntil());
        r.setContactPhone(p.getContactPhone());
        r.setContactEmail(p.getContactEmail());
        r.setContactWhatsapp(p.getContactWhatsapp());
        r.setContactTelegram(p.getContactTelegram());
        r.setContactInstagram(p.getContactInstagram());

        if (includePremiumOffer && p.getType() != null) {
            CustomerSubscriptionPlan plan = getActivePlanOrNull();
            r.setPremiumPurchaseAvailable(plan != null);
            r.setPremiumCheckoutEndpoint(plan != null ? "/api/payments/profile-premium" : null);
            r.setPremiumPrice(plan != null ? plan.getPremiumProfilePrice() : null);
            r.setPremiumDurationDays(plan != null ? plan.getPremiumProfileDays() : null);
        } else {
            r.setPremiumPurchaseAvailable(false);
        }

        return r;
    }

    private CustomerProfileResponse mapCustomer(User u) {

        CustomerProfileResponse r = new CustomerProfileResponse();

        r.setId(u.getId());
        r.setDisplayName(u.getEmail());
        r.setCity(u.getCity());
        r.setDescription(u.getDescription());
        r.setMainPhotoUrl(normalizeUrl(u.getAvatarUrl()));
        r.setContactEmail(u.getEmail());
        r.setContactPhone(u.getPhone());
        r.setContactTelegram(u.getTelegram());
        r.setPublished(true);

        return r;
    }

    /* ================= HELPERS ================= */

    private Set<String> toSet(List<String> list) {
        return list == null ? new LinkedHashSet<>() : new LinkedHashSet<>(list);
    }

    private void syncMainPhotoWithGallery(PerformerProfile profile) {

        Set<String> photoUrls = profile.getPhotoUrls();
        String mainPhotoUrl = profile.getMainPhotoUrl();

        if (photoUrls == null || photoUrls.isEmpty()) {
            profile.setMainPhotoUrl(null);
            profile.setPublished(false);
            return;
        }

        if (isBlank(mainPhotoUrl) || !photoUrls.contains(mainPhotoUrl)) {
            profile.setMainPhotoUrl(photoUrls.iterator().next());
        }
    }

    private String normalizeUrl(String url) {

        if (url == null || url.isBlank()) return null;

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        String base = publicUrl.endsWith("/")
                ? publicUrl.substring(0, publicUrl.length() - 1)
                : publicUrl;

        String path = url.startsWith("/") ? url : "/" + url;

        return base + path;
    }

    private void validateBeforePublish(PerformerProfile p) {

        if ((p.getMainPhotoUrl() == null || p.getMainPhotoUrl().isBlank())
                && p.getPhotoUrls() != null
                && !p.getPhotoUrls().isEmpty()) {

            p.setMainPhotoUrl(p.getPhotoUrls().iterator().next());
        }

        if (p.getMainPhotoUrl() == null || p.getMainPhotoUrl().isBlank()) {
            throw new BadRequestException("Add photo before publishing");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private boolean isPremiumActive(PerformerProfile profile) {
        return profile.getPremiumUntil() != null && profile.getPremiumUntil().isAfter(LocalDateTime.now());
    }

    private CustomerSubscriptionPlan getActivePlanOrNull() {
        var activePlans = planRepository.findByActiveTrueOrderByIdAsc();
        if (activePlans.size() != 1) {
            return null;
        }
        return activePlans.get(0);
    }

    private <E extends Enum<E>> E parseEnum(String value, Class<E> type) {

        if (value == null || value.isBlank()) return null;

        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid value for " + type.getSimpleName());
        }
    }

    private User getCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth.getPrincipal() instanceof UserPrincipal p)) {
            throw new ForbiddenException("Unauthenticated");
        }

        return userRepository.findById(p.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private boolean isAllowed(UserRole role, PerformerType type) {
        return switch (role) {
            case ACTOR -> type == PerformerType.ACTOR;
            case CREATOR -> type == PerformerType.CREATOR;
            case LOCATION_OWNER -> type == PerformerType.LOCATION;
            case ADMIN -> true;
            case CUSTOMER -> false;
        };
    }
}
