package com.warehousemanagement.service;

import com.warehousemanagement.model.Truck;

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
