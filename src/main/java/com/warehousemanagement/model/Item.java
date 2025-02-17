package com.warehousemanagement.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.warehousemanagement.constant.Constants.ITEM;
import static com.warehousemanagement.constant.Constants.ITEMS;

@Entity
@Table(name = ITEMS)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = ITEM, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItemQuantity> orderItemsQuantities;
}
