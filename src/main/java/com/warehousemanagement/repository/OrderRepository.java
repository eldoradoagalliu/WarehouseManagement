package com.warehousemanagement.repository;

import com.warehousemanagement.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderNumberIs(Long orderNumber);

    List<Order> findByStatusOrderBySubmittedDateDesc(String status);
}
