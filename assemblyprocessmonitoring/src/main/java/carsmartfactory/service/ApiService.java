package carsmartfactory.service;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;


@Slf4j
@Service
public class ApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String sendImage(MultipartFile image)  {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        log.info("=== AI 서버 요청 정보 ===");
        log.info("파일명: {}", image.getOriginalFilename());
        log.info("파일 크기: {}", image.getSize());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            // 이미지 리사이징
            byte[] resizedImageBytes = resizeImage(image);
            log.info("리사이징 후 크기: {} bytes", resizedImageBytes.length);

            log.info("Content Type: {}", image.getContentType());

            body.add("file", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    String originalName = image.getOriginalFilename();

                    // URL 디코딩
                    String decoded;
                    try {
                        decoded = URLDecoder.decode(originalName, "UTF-8");
                    } catch (Exception e) {
                        decoded = originalName;
                    }

                    // 마지막 '/' 이후의 파일명만 추출
                    String pureName = decoded.substring(decoded.lastIndexOf("/") + 1);

                    log.info("파싱된 파일명: {}", pureName);
                    return pureName;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 실패", e);
        }


        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        log.info("AI 서버로 요청 전송 시작...");
        String result = restTemplate.postForObject("http://localhost:8005/predict/file", request, String.class);

        log.info("AI 서버 응답 성공: {}", result);
        return result;
    }

    private byte[] resizeImage(MultipartFile image) throws IOException {
        BufferedImage originalImage = ImageIO.read(image.getInputStream());

        // 너비를 800px로 제한 (비율 유지)
        BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY,
                Scalr.Mode.FIT_TO_WIDTH, 800);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);

        log.info("이미지 리사이징 완료: {}x{} -> {}x{}",
                originalImage.getWidth(), originalImage.getHeight(),
                resizedImage.getWidth(), resizedImage.getHeight());

        return baos.toByteArray();
    }
}
