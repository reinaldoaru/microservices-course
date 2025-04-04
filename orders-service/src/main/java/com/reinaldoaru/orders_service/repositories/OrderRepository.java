package com.reinaldoaru.orders_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reinaldoaru.orders_service.model.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
