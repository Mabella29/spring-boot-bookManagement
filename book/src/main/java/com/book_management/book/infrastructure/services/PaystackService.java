package com.book_management.book.infrastructure.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Slf4j
@Service
public class PaystackService {

    @Value("${paystack.secret-key}")
    private String secretKey;

    @Value("${paystack.base-url}")
    private String baseUrl;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();


    public Mono<String> initializePayment(
            String email,
            long amount,
            String reference,
            String orderId
    ) {
        return Mono.fromCallable(() -> {

                    // build the request body Paystack expects
                    // amount must be in the smallest currency unit — multiply by 100
                    String requestBody = objectMapper.writeValueAsString(Map.of(
                            "email", email,
                            "amount", amount,
                            "reference", reference,
                            "metadata", Map.of("orderId", orderId),
                            "currency", "GHS"
                    ));

                    // build the HTTP request
                    Request request = new Request.Builder()
                            .url(baseUrl + "/transaction/initialize")
                            .addHeader("Authorization", "Bearer " + secretKey)
                            .addHeader("Content-Type", "application/json")
                            .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                            .build();

                    try (Response response = httpClient.newCall(request).execute()) {
                        String responseBody = response.body().string();
                        JsonNode json = objectMapper.readTree(responseBody);

                        if (!json.get("status").asBoolean()) {
                            throw new RuntimeException("Paystack error: " + json.get("message").asText());
                        }

                        return json.get("data").get("authorization_url").asText();
                    }

                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(url -> log.info("Payment initialized, redirect URL: {}", url))
                .doOnError(err -> log.error("Failed to initialize payment", err));
    }


    public Mono<JsonNode> verifyPayment(String reference) {
        return Mono.fromCallable(() -> {

                    Request request = new Request.Builder()
                            .url(baseUrl + "/transaction/verify/" + reference)
                            .addHeader("Authorization", "Bearer " + secretKey)
                            .get()
                            .build();

                    try (Response response = httpClient.newCall(request).execute()) {
                        String responseBody = response.body().string();
                        JsonNode json = objectMapper.readTree(responseBody);

                        if (!json.get("status").asBoolean()) {
                            throw new RuntimeException("Pay stack verification error: " + json.get("message").asText());
                        }
                        return json.get("data");
                    }

                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(data -> log.info("Payment verified: {}", data.get("reference").asText()))
                .doOnError(err -> log.error("Failed to verify payment", err));
    }

    public boolean isValidWebhook(String payload, String signature) {
        try {
            // compute HMAC SHA512 of the raw payload using the secret key
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec keySpec =
                    new javax.crypto.spec.SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes());

            // convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // compare computed signature with what Paystack sent
            // if they match, the webhook is genuine
            return hexString.toString().equals(signature);

        } catch (Exception e) {
            log.error("Failed to validate webhook signature", e);
            return false;
        }
    }
}