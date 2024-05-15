package com.warehousemanagement.repository;

import com.warehousemanagement.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Item, Long> {
    Item findByIdIs(Long id);
}
