package com.casting.platform.controller;

import com.casting.platform.dto.request.profile.CreateActorProfileRequest;
import com.casting.platform.dto.request.profile.CreateCreatorProfileRequest;
import com.casting.platform.dto.request.profile.CreateLocationProfileRequest;
import com.casting.platform.dto.request.profile.UpdateActorProfileRequest;
import com.casting.platform.dto.request.profile.UpdateCreatorProfileRequest;
import com.casting.platform.dto.request.profile.UpdateLocationProfileRequest;
import com.casting.platform.dto.response.profile.ProfileResponse;
import com.casting.platform.entity.PerformerType;
import com.casting.platform.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /* =======================
       CREATE
       ======================= */

    @PostMapping("/actor")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse createActorProfile(
            @RequestBody CreateActorProfileRequest request
    ) {
        return profileService.createProfileForCurrentUser(
                PerformerType.ACTOR,
                request
        );
    }

    @PostMapping("/creator")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse createCreatorProfile(
            @RequestBody CreateCreatorProfileRequest request
    ) {
        return profileService.createProfileForCurrentUser(
                PerformerType.CREATOR,
                request
        );
    }

    @PostMapping("/location")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse createLocationProfile(
            @RequestBody CreateLocationProfileRequest request
    ) {
        return profileService.createProfileForCurrentUser(
                PerformerType.LOCATION,
                request
        );
    }

    /* =======================
       UPDATE (PATCH)
       ======================= */

   /* =======================
   UPDATE (PATCH)
   ======================= */

    @PatchMapping("/actor")
    public ProfileResponse updateActorProfile(
            @RequestBody UpdateActorProfileRequest request
    ) {
        return profileService.updateActorProfile(request);
    }

    @PatchMapping("/creator")
    public ProfileResponse updateCreatorProfile(
            @RequestBody UpdateCreatorProfileRequest request
    ) {
        return profileService.updateCreatorProfile(request);
    }

    @PatchMapping("/location")
    public ProfileResponse updateLocationProfile(
            @RequestBody UpdateLocationProfileRequest request
    ) {
        return profileService.updateLocationProfile(request);
    }

    /* =======================
       GET
       ======================= */

    @GetMapping("/me")
    public ProfileResponse getMyProfile() {
        return profileService.getMyProfile();
    }

    @GetMapping("/{id}")
    public ProfileResponse getProfileById(@PathVariable Long id) {
        return profileService.getProfileById(id);
    }

}
