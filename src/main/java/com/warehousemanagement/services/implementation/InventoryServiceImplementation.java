package com.warehousemanagement.services.implementation;

import com.warehousemanagement.models.Item;
import com.warehousemanagement.models.Order;
import com.warehousemanagement.models.OrderItemQuantity;
import com.warehousemanagement.repositories.InventoryRepository;
import com.warehousemanagement.repositories.OrderItemQuantityRepository;
import com.warehousemanagement.services.InventoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InventoryServiceImplementation implements InventoryService {

    private final InventoryRepository inventoryRepo;
    private final OrderItemQuantityRepository itemQuantityRepo;

    public InventoryServiceImplementation(InventoryRepository inventoryRepo, OrderItemQuantityRepository itemQuantityRepo) {
        this.inventoryRepo = inventoryRepo;
        this.itemQuantityRepo = itemQuantityRepo;
    }

    @Override
    public List<Item> getAllItems() {
        return inventoryRepo.findAll();
    }

    @Override
    public Item getItem(Long id) {
        return inventoryRepo.findByIdIs(id);
    }

    @Override
    public void addItem(Item item) {
        inventoryRepo.save(item);
    }

    @Override
    public void updateItem(Item item) {
        inventoryRepo.save(item);
    }

    @Override
    public void deleteItem(Item item) {
        inventoryRepo.delete(item);
    }

    @Override
    public void addRequestedItemQuantity(OrderItemQuantity orderItemQuantity) {
        itemQuantityRepo.save(orderItemQuantity);
    }

    @Override
    public void updateItemQuantity(OrderItemQuantity orderItemQuantity) {
        itemQuantityRepo.save(orderItemQuantity);
    }

    @Override
    public List<OrderItemQuantity> getOrderItems(Order order) {
        return itemQuantityRepo.findByOrder(order);
    }

    @Override
    public Optional<OrderItemQuantity> getOrderItem(Order order, Item item) {
        return itemQuantityRepo.findByOrderAndItem(order, item);
    }

    @Override
    public void returnItemsInInventory(Order order) {
        List<OrderItemQuantity> orderItems = getOrderItems(order);
        orderItems.forEach(orderItem -> removeItemFromOrder(orderItem.getOrder(), orderItem.getItem()));
    }

    @Override
    public void removeItemFromOrder(Order order, Item item) {
        Optional<OrderItemQuantity> foundOrderItem = getOrderItem(order, item);
        if(foundOrderItem.isPresent()) {
            OrderItemQuantity orderItem = foundOrderItem.get();
            if (Objects.equals(orderItem.getItem().getId(), item.getId())) {
                item.setQuantity(item.getQuantity() + orderItem.getQuantity());
                updateItem(item);
                removeOrderItemQuantity(orderItem);
            }
        }
    }

    public void removeOrderItemQuantity(OrderItemQuantity orderItemQuantity) {
        itemQuantityRepo.delete(orderItemQuantity);
    }
}
