package com.warehousemanagement.services;

import com.warehousemanagement.models.Truck;

import java.util.List;

public interface TruckService {

    List<Truck> getAllTrucks();

    Truck findTruck(Long id);

    Truck findTruck(String chassisNumber);

    Truck findTruckByPlate(String licensePlate);

    void addTruck(Truck truck);

    void updateTruck(Truck truck);

    void deleteTruck(Truck truck);
}
