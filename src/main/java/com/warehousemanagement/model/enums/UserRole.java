package com.warehousemanagement.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    CLIENT("CLIENT"),
    WAREHOUSE_MANAGER("WAREHOUSE_MANAGER"),
    SYSTEM_ADMIN("SYSTEM_ADMIN");

    private final String role;
}
