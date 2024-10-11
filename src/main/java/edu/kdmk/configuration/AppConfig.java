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
//@ComponentScan({"edu.kdmk.repositories", "edu.kdmk.services"})
public class AppConfig implements AutoCloseable {

    private final EntityManagerFactory entityManagerFactory =  Persistence.createEntityManagerFactory("my-persistence-unit");

    @Bean
    public EntityManager entityManager() {
        return entityManagerFactory.createEntityManager();
    }

    @Override
    public void close() throws Exception {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
