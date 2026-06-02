package com.book_management.book.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentMethod {
    CARD,
    BANK_TRANSFER,
    MOBILE_MONEY;

    @JsonCreator
    public static PaymentMethod from(String value) {
        return PaymentMethod.valueOf(value.toUpperCase());
    }
}
