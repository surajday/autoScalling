package com.example.imageupload;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "image-store.capacity-bytes=12")
class ImageUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void uploadsMultipleImagesAsByteArrays() throws Exception {
        MockMultipartFile first = new MockMultipartFile("files", "first.png", "image/png", new byte[] {1, 2, 3});
        MockMultipartFile second = new MockMultipartFile("files", "second.jpg", "image/jpeg", new byte[] {4, 5});

        mockMvc.perform(multipart("/api/images").file(first).file(second))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images", hasSize(2)))
                .andExpect(jsonPath("$.instanceUsedBytes").value(5))
                .andExpect(jsonPath("$.instanceCapacityBytes").value(12));
    }

    @Test
    void rejectsNonImageContent() throws Exception {
        MockMultipartFile text = new MockMultipartFile("files", "notes.txt", "text/plain", new byte[] {1});

        mockMvc.perform(multipart("/api/images").file(text))
                .andExpect(status().isUnsupportedMediaType());
    }
}
