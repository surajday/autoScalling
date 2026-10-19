package com.example.imageupload;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class ImageStoreTest {

    @Test
    void rejectsWholeBatchWhenInstanceCapacityWouldBeExceeded() {
        ImageStore store = new ImageStore(4);
        List<ImageStore.ImageToStore> batch = List.of(
                new ImageStore.ImageToStore("one.png", "image/png", new byte[] {1, 2, 3}),
                new ImageStore.ImageToStore("two.png", "image/png", new byte[] {4, 5}));

        assertThatThrownBy(() -> store.saveAll(batch))
                .isInstanceOf(StoreCapacityExceededException.class);
    }
}
