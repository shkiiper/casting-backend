package com.casting.platform.controller;

import com.casting.platform.dto.common.PageResponse;
import com.casting.platform.dto.response.admin.AdminUserResponse;
import com.casting.platform.dto.response.common.MessageResponse;
import com.casting.platform.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public PageResponse<AdminUserResponse> getUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        return adminUserService.getUsers(page, size, role, query, sortBy, sortDir);
    }

    @PostMapping("/{userId}/deactivate")
    public MessageResponse deactivate(@PathVariable Long userId) {
        adminUserService.deactivateUser(userId);
        return new MessageResponse("User deactivated");
    }

    @PostMapping("/{userId}/activate")
    public MessageResponse activate(@PathVariable Long userId) {
        adminUserService.activateUser(userId);
        return new MessageResponse("User activated");
    }

    @PostMapping("/{userId}/ban")
    public MessageResponse ban(@PathVariable Long userId) {
        adminUserService.banUser(userId);
        return new MessageResponse("User banned");
    }

    @PostMapping("/{userId}/unban")
    public MessageResponse unban(@PathVariable Long userId) {
        adminUserService.unbanUser(userId);
        return new MessageResponse("User unbanned");
    }

    @PostMapping("/{userId}/profile/visibility")
    public MessageResponse updateProfileVisibility(
            @PathVariable Long userId,
            @RequestParam boolean published
    ) {
        adminUserService.updateProfileVisibility(userId, published);
        return new MessageResponse(published ? "Profile published" : "Profile hidden");
    }

    @PostMapping("/{userId}/notify-missing-photo")
    public MessageResponse notifyMissingPhoto(@PathVariable Long userId) {
        adminUserService.sendMissingPhotoReminder(userId);
        return new MessageResponse("Photo reminder email sent");
    }

    @DeleteMapping("/{userId}")
    public MessageResponse delete(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return new MessageResponse("User deleted");
    }
}
