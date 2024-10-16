package edu.kdmk;

import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.VehicleManager;
import edu.kdmk.model.Client;
import edu.kdmk.model.vehicle.Car;
import edu.kdmk.model.vehicle.Motorcycle;
import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.EntityRepository;
import edu.kdmk.repositories.implemntations.ClientRepository;
import edu.kdmk.repositories.implemntations.VehicleRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class VehicleRepositoryTest {

    EntityRepository<Vehicle> vehicleRepository;
    VehicleManager vehicleManager;
    EntityManagerFactory emf;

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

        emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        vehicleRepository = new VehicleRepository();
        vehicleManager = new VehicleManager(emf, vehicleRepository);
    }

    @AfterEach
    public void tearDown() {
        emf.close();
    }

    @Test
    public void addVehicleTest() {
        vehicleManager.addVehicle(vehicle1);
        vehicleManager.addVehicle(vehicle2);
        assertEquals(vehicle1, vehicleManager.getVehicleById(vehicle1.getId()));
        assertEquals(vehicle2, vehicleManager.getVehicleById(vehicle2.getId()));
    }

    @Test
    public void removeVehicleTest() {
        vehicleManager.addVehicle(vehicle1);
        vehicleManager.addVehicle(vehicle2);
        assertTrue(vehicleManager.removeVehicle(vehicle1));
        assertTrue(vehicleManager.removeVehicle(vehicle2));
        assertNull(vehicleManager.getVehicleById(vehicle1.getId()));
        assertNull(vehicleManager.getVehicleById(vehicle2.getId()));
    }

    @Test
    public void updateVehicleTest() {
        vehicleManager.addVehicle(vehicle1);
        vehicle1.setBrand("Ford");
        vehicleManager.updateVehicle(vehicle1);
        Vehicle vehicle = vehicleManager.getVehicleById(vehicle1.getId());
        assertEquals(vehicle.getBrand(), "Ford");
    }

    @Test
    public void getByIdTest() {
        vehicleManager.addVehicle(vehicle1);
        Vehicle vehicle = vehicleManager.getVehicleById(vehicle1.getId());
        assertEquals(vehicle, vehicle1);
    }

}

