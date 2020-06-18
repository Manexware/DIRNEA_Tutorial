package org.dirnea.lab03.database;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dirnea.lab03.model.Employee;
import org.dirnea.lab03.model.Order;
import org.dirnea.lab03.model.Status;
import org.dirnea.lab03.repository.EmployeeRepository;
import org.dirnea.lab03.repository.OrderRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@AllArgsConstructor
public class LoadDatabase implements ApplicationRunner{

    private EmployeeRepository employeeRepository;
    private OrderRepository orderRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Populating database with sample data...");
        log.info("Preloading " + employeeRepository.save(new Employee("Manuel Vega", "Instructor")));
        log.info("Preloading " + employeeRepository.save(new Employee("Juan Pueblo", "Student")));
        log.info("Preloading " + employeeRepository.save(new Employee("Peter Love", "Student")));
        log.info("Preloading " + employeeRepository.save(new Employee("Armando Meza", "Student")));

        orderRepository.save(new Order("Dell 324", Status.CANCELLED));
        orderRepository.save(new Order("MacBook Pro", Status.COMPLETED));
        orderRepository.save(new Order("iPhone", Status.IN_PROGRESS));

        orderRepository.findAll().forEach(order -> {
            log.info("Preloaded " + order.getId() + " -- " + order.getDescription() + " - Status: " + order.getStatus().toString());
        });

    }

}
