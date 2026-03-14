package com.casting.platform.controller;

import com.casting.platform.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService storageService;

    /** допустимые mime-типы */
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/heic",   // ✅ iPhone
            "image/heif"    // ✅ iPhone
    );

    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4",
            "video/webm",
            "video/quicktime" // ✅ MOV (iPhone/macOS)
    );

    /** 50 MB */
    private static final long MAX_FILE_SIZE = 50L * 1024 * 1024;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<String>> upload(
            @RequestParam("files") List<MultipartFile> files
    ) {

        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No files provided"
            );
        }

        List<String> urls = files.stream()
                .map(this::validateAndStore)
                .toList();

        return ResponseEntity.ok(urls);
    }

    /* =======================
       INTERNAL
       ======================= */

    private String validateAndStore(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Empty file"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "File too large: " + file.getOriginalFilename()
            );
        }

        if (!isAllowed(file)) {
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Unsupported file type: " + file.getContentType()
            );
        }

        return storageService.store(file);
    }

    private boolean isAllowed(MultipartFile file) {

        String contentType = file.getContentType();
        String name = file.getOriginalFilename();

        // ===== MIME проверка =====
        if (contentType != null) {
            if (ALLOWED_IMAGE_TYPES.contains(contentType) ||
                    ALLOWED_VIDEO_TYPES.contains(contentType)) {
                return true;
            }
        }

        // ===== fallback по расширению (Safari / mobile) =====
        if (name != null) {

            String lower = name.toLowerCase();

            return lower.endsWith(".jpg")
                    || lower.endsWith(".jpeg")
                    || lower.endsWith(".png")
                    || lower.endsWith(".webp")
                    || lower.endsWith(".heic")
                    || lower.endsWith(".heif")
                    || lower.endsWith(".mp4")
                    || lower.endsWith(".webm")
                    || lower.endsWith(".mov");
        }

        return false;
    }
}