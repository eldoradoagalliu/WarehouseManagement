package com.warehousemanagement.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.warehousemanagement.models.constants.Constants.DEFAULT_VALUE;
import static com.warehousemanagement.models.enums.OrderStatusEnum.FULFILLED;
import static com.warehousemanagement.models.enums.OrderStatusEnum.UNDER_DELIVERY;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderNumber;

    private String status;

    private Date submittedDate;

    private LocalDate deadline;

    private String declineReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User client;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItemQuantity> itemQuantities;

    @Column(updatable = false)
    @OneToMany(mappedBy = "orderToDeliver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Truck> truckDelivers;

    public Order() {
    }

    public Order(String status, User client) {
        this.status = status;
        this.client = client;
        this.submittedDate = new Date(DEFAULT_VALUE);
    }

    public String formatStatus() {
        return status.replace("_", " ");
    }

    public boolean isDeliveryDone() {
        if (deadline == null) {
            return false;
        } else {
            return LocalDate.now().isAfter(deadline);
        }
    }

    public boolean isStatusUnderDeliveryOrFulfilled() {
        return status.equals(UNDER_DELIVERY.getStatus()) || status.equals(FULFILLED.getStatus());
    }
}
