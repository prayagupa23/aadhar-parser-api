package com.example.aadharparserapi.web;

import com.example.aadharparserapi.service.AadhaarQrService;
import com.google.zxing.NotFoundException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/aadhaar")
@Validated
public class AadhaarController {

    private final AadhaarQrService aadhaarQrService;

    public AadhaarController(AadhaarQrService aadhaarQrService) {
        this.aadhaarQrService = aadhaarQrService;
    }

    @PostMapping(value = "/parse-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> parseFromImage(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        try {
            String xml = aadhaarQrService.decodeQrXmlFromImage(file.getInputStream());
            Map<String, String> data = aadhaarQrService.parseAadhaarXmlSecurely(xml);
            return ResponseEntity.ok(Map.of(
                    "xml", xml,
                    "data", data
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", "QR code not found in image"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid image: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to parse: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/parse-xml", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> parseFromXml(@RequestBody @NotBlank String xml) {
        try {
            Map<String, String> data = aadhaarQrService.parseAadhaarXmlSecurely(xml);
            return ResponseEntity.ok(Map.of(
                    "xml", xml,
                    "data", data
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to parse: " + e.getMessage()));
        }
    }
}


