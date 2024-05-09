package com.warehousemanagement.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Data
@Entity
@Table(name = "order_items_quantities")
public class OrderItemQuantity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_number")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public OrderItemQuantity() {
    }

    public OrderItemQuantity(Long quantity, Order order, Item item) {
        this.quantity = quantity;
        this.order = order;
        this.item = item;
    }
}
