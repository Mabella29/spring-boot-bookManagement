package com.book_management.book.infrastructure.services;

import com.book_management.book.application.interfaces.OrderService;
import com.book_management.book.domain.enums.PaymentMethod;
import com.book_management.book.domain.enums.PaymentStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class PaystackService {

    @Value("${paystack.secret-key}")
    private String secretKey;

    @Value("${paystack.base-url}")
    private String baseUrl;

    private final OkHttpClient httpClient = new OkHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    public Mono<String> initializePayment(
            String email,
            long amount,
            String reference,
            String orderId
    ) {
        return Mono.fromCallable(() -> {

                    String requestBody = OBJECT_MAPPER.writeValueAsString(Map.of(
                            "email", email,
                            "amount", amount,
                            "reference", reference,
                            "metadata", Map.of("orderId", orderId),
                            "currency", "GHS"
                    ));

                    Request request = new Request.Builder()
                            .url(baseUrl + "/transaction/initialize")
                            .addHeader("Authorization", "Bearer " + secretKey)
                            .addHeader("Content-Type", "application/json")
                            .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                            .build();

                    try (Response response = httpClient.newCall(request).execute()) {
                        if (response.body() == null) {
                            throw new RuntimeException("Empty response from PayStack");
                        }
                        String responseBody = response.body().string();
                        JsonNode json = OBJECT_MAPPER.readTree(responseBody);

                        if (!json.get("status").asBoolean()) {
                            throw new RuntimeException("PayStack error: " + json.get("message").asText());
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
                        if (response.body() == null) {
                            throw new RuntimeException("Empty response from Paystack");
                        }
                        String responseBody = response.body().string();
                        JsonNode json = OBJECT_MAPPER.readTree(responseBody);

                        if (!json.get("status").asBoolean()) {
                            throw new RuntimeException("PayStack verification error: " + json.get("message").asText());
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
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString().equals(signature);

        } catch (Exception e) {
            log.error("Failed to validate webhook signature", e);
            return false;
        }
    }

    public Mono<Void> handleWebhook(String payload, OrderService orderService) {
        try {
            JsonNode event = OBJECT_MAPPER.readTree(payload);
            String eventType = event.get("event").asText();

            if (!"charge.success".equals(eventType)) {
                return Mono.empty();
            }

            JsonNode data = event.get("data");
            String reference = data.get("reference").asText();
            String orderId = data.get("metadata").get("orderId").asText();

            return verifyPayment(reference)
                    .flatMap(verifiedData -> {
                        String status = verifiedData.get("status").asText();
                        String channel = verifiedData.get("channel").asText();

                        PaymentMethod paymentMethod = switch (channel) {
                            case "card" -> PaymentMethod.CARD;
                            case "bank" -> PaymentMethod.BANK_TRANSFER;
                            case "mobile_money" -> PaymentMethod.MOBILE_MONEY;
                            default -> PaymentMethod.CARD;
                        };

                        PaymentStatus paymentStatus = "success".equals(status)
                                ? PaymentStatus.SUCCESS
                                : PaymentStatus.FAILED;

                        BigDecimal amountPaid = BigDecimal.valueOf(
                                verifiedData.get("amount").asLong()
                        ).divide(BigDecimal.valueOf(100));

                        return orderService.updatePayment(
                                UUID.fromString(orderId),
                                paymentStatus.name(),
                                paymentMethod.name(),
                                reference,
                                amountPaid
                        ).then();
                    });

        } catch (Exception e) {
            log.error("Failed to parse webhook payload", e);
            return Mono.error(e);
        }
    }
}