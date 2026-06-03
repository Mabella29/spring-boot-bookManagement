package com.book_management.book.application.usecases;

import com.book_management.book.application.interfaces.OrderService;
import com.book_management.book.application.repository.OrderItemRepository;
import com.book_management.book.application.repository.OrderRepository;
import com.book_management.book.domain.dto.*;
import com.book_management.book.domain.enums.OrderStatus;
import com.book_management.book.domain.enums.PaymentMethod;
import com.book_management.book.domain.enums.PaymentStatus;
import com.book_management.book.domain.exceptions.OrderNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    @Override
    public Mono<OrderResponse> createOrder(UUID userId, DeliveryAddress deliveryAddress){

        String addressJson;
        try {
            addressJson = OBJECT_MAPPER
                    .writeValueAsString(deliveryAddress);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to serialize delivery address"));
        }

        //create an order using the user id
        return orderRepository.createOrder(userId, addressJson)
                //now we have the order, but it contains metadata
                //we use flatmap because we are returning a mono
                //collect list returns a mono
                .flatMap(this::buildOrderResponse)
                .onErrorMap(ex -> {
                    String msg = ex.getMessage();
                    if (msg != null && msg.contains("Insufficient stock for:")) {
                        int index = msg.indexOf("Insufficient stock for:");
                        return new RuntimeException(msg.substring(index));
                    }
                    return ex;
                })
                .doOnSuccess(res -> log.info("successfully created an order {}", res))
                .doOnError(err -> log.error("failed to create an order", err));

    }

    @Override
    public Mono<PageResponse<OrderResponse>> getAllOrders(int page, int size){
        log.info("Fetching all orders");

        int offset = page * size;

        return Mono.zip(
                orderRepository.getAllOrders(size, offset)
                        .flatMap(this::buildOrderResponse)
                        .collectList(),
                orderRepository.getOrderCount()
        ).map(tuple ->{
            var content = tuple.getT1();
            var totalElements = tuple.getT2();
            var totalPages = (int) Math.ceil((double) totalElements / size);

            return PageResponse.<OrderResponse>builder()
                    .content(content)
                    .page(page)
                    .size(size)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .build();
        });

    }

    @Override
    public Mono<OrderResponse> getOrderById(UUID orderId){
        log.info("fetching order by id {}",orderId);

        return orderRepository.getOrderById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException("order cannot be found")))
                //if found
                //order contains metadata so we fetch the items in the order
                .flatMap(this::buildOrderResponse)
                .doOnSuccess(res -> log.info("order fetched successfully {}",res))
                .doOnError(err -> log.error("failed to fetch order", err));
    }

    @Override
    public Flux<OrderResponse> getOrdersByUserId(UUID userId){
        log.info("fetching all orders for user {}", userId);

        return orderRepository.getAllOrdersByUserId(userId)
                .flatMap(this::buildOrderResponse)
                .doOnComplete(() -> log.info("successfully fetched all orders for this user {}", userId))
                .doOnError(err -> log.error("failed to get all orders for this user",err));
    }

    @Override
    public Mono<OrderResponse> updateOrderStatus(UUID orderId, OrderStatus status){
        log.info("updating status for order {}", orderId);

        return orderRepository.updateOrderStatus(orderId, status.name())
                //check if order exists
                .switchIfEmpty(Mono.error(new OrderNotFoundException("order not found")))
                .flatMap(this::buildOrderResponse)
                .doOnSuccess(res -> log.info("successfully updated order status {}",res))
                .doOnError(error -> log.error("failed to update order status", error));


    }

    @Override
    public Mono<OrderResponse> updatePayment(
            UUID orderId, String paymentStatus, String paymentMethod,
            String paymentReference, BigDecimal amountPaid) {
        log.info("Updating payment for order {}", orderId);

        return orderRepository.updatePayment(orderId, paymentStatus, paymentMethod, paymentReference, amountPaid)
                .switchIfEmpty(Mono.error(new OrderNotFoundException("Order not found: " + orderId)))
                .flatMap(this::buildOrderResponse)
                .doOnSuccess(res -> log.info("Payment updated successfully for order {}", orderId))
                .doOnError(err -> log.error("Failed to update payment", err));
    }

    private Mono<OrderResponse> buildOrderResponse(Order order){

        DeliveryAddress address = null;

        if (order.getDeliveryAddress() != null) {
            try {
                address = OBJECT_MAPPER
                        .readValue(order.getDeliveryAddress(), DeliveryAddress.class);
            } catch (Exception e) {
                log.warn("Failed to parse delivery address for order {}", order.getOrderId());
            }
        }


        final DeliveryAddress finalAddress = address;

        return orderItemRepository.getOrderItems(order.getOrderId())
                //convert the items into the item response builder
                .map(OrderItemResponse::from)
                .collectList()
                // after collecting all the items together, we use the
                //items to build an order res to the client
                .map(items ->
                        OrderResponse.builder()
                                .orderId(order.getOrderId())
                                .userId(order.getUserId())
                                .items(items)
                                .totalPrice(order.getTotalPrice())
                                .status(order.getStatus())
                                .paymentStatus(order.getPaymentStatus() != null
                                        ? PaymentStatus.valueOf(order.getPaymentStatus()) : null)
                                .paymentMethod(order.getPaymentMethod() != null
                                        ? PaymentMethod.valueOf(order.getPaymentMethod()) : null)
                                .paymentReference(order.getPaymentReference())
                                .amountPaid(order.getAmountPaid())
                                .paidAt(order.getPaidAt())
                                .deliveryAddress(finalAddress)
                                .createdAt(order.getCreatedAt())
                                .updatedAt(order.getUpdatedAt())
                                .build()
                );

    }
}
