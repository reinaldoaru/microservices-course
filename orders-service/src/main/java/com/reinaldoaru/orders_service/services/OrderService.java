package com.reinaldoaru.orders_service.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.reinaldoaru.orders_service.model.dtos.BaseResponse;
import com.reinaldoaru.orders_service.model.dtos.OrderItemRequest;
import com.reinaldoaru.orders_service.model.dtos.OrderItemResponse;
import com.reinaldoaru.orders_service.model.dtos.OrderRequest;
import com.reinaldoaru.orders_service.model.dtos.OrderResponse;
import com.reinaldoaru.orders_service.model.entities.Order;
import com.reinaldoaru.orders_service.model.entities.OrderItem;
import com.reinaldoaru.orders_service.repositories.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        
        // Check inventory

        BaseResponse result = this.webClientBuilder.build()
            .post()
            .uri("http://localhost:8080/api/inventory/in-stock")
            .bodyValue(orderRequest.getOrderItems())
            .retrieve()
            .bodyToMono(BaseResponse.class)
            .block();

        if (result != null && !result.hasErrors()) {
            Order order = new Order();

            order.setOrderNumber(UUID.randomUUID().toString());
            order.setOrderItems(orderRequest.getOrderItems().stream()
                    .map(orderItemRequest -> mapOrderItemRequestToOrderItem(orderItemRequest, order))
                    .toList());

            this.orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Some of the products are not in stock.");
        }
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = this.orderRepository.findAll();

        return orders.stream().map(this::mapToOrderResponse).toList();
    }

    private OrderResponse mapToOrderResponse (Order order) {
        return new OrderResponse(order.getId(), order.getOrderNumber(), order.getOrderItems().stream().map(this::mapToOrderItemResponse).toList());
    }
    
    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        return new OrderItemResponse(orderItem.getId(), orderItem.getSku(), orderItem.getPrice(), orderItem.getQuantity());
    }

    private OrderItem mapOrderItemRequestToOrderItem(OrderItemRequest orderItemRequest, Order order) {
        return OrderItem.builder()
            .id(orderItemRequest.getId())
            .sku(orderItemRequest.getSku())
            .price(orderItemRequest.getPrice())
            .quantity(orderItemRequest.getQuantity())
            .order(order)
            .build();
    }

    

}
