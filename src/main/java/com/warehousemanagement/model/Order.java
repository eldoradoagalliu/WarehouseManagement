package com.warehousemanagement.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.warehousemanagement.constant.Constants.ORDER;
import static com.warehousemanagement.constant.Constants.ORDERS;
import static com.warehousemanagement.model.enums.OrderStatus.FULFILLED;
import static com.warehousemanagement.model.enums.OrderStatus.UNDER_DELIVERY;

@Entity
@Table(name = ORDERS)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = ORDER, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItemQuantity> itemQuantities;

    @Column(updatable = false)
    @OneToMany(mappedBy = "orderToDeliver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Truck> truckDelivers;

    public String formatStatus() {
        return status.replace("_", " ");
    }

    public boolean isDeliveryDone() {
        return deadline != null && LocalDate.now().isAfter(deadline);
    }

    public boolean isStatusUnderDeliveryOrFulfilled() {
        return status.equals(UNDER_DELIVERY.getStatus()) || status.equals(FULFILLED.getStatus());
    }
}
