package org.dirnea.lab02.database;

import lombok.extern.slf4j.Slf4j;
import org.dirnea.lab02.model.Employee;
import org.dirnea.lab02.repository.Employeerepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(Employeerepository repository){
        return args -> {
            log.info("Preloading " + repository.save(new Employee("Manuel Vega", "Instructor")));
            log.info("Preloading " + repository.save(new Employee("Juan Pueblo", "Student")));
            log.info("Preloading " + repository.save(new Employee("Peter Love", "Student")));
        };
    }
}
