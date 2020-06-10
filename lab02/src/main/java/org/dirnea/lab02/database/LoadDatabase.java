package org.dirnea.lab02.database;

import lombok.extern.slf4j.Slf4j;
import org.dirnea.lab02.model.Employee;
import org.dirnea.lab02.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository repository){
        return args -> {
            log.info("Preloading " + repository.save(new Employee("Manuel Vega", "Instructor")));
            log.info("Preloading " + repository.save(new Employee("Juan Pueblo", "Student")));
            log.info("Preloading " + repository.save(new Employee("Peter Love", "Student")));
            log.info("Preloading " + repository.save(new Employee("Armando Meza", "Student")));
        };
    }
}
