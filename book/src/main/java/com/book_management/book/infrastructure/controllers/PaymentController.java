package com.book_management.book.infrastructure.controllers;

import com.book_management.book.application.interfaces.OrderService;
import com.book_management.book.domain.dto.ApiResponse;
import com.book_management.book.domain.dto.PaymentInitRequest;
import com.book_management.book.domain.dto.PaymentInitResponse;
import com.book_management.book.infrastructure.services.PaystackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaystackService paystackService;
    private final OrderService orderService;

    @PostMapping("/initialize")
    public Mono<ResponseEntity<ApiResponse<PaymentInitResponse>>> initializePayment(
            @AuthenticationPrincipal Mono<String> userPrincipal,
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody PaymentInitRequest request
    ) {
        return userPrincipal.flatMap(userId ->
                orderService.getOrderById(request.getOrderId())
                        .flatMap(order -> {
                            String reference = "ORDER-" + request.getOrderId() + "-" + System.currentTimeMillis();

                            long amountInPesewas = order.getTotalPrice()
                                    .multiply(BigDecimal.valueOf(100))
                                    .longValue();

                            return paystackService.initializePayment(
                                    email,
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
                        })
        );
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

        return paystackService.handleWebhook(payload, orderService)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .doOnError(err -> log.error("Failed to process webhook", err))
                .onErrorReturn(ResponseEntity.ok().<Void>build());
    }
}