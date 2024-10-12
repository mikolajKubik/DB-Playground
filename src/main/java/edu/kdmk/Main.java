package edu.kdmk;

import edu.kdmk.model.Client;
import edu.kdmk.repositories.ClientRepository;
import edu.kdmk.repositories.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.function.Consumer;


public class Main {
    public static void main(String[] args) {
//        EntityManagerFactory entityManagerFactory = null;
//        EntityManager entityManager = null;
//
//        try {
//            entityManagerFactory = Persistence.createEntityManagerFactory("my-persistence-unit");
//            entityManager = entityManagerFactory.createEntityManager();
//
//
//            var client = Client.builder()
//                    .name("John Doeeee")
//                    .phoneNumber("123-456-789")
//                    .build();
//
//            ClientRepository clientRepository = new ClientRepository(entityManager);
//            clientRepository.add(client);
//
//        } finally {
//            if (entityManager != null && entityManager.isOpen()) {
//                entityManager.close();
//            }
//            if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
//                entityManagerFactory.close();
//            }
//        }

        JPAUtil.inTransaction(em -> {
            // Your database operation here
            var client = Client.builder()
                    .name("John Doeeee")
                    .phoneNumber("123-456-789")
                    .build();
            em.persist(client);
        });




    }


}