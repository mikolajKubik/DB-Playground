package edu.kdmk;

import edu.kdmk.model.Address;
import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.vehicle.Motorcycle;
import edu.kdmk.repositories.AddressRepository;
import edu.kdmk.repositories.ClientRepository;
import edu.kdmk.repositories.VehicleRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Configuration
public class Main {
    public static void main(String[] args) {

//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");


        AddressRepository addressRepository = context.getBean(AddressRepository.class);
        ClientRepository clientRepository = context.getBean(ClientRepository.class);

        //VehicleRepository vehicleRepository = context.getBean(VehicleRepository.class);
        //RentRepository rentRepository = context.getBean(RentRepository.class);

        var address = Address.builder()
                .street("Main Streettyttttttttt")
                .city("New York")
                .state("NY")
                .zipCode("10044")
                .build();

        var client = Client.builder()
                .name("John Doeeee")
                .phoneNumber("123-456-789")
                .address(address)
                .build();

//        var vehicle = Motorcycle.builder()
//                .licensePlate("12345")
//                .brand("Yamaha")
//                .model("R1")
//                .year(2021)
//                .price(100)
//                .cylinderCapacity(1000)
//                .power(200)
//                .build();
//
//        var rent = Rent.builder()
//                .days(5)
//                .price(500)
//                .client(client)
//                .vehicle(vehicle)
//                .build();

        addressRepository.add(address);
        clientRepository.add(client);

        //vehicleRepository.add(vehicle);
        //rentRepository.add(rent);

    }
}