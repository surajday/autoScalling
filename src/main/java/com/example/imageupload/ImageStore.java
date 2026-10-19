package com.example.imageupload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImageStore {

    private final Map<UUID, byte[]> images = new ConcurrentHashMap<>();
    private final AtomicLong usedBytes = new AtomicLong();
    private final long capacityBytes;

    public ImageStore(@Value("${image-store.capacity-bytes:52428800}") long capacityBytes) {
        if (capacityBytes <= 0) {
            throw new IllegalArgumentException("image-store.capacity-bytes must be positive");
        }
        this.capacityBytes = capacityBytes;
    }

    public synchronized List<StoredImage> saveAll(List<ImageToStore> uploads) {
        long batchBytes = uploads.stream().mapToLong(upload -> upload.bytes().length).sum();
        if (batchBytes > capacityBytes - usedBytes.get()) {
            throw new StoreCapacityExceededException(capacityBytes, usedBytes.get(), batchBytes);
        }

        List<StoredImage> stored = new ArrayList<>(uploads.size());
        for (ImageToStore upload : uploads) {
            UUID id = UUID.randomUUID();
            byte[] copy = Arrays.copyOf(upload.bytes(), upload.bytes().length);
            images.put(id, copy);
            stored.add(new StoredImage(id, upload.fileName(), upload.contentType(), copy.length));
        }
        usedBytes.addAndGet(batchBytes);
        return List.copyOf(stored);
    }

    public long usedBytes() {
        return usedBytes.get();
    }

    public long capacityBytes() {
        return capacityBytes;
    }

    public record ImageToStore(String fileName, String contentType, byte[] bytes) {}

    public record StoredImage(UUID id, String fileName, String contentType, long sizeBytes) {}
}
