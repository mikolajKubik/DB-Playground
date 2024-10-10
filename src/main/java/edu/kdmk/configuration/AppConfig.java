package edu.kdmk.configuration;

import edu.kdmk.model.vehicle.Car;
import edu.kdmk.model.vehicle.Motorcycle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("edu.kdmk.repositories")
public class AppConfig {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");

    @Bean
    public EntityManager entityManager() {
        return emf.createEntityManager();
    }
    
    @Bean
    public Motorcycle motorCycle() {
        return Motorcycle.builder()
                .licensePlate("12345")
                .brand("Yamaha")
                .model("R1")
                .year(2021)
                .price(100)
                .cylinderCapacity(1000)
                .power(200)
                .build();
    }
}
