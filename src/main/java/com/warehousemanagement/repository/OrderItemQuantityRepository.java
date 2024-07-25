package com.warehousemanagement.repository;

import com.warehousemanagement.model.Item;
import com.warehousemanagement.model.Order;
import com.warehousemanagement.model.OrderItemQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemQuantityRepository extends JpaRepository<OrderItemQuantity, Long> {

    List<OrderItemQuantity> findByOrder(Order order);

    Optional<OrderItemQuantity> findByOrderAndItem(Order order, Item item);
}
