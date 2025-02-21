package com.warehousemanagement.service.implementation;

import com.warehousemanagement.model.Order;
import com.warehousemanagement.repository.OrderRepository;
import com.warehousemanagement.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> getSortedOrders() {
        return orderRepository.findAll().stream()
                .filter(order -> order.getSubmittedDate() != null)
                .sorted(Comparator.comparing(Order::getSubmittedDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Order findOrder(Long orderNumber) {
        return orderRepository.findByOrderNumberIs(orderNumber);
    }

    @Override
    public void createOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void updateOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Order order) {
        orderRepository.delete(order);
    }

    @Override
    public List<Order> filterOrders(String status) {
        return orderRepository.findByStatusOrderBySubmittedDateDesc(status);
    }

    @Override
    public List<Order> filterClientOrder(List<Order> clientOrders, String status) {
        return clientOrders.stream()
                .filter(order -> order.getStatus().equals(status))
                .sorted(Comparator.comparing(Order::getSubmittedDate))
                .toList();
    }
}
