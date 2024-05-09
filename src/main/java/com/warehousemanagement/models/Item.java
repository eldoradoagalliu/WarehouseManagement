package com.warehousemanagement.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Size(min = 3, max = 30)
    private String name;

    @NotNull
    @Min(1)
    private Long quantity;

    @NotNull
    @Min(1)
    private Double unitPrice;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItemQuantity> orderItemsQuantities;
}
