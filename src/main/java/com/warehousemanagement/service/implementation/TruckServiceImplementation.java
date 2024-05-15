package com.warehousemanagement.service.implementation;

import com.warehousemanagement.model.Truck;
import com.warehousemanagement.repository.TruckRepository;
import com.warehousemanagement.service.TruckService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruckServiceImplementation implements TruckService {

    private final TruckRepository truckRepo;

    public TruckServiceImplementation(TruckRepository truckRepo) {
        this.truckRepo = truckRepo;
    }

    @Override
    public List<Truck> getAllTrucks() {
        return truckRepo.findAll();
    }

    @Override
    public Truck findTruck(Long id) {
        return truckRepo.findByIdIs(id);
    }

    @Override
    public Truck findTruck(String chassisNumber) {
        return truckRepo.findByChassisNumber(chassisNumber);
    }

    @Override
    public Truck findTruckByPlate(String licensePlate) {
        return truckRepo.findByLicensePlate(licensePlate);
    }

    @Override
    public void addTruck(Truck truck) {
        truckRepo.save(truck);
    }

    @Override
    public void updateTruck(Truck truck) {
        truckRepo.save(truck);
    }

    @Override
    public void deleteTruck(Truck truck) {
        truckRepo.delete(truck);
    }
}
