package com.Reboot.Minty.member.service;

import com.Reboot.Minty.member.dto.CustomResponse;
import com.Reboot.Minty.member.dto.SmsMessageDto;
import com.Reboot.Minty.member.dto.SmsMessageRequestDto;
import com.Reboot.Minty.member.dto.SmsMessageResponseDto;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor

public class SmsService {
    private final WebClient webClient;

   private final UserRepository userRepository;
    private final String VERIFICATION_PREFIX = "sms:";
    private final int VERIFICATION_TIME_LIMIT = 3;


    @Value("${naver-cloud-sms.accessKey}")
    private String accessKey;

    @Value("${naver-cloud-sms.secretKey}")
    private String secretKey;

    @Value("${naver-cloud-sms.serviceId}")
    private String serviceId;

    @Value("${naver-cloud-sms.senderPhone}")
    private String sender;


    public String makeSignature(Long time) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        System.out.println("makesignature 실행");
        String space = " ";                    // one space
        String newLine = "\n";                    // new line
        String method = "POST";                    // method
        String url = "/sms/v2/services/" + this.serviceId + "/messages";    // url (include query string)
        String timestamp = time.toString();            // current timestamp (epoch)
        String accessKey = this.accessKey;            // access key id (from portal or Sub Account)
        String secretKey = this.secretKey;

        String message = method +
                space +
                url +
                newLine +
                timestamp +
                newLine +
                accessKey;

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);
        System.out.println(encodeBase64String);
        return encodeBase64String;
    }


    public Mono<CustomResponse> sendSms(String to) throws JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        final String smsURL = "/sms/v2/services/" + this.serviceId + "/messages";
        ObjectMapper objectMapper = new ObjectMapper();
        Long time = System.currentTimeMillis();
        System.out.println(to);
        final String verificationCode = generateVerificationCode();
        // 3분 제한시간
        final Duration verificationTimeLimit = Duration.ofMinutes(VERIFICATION_TIME_LIMIT);

        final SmsMessageDto messageDto = new SmsMessageDto(to, generateMessageWithCode(verificationCode));
        System.out.println(messageDto.toString());
        List<SmsMessageDto> messages = new ArrayList<>();
        messages.add(messageDto);

        SmsMessageRequestDto request = SmsMessageRequestDto.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(sender)
                .content(messageDto.getContent())
                .messages(messages)
                .build();

        // Convert request to JSON
        final String body = new ObjectMapper().writeValueAsString(request);

        // Generate signature
        String signature = makeSignature(time);

        return webClient.post()
                .uri(smsURL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-ncp-apigw-timestamp", time.toString())
                .header("x-ncp-iam-access-key", accessKey)
                .header("x-ncp-apigw-signature-v2", signature)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "클라이언트 오류가 발생했습니다.")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.")))
                .bodyToMono(SmsMessageResponseDto.class)
                .flatMap(response -> {
                    CustomResponse customResponse = CustomResponse.builder()
                            .verificationCode(verificationCode).verificationTimeLimit(verificationTimeLimit).smsResponse(response)
                            .build();
                    return Mono.just(customResponse);
                })
                .doOnError(error -> {
                    log.error("Failed to send SMS: {}", error.getMessage());
                });
    }

    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public String generateMessageWithCode(String verificationCode) {
        return "[Minty 휴대폰 인증 서비스] 인증번호 [" + verificationCode + "] 를 입력해주세요.";
    }

    public String generateVerificationKey(String phoneNumber) {
        return VERIFICATION_PREFIX + phoneNumber;
    }

    public int getVerificationTimeLimit() {
        return VERIFICATION_TIME_LIMIT;
    }
}