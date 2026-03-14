package com.casting.platform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;
    private final String publicPath;
    private final int maxImageDimension;
    private final float jpegQuality;

    public FileStorageService(
            @Value("${app.upload.dir:uploads}") String uploadDir,
            @Value("${app.upload.public-path:/uploads}") String publicPath,
            @Value("${app.upload.max-image-dimension:2560}") int maxImageDimension,
            @Value("${app.upload.jpeg-quality:0.82}") float jpegQuality
    ) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.publicPath = publicPath;
        this.maxImageDimension = maxImageDimension;
        this.jpegQuality = jpegQuality;

        try {
            Files.createDirectories(this.uploadDir);
            Files.createDirectories(this.uploadDir.resolve("images"));
            Files.createDirectories(this.uploadDir.resolve("videos"));
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory", e);
        }
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        String originalName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename())
        );

        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot > 0) {
            ext = originalName.substring(dot);
        }

        try {
            if (isImage(file)) {
                return storeOptimizedImage(file, ext);
            }
            return storeBinary(file, "videos", ext);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private String storeOptimizedImage(MultipartFile file, String originalExt) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage source = ImageIO.read(inputStream);
            if (source == null) {
                return storeBinary(file, "images", normalizeExtension(originalExt, "jpg"));
            }

            BufferedImage resized = resizeIfNeeded(source);
            boolean hasAlpha = resized.getColorModel().hasAlpha();
            String format = hasAlpha ? "png" : "jpg";
            String extension = "." + format;
            String filename = UUID.randomUUID() + extension;
            Path target = uploadDir.resolve("images").resolve(filename);

            try (OutputStream outputStream = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW)) {
                writeImage(resized, format, outputStream);
            }

            return publicPath + "/images/" + filename;
        }
    }

    private String storeBinary(MultipartFile file, String folder, String ext) throws IOException {
        String filename = UUID.randomUUID() + normalizeExtension(ext, "");
        Path target = uploadDir.resolve(folder).resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return publicPath + "/" + folder + "/" + filename;
    }

    private BufferedImage resizeIfNeeded(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        int maxDimension = Math.max(width, height);
        if (maxDimension <= maxImageDimension) {
            return source;
        }

        double ratio = (double) maxImageDimension / maxDimension;
        int targetWidth = Math.max(1, (int) Math.round(width * ratio));
        int targetHeight = Math.max(1, (int) Math.round(height * ratio));
        int imageType = source.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;

        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, imageType);
        Graphics2D graphics = resized.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        return resized;
    }

    private void writeImage(BufferedImage image, String format, OutputStream outputStream) throws IOException {
        if ("jpg".equals(format)) {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream)) {
                writer.setOutput(imageOutputStream);
                ImageWriteParam writeParam = writer.getDefaultWriteParam();
                if (writeParam.canWriteCompressed()) {
                    writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    writeParam.setCompressionQuality(jpegQuality);
                }
                writer.write(null, new IIOImage(image, null, null), writeParam);
            } finally {
                writer.dispose();
            }
            return;
        }

        ImageIO.write(image, format, outputStream);
    }

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private String normalizeExtension(String ext, String fallback) {
        if (ext == null || ext.isBlank()) {
            return fallback.isBlank() ? "" : "." + fallback;
        }
        return ext.startsWith(".") ? ext.toLowerCase() : "." + ext.toLowerCase();
    }
}
