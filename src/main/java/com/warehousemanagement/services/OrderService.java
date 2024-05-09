package com.warehousemanagement.services;

import com.warehousemanagement.models.Order;

import java.util.List;

public interface OrderService {

    List<Order> getSortedOrders();

    Order findOrder(Long orderNumber);

    void createOrder(Order order);

    void updateOrder(Order order);

    void deleteOrder(Order order);

    List<Order> filterOrders(String status);

    List<Order> filterClientOrder(List<Order> clientOrders, String status);
}
