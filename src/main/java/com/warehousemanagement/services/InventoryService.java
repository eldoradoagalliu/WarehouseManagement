package com.warehousemanagement.services;

import com.warehousemanagement.models.Item;
import com.warehousemanagement.models.Order;
import com.warehousemanagement.models.OrderItemQuantity;

import java.util.List;
import java.util.Optional;

public interface InventoryService {

    List<Item> getAllItems();

    Item getItem(Long id);

    void addItem(Item item);

    void updateItem(Item item);

    void deleteItem(Item item);

    void addRequestedItemQuantity(OrderItemQuantity orderItemQuantity);

    void updateItemQuantity(OrderItemQuantity orderItemQuantity);

    List<OrderItemQuantity> getOrderItems(Order order);

    Optional<OrderItemQuantity> getOrderItem(Order order, Item item);

    void returnItemsInInventory(Order order);

    void removeItemFromOrder(Order order, Item item);
}
