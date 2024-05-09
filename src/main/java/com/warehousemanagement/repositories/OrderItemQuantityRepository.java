package com.warehousemanagement.repositories;

import com.warehousemanagement.models.Item;
import com.warehousemanagement.models.Order;
import com.warehousemanagement.models.OrderItemQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemQuantityRepository extends JpaRepository<OrderItemQuantity, Long> {
    List<OrderItemQuantity> findByOrder(Order order);

    Optional<OrderItemQuantity> findByOrderAndItem(Order order, Item item);
}
