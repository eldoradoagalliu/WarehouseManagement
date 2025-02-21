package com.warehousemanagement.service.implementation;

import com.warehousemanagement.model.Truck;
import com.warehousemanagement.repository.TruckRepository;
import com.warehousemanagement.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TruckServiceImpl implements TruckService {

    private final TruckRepository truckRepository;

    @Override
    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }

    @Override
    public Truck findTruck(Long id) {
        return truckRepository.findByIdIs(id);
    }

    @Override
    public Truck findTruck(String chassisNumber) {
        return truckRepository.findByChassisNumber(chassisNumber);
    }

    @Override
    public Truck findTruckByPlate(String licensePlate) {
        return truckRepository.findByLicensePlate(licensePlate);
    }

    @Override
    public void addTruck(Truck truck) {
        truckRepository.save(truck);
    }

    @Override
    public void updateTruck(Truck truck) {
        truckRepository.save(truck);
    }

    @Override
    public void deleteTruck(Truck truck) {
        truckRepository.delete(truck);
    }
}
