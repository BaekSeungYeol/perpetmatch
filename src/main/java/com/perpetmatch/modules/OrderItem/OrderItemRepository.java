package com.perpetmatch.modules.OrderItem;

import com.perpetmatch.Domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}