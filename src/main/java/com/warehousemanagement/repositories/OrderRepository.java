package com.warehousemanagement.repositories;

import com.warehousemanagement.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderNumberIs(Long orderNumber);

    List<Order> findByStatusOrderBySubmittedDateDesc(String status);
}
