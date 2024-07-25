package com.warehousemanagement.repository;

import com.warehousemanagement.model.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {

    Truck findByIdIs(Long id);

    Truck findByChassisNumber(String chassisNumber);

    Truck findByLicensePlate(String licensePlate);
}
