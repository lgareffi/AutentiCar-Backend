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

    public AiMlFullResponse analyzeBytes(byte[] bytes, String filename, String contentType) {
        try {
            String url = baseUrl + "/risk-ml?language=" +
                    URLEncoder.encode(language, StandardCharsets.UTF_8);

            String safeContentType = (contentType != null && !contentType.isBlank())
                    ? contentType
                    : "application/octet-stream";

            String safeFilename = (filename != null && !filename.isBlank())
                    ? filename
                    : "upload" + inferExt(safeContentType);

            ByteArrayResource fileRes = new ByteArrayResource(bytes) {
                @Override public String getFilename() { return safeFilename; }
            };

            HttpHeaders partHeaders = new HttpHeaders();
            partHeaders.setContentType(MediaType.parseMediaType(safeContentType));
            partHeaders.setContentDisposition(ContentDisposition
                    .builder("form-data")
                    .name("file")
                    .filename(safeFilename)
                    .build());

            HttpEntity<ByteArrayResource> filePart = new HttpEntity<>(fileRes, partHeaders);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", filePart);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> req = new HttpEntity<>(body, headers);

            ResponseEntity<AiMlFullResponse> resp =
                    rest.postForEntity(url, req, AiMlFullResponse.class);

            return resp.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Fallo al llamar IA (/risk-ml con bytes): " + e.getMessage(), e);
        }
    }

    public DownloadedFile download(String fileUrl) {
        try {
            ResponseEntity<byte[]> r = rest.getForEntity(fileUrl, byte[].class);
            String contentType = null;
            if (r.getHeaders() != null && r.getHeaders().getContentType() != null) {
                contentType = r.getHeaders().getContentType().toString();
            }
            byte[] bytes = r.getBody();
            if (bytes == null || bytes.length == 0) throw new RuntimeException("Archivo vacío al descargar");
            return new DownloadedFile(bytes, contentType);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo descargar el archivo: " + e.getMessage(), e);
        }
    }

    public static class DownloadedFile {
        public final byte[] bytes;
        public final String contentType;
        public DownloadedFile(byte[] b, String ct) { this.bytes = b; this.contentType = ct; }
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

