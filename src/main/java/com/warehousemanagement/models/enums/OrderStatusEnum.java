package com.warehousemanagement.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatusEnum {
    CREATED("CREATED"),
    AWAITING_APPROVAL("AWAITING_APPROVAL"),
    APPROVED("APPROVED"),
    DECLINED("DECLINED"),
    UNDER_DELIVERY("UNDER_DELIVERY"),
    FULFILLED("FULFILLED"),
    CANCELLED("CANCELLED");

    private final String status;
}
