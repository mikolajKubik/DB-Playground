package edu.kdmk;

import edu.kdmk.configuration.AppConfig;
import edu.kdmk.model.Address;
import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.vehicle.Motorcycle;
import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.ClientRepository;
import edu.kdmk.repositories.VehicleEntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

@Configuration
@ComponentScan(basePackages = "edu.kdmk")
public class Main {
    public static void main(String[] args) {

     //   AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class);

       //    GenericApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
      //  context.getBean("AppConfig", AppConfig.class);

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
////        GenericApplicationContext context = new GenericApplicationContext();
//
//        // Register the AppConfig class (contains the @Bean definitions)
//        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(context);
//        reader.register(AppConfig.class);
//
//        // Refresh the context to apply the bean definitions
//        context.refresh();

        /*var address = Address.builder()
                .street("Main Street")
                .city("New York")
                .state("NY")
                .zipCode("10044")
                .build();

        var client = Client.builder()
                .name("John Doe")
                .phoneNumber("123-456-789")
                .address(address)
                .build();

        ClientRepository repo = new ClientRepository();
        repo.add(client);*/

        var vehicle = Motorcycle.builder()
                .licensePlate("12345")
                .brand("Yamaha")
                .model("R1")
                .year(2021)
                .price(100)
                .cylinderCapacity(1000)
                .power(200)
                .build();


        VehicleEntityRepository vehicleRepository = context.getBean(VehicleEntityRepository.class);
        //VehicleEntityRepository vehicleEntityRepository = new VehicleEntityRepository();


        System.out.println(vehicleRepository.remove(vehicle));


        //Uzycie XML
        /*EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            var address = Address.builder()
                    .street("Main Street")
                    .city("New York")
                    .state("NY")
                    .zipCode("10044")
                    .build();

            var client = Client.builder()
                    .name("John Doe")
                    .phoneNumber("123-456-789")
                    .address(address)
                    .build();

            var vehicle = Motorcycle.builder()
                            .licensePlate("12345")
                            .brand("Yamaha")
                            .model("R1")
                            .year(2021)
                            .price(100)
                            .cylinderCapacity(1000)
                            .power(200)
                            .build();

            var rent = Rent.builder()
                    .days(5)
                    .price(500)
                    .client(client)
                    .vehicle(vehicle)
                    .build();

            em.persist(address);
            em.persist(client);
            em.persist(vehicle);
            em.persist(rent);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }*/
    }
}