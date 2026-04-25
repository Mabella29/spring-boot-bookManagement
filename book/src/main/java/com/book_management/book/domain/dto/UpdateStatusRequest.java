package com.book_management.book.domain.dto;

import com.book_management.book.domain.enums.OrderStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {
    private OrderStatus status;
}
