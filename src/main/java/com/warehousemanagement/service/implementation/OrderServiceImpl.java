package com.warehousemanagement.service.implementation;

import com.warehousemanagement.model.Order;
import com.warehousemanagement.repository.OrderRepository;
import com.warehousemanagement.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;

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
