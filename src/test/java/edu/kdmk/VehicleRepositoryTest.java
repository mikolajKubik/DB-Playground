package edu.kdmk;

import edu.kdmk.managers.VehicleManager;
import edu.kdmk.model.vehicle.Car;
import edu.kdmk.model.vehicle.Motorcycle;
import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.EntityRepository;
import edu.kdmk.repositories.implemntations.VehicleRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
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
                .pricePerDay(100)
                .numberOfDoors(5)
                .numberOfSeats(5)
                .build();

        vehicle2 = Motorcycle.builder()
                .licensePlate("xd123")
                .brand("BMW")
                .model("Corolla")
                .year(2021)
                .pricePerDay(200)
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
        Vehicle addedVehicle1 = vehicleManager.addVehicle(vehicle1);
        Vehicle addedVehicle2 = vehicleManager.addVehicle(vehicle2);
        Vehicle vehicleFromDb1 = vehicleManager.getVehicleById(addedVehicle1.getId());
        Vehicle vehicleFromDb2 = vehicleManager.getVehicleById(addedVehicle2.getId());
        assertEquals(addedVehicle1, vehicleFromDb1);
        assertEquals(addedVehicle2, vehicleFromDb2);

    }

    @Test
    public void removeVehicleTest() {
        Vehicle addedVehicle1 = vehicleManager.addVehicle(vehicle1);
        Vehicle addedVehicle2 = vehicleManager.addVehicle(vehicle2);
        assertTrue(vehicleManager.removeVehicle(addedVehicle1.getId()));
        assertTrue(vehicleManager.removeVehicle(addedVehicle2.getId()));
        assertNull(vehicleManager.getVehicleById(addedVehicle1.getId()));
        assertNull(vehicleManager.getVehicleById(addedVehicle2.getId()));
    }

    @Test
    public void removeVehicleNullTest() {
        assertThrows(IllegalArgumentException.class, () -> vehicleManager.removeVehicle(0));
    }

    @Test
    public void updateVehicleTest() {
        Vehicle addedVehicle = vehicleManager.addVehicle(vehicle1);
        vehicle1.setBrand("Ford");
        Vehicle updatedVehicle = vehicleManager.updateVehicle(vehicle1);
        assertEquals(updatedVehicle.getBrand(), "Ford");
    }

    @Test
    public void updateVehicleNullTest() {
        Vehicle vehicle = null;
        assertThrows(IllegalArgumentException.class, () -> vehicleManager.updateVehicle(vehicle));
    }

    @Test
    public void getByIdTest() {
        Vehicle addedVehicle = vehicleManager.addVehicle(vehicle1);
        Vehicle vehicleFromDb = vehicleManager.getVehicleById(addedVehicle.getId());
        assertEquals(addedVehicle, vehicleFromDb);
    }
}

