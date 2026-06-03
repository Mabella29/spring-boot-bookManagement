package com.book_management.book.infrastructure.controllers;

import com.book_management.book.application.interfaces.OrderService;
import com.book_management.book.domain.dto.ApiResponse;
import com.book_management.book.domain.dto.PaymentInitRequest;
import com.book_management.book.domain.dto.PaymentInitResponse;
import com.book_management.book.domain.enums.PaymentMethod;
import com.book_management.book.domain.enums.PaymentStatus;
import com.book_management.book.infrastructure.services.PaystackService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaystackService paystackService;
    private final OrderService orderService;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    @PostMapping("/initialize")
    public Mono<ResponseEntity<ApiResponse<PaymentInitResponse>>> initializePayment(
            @AuthenticationPrincipal Mono<String> userPrincipal,
            @Valid @RequestBody PaymentInitRequest request
    ) {
        return userPrincipal.flatMap(userId -> {
            return orderService.getOrderById(request.getOrderId())
                    .flatMap(order -> {
                        String reference = "ORDER-" + request.getOrderId() + "-" + System.currentTimeMillis();

                        // convert total price to the smallest currency unit
                        // multiply by 100
                        long amountInPesewas = order.getTotalPrice()
                                .multiply(BigDecimal.valueOf(100))
                                .longValue();

                        return paystackService.initializePayment(
                                request.getEmail(),
                                amountInPesewas,
                                reference,
                                request.getOrderId().toString()
                        ).map(authUrl ->
                                ResponseEntity.ok(new ApiResponse<>(
                                        true,
                                        "Payment initialized",
                                        PaymentInitResponse.builder()
                                                .authorizationUrl(authUrl)
                                                .reference(reference)
                                                .orderId(request.getOrderId().toString())
                                                .build()
                                ))
                        );
                    });
        });
    }


    @PostMapping("/webhook")
    public Mono<ResponseEntity<Void>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Paystack-Signature") String signature
    ) {
        if (!paystackService.isValidWebhook(payload, signature)) {
            log.warn("Invalid webhook signature — ignoring");
            return Mono.just(ResponseEntity.ok().<Void>build());
        }

        try {
            JsonNode event = OBJECT_MAPPER.readTree(payload);
            String eventType = event.get("event").asText();

            if (!"charge.success".equals(eventType)) {
                return Mono.just(ResponseEntity.ok().<Void>build());
            }

            JsonNode data = event.get("data");
            String reference = data.get("reference").asText();

            String orderId = data.get("metadata").get("orderId").asText();


            return paystackService.verifyPayment(reference)
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
                        );
                    })
                    .then(Mono.just(ResponseEntity.ok().<Void>build()))
                    .doOnError(err -> log.error("Failed to process webhook", err))
                    .onErrorReturn(ResponseEntity.ok().<Void>build());

        } catch (Exception e) {
            log.error("Failed to parse webhook payload", e);
            return Mono.just(ResponseEntity.ok().<Void>build());
        }
    }
}