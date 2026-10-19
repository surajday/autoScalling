package com.example.imageupload;

import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    private final ImageStore imageStore;

    public ImageUploadController(ImageStore imageStore) {
        this.imageStore = imageStore;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResponse upload(@RequestPart("files") List<MultipartFile> files) {
        if (files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one image is required");
        }

        List<ImageStore.ImageToStore> images = files.stream().map(this::toImage).toList();
        List<ImageStore.StoredImage> stored = imageStore.saveAll(images);
        return new UploadResponse(stored, imageStore.usedBytes(), imageStore.capacityBytes());
    }

    private ImageStore.ImageToStore toImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty files are not allowed");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    file.getOriginalFilename() + " is not an image");
        }
        try {
            return new ImageStore.ImageToStore(file.getOriginalFilename(), contentType, file.getBytes());
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read uploaded file", exception);
        }
    }

    public record UploadResponse(List<ImageStore.StoredImage> images, long instanceUsedBytes,
                                 long instanceCapacityBytes) {}
}
