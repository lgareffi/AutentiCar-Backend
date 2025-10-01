package app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class AiClient {

    @Value("${ai.base-url:http://127.0.0.1:8000}")
    private String baseUrl;

    @Value("${ai.language:spa}")
    private String language;

    private final RestTemplate rest;

    public AiClient(RestTemplateBuilder builder) {
        this.rest = builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(90))
                .build();
    }

    public AiResponse analyze(MultipartFile file) {
        try {
            String url = baseUrl + "/risk-ml?language=" +
                    URLEncoder.encode(language, StandardCharsets.UTF_8);

            HttpEntity<MultiValueMap<String, Object>> req = buildMultipart(file);
            ResponseEntity<AiResponse> resp = rest.postForEntity(url, req, AiResponse.class);
            return resp.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Fallo al llamar IA: " + e.getMessage(), e);
        }
    }

    private HttpEntity<MultiValueMap<String, Object>> buildMultipart(MultipartFile file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Leer los bytes y manejar IOException acá
        final byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo subido", e);
        }

        ByteArrayResource fileRes = new ByteArrayResource(bytes) {
            @Override public String getFilename() {
                String name = file.getOriginalFilename();
                if (name == null || name.isBlank()) {
                    return "upload" + inferExt(file.getContentType());
                }
                return name;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileRes);

        return new HttpEntity<>(body, headers);
    }

    private String inferExt(String mime) {
        if ("application/pdf".equals(mime)) return ".pdf";
        if (mime != null && mime.startsWith("image/")) {
            String ext = mime.substring("image/".length());
            if ("jpeg".equalsIgnoreCase(ext)) ext = "jpg";
            return "." + ext;
        }
        return "";
    }
}

