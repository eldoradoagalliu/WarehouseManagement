package com.warehousemanagement.services.implementation;

import com.warehousemanagement.models.Order;
import com.warehousemanagement.repositories.OrderRepository;
import com.warehousemanagement.services.OrderService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class OrderServiceImplementation implements OrderService {

    private final OrderRepository orderRepo;

    public OrderServiceImplementation(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    public List<Order> getSortedOrders() {
        return orderRepo.findAll().stream()
                .sorted(Comparator.comparing(Order::getSubmittedDate).reversed())
                .toList();
    }

    @Override
    public Order findOrder(Long orderNumber) {
        return orderRepo.findByOrderNumberIs(orderNumber);
    }

    @Override
    public void createOrder(Order order) {
        orderRepo.save(order);
    }

    @Override
    public void updateOrder(Order order) {
        orderRepo.save(order);
    }

    @Override
    public void deleteOrder(Order order) {
        orderRepo.delete(order);
    }

    @Override
    public List<Order> filterOrders(String status) {
        return orderRepo.findByStatusOrderBySubmittedDateDesc(status);
    }

    @Override
    public List<Order> filterClientOrder(List<Order> clientOrders, String status) {
        return clientOrders.stream()
                .filter(order -> order.getStatus().equals(status))
                .sorted(Comparator.comparing(Order::getSubmittedDate))
                .toList();
    }
}
