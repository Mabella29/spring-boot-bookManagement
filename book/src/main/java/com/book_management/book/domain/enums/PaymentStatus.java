package com.book_management.book.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED;

    @JsonCreator
    public static PaymentStatus from(String value) {
        return PaymentStatus.valueOf(value.toUpperCase());
    }
}
