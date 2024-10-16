/*
package edu.kdmk;

import edu.kdmk.model.vehicle.Car;
import edu.kdmk.model.vehicle.Motorcycle;
import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.JPAUtil;
import edu.kdmk.repositories.implemntations.VehicleRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class VehicleRepositoryTest {

    Vehicle vehicle1;
    Vehicle vehicle2;

    @BeforeEach
    public void setUp() {
        vehicle1 = Car.builder()
                .licensePlate("dx123")
                .brand("Toyota")
                .model("Corolla")
                .year(2021)
                .price(100)
                .numberOfDoors(5)
                .numberOfSeats(5)
                .build();

        vehicle2 = Motorcycle.builder()
                .licensePlate("xd123")
                .brand("BMW")
                .model("Corolla")
                .year(2021)
                .price(200)
                .cylinderCapacity(1000)
                .power(125)
                .build();
    }

    @Test
    public void addVehicleTest() {
        VehicleRepository vehicleRepository = new VehicleRepository();
        vehicleRepository.add(vehicle1);
        vehicleRepository.add(vehicle2);
        Vehicle vehicleFromDb1 = vehicleRepository.getById(vehicle1.getId());
        Vehicle vehicleFromDb2 = vehicleRepository.getById(vehicle2.getId());

        assertEquals(vehicleFromDb1, vehicle1);
        assertEquals(vehicleFromDb2, vehicle2);

    }

    @Test
    public void removeVehicleTest() {
        VehicleRepository vehicleRepository = new VehicleRepository();
        vehicleRepository.add(vehicle1);
        vehicleRepository.add(vehicle2);
        assertTrue(vehicleRepository.remove(vehicle1));
        assertTrue(vehicleRepository.remove(vehicle2));
        assertNull(vehicleRepository.getById(vehicle1.getId()));
        assertNull(vehicleRepository.getById(vehicle2.getId()));
    }

    @Test
    public void updateVehicleTest() {
        VehicleRepository vehicleRepository = new VehicleRepository();
        vehicleRepository.add(vehicle1);
        vehicle1.setBrand("Ford");
        vehicleRepository.update(vehicle1);
        Vehicle vehicle = vehicleRepository.getById(vehicle1.getId());
        assertEquals(vehicle.getBrand(), "Ford");
    }

    @Test
    public void getByIdTest() {
        VehicleRepository vehicleRepository = new VehicleRepository();
        vehicleRepository.add(vehicle1);
        Vehicle vehicle = vehicleRepository.getById(vehicle1.getId());
        assertEquals(vehicle, vehicle1);
    }

}
*/
