package com.book_management.book.domain.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInitResponse {
    private String authorizationUrl;
    private String reference;
    private String orderId;
}