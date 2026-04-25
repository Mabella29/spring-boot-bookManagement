package com.book_management.book.application.usecases;

import com.book_management.book.application.interfaces.OrderService;
import com.book_management.book.application.repository.OrderItemRepository;
import com.book_management.book.application.repository.OrderRepository;
import com.book_management.book.domain.dto.Order;
import com.book_management.book.domain.dto.OrderItemResponse;
import com.book_management.book.domain.dto.OrderResponse;
import com.book_management.book.domain.enums.OrderStatus;
import com.book_management.book.domain.exceptions.OrderNotFoundException;
import com.book_management.book.domain.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    @Override
    public Mono<OrderResponse> createOrder(UUID userId){

        //create an order using the user id
        return orderRepository.createOrder(userId)
                //now we have the order, but it contains metadata
                //we use flatmap because we are returning a mono
                //collect list returns a mono
                .flatMap(this::buildOrderResponse)
                .doOnSuccess(res -> log.info("successfully created an order {}", res))
                .doOnError(err -> log.error("failed to create an order", err));

    }

    @Override
    public Mono<OrderResponse> getOrderById(UUID orderId){
        log.info("fetching order by id {}",orderId);

        return orderRepository.getOrderById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException("order cannot be found")))
                //if found
                //order contains metadata so we fetch the items in the order
                .flatMap(this::buildOrderResponse).doOnSuccess(res -> log.info("order fetched successfully {}",res))
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

    private Mono<OrderResponse> buildOrderResponse(Order order){
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
                                .createdAt(order.getCreatedAt())
                                .updatedAt(order.getUpdatedAt())
                                .build()
                );

    }
}
