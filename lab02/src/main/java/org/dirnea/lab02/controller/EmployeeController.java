package org.dirnea.lab02.controller;

import org.dirnea.lab02.model.Employee;
import org.dirnea.lab02.repository.EmployeeRepository;
import org.dirnea.lab02.util.EmployeeNotFoundException;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
public class EmployeeController {
    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    // Aggregate root
    // http://localohots:8080/employees
    @GetMapping("/employees")
    List<Employee> all() {
        List<Employee> employees = repository.findAll();
        for(final Employee employee : employees){
            Link link = linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel();
            employee.add(link);
        }

        return  employees ;//repository.findAll();
    }

    @PostMapping("/employees")
    Employee newEmployee(@RequestBody Employee employee) {
        return repository.save(employee);
    }

    // Single item
    // http://localohots:8080/employees/4
    @GetMapping("/employees/{id}")
    Employee one(@PathVariable Long id) {
        //{ "id": 1, "name":"Manuel Vega", "role":"Instructor"}
        // utilizando los mensaje de HTTP
        Optional<Employee> employee = repository.findById(id);

        Link link = linkTo(methodOn(EmployeeController.class).one(employee.get().getId())).withSelfRel();
        employee.get().add(link);
        link = linkTo(methodOn(EmployeeController.class).all()).withRel("todos");
        employee.get().add(link);
         return  employee.get();
         //utilizando mis propiios mensaje a las excepciones
//        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
//
//        Link link = linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel();
//        employee.add(link);
//        link = linkTo(methodOn(EmployeeController.class).all()).withRel("todos");
//        employee.add(link);
//        return  employee;

    }

    // employee ---> objeto, tiene unos atributos : id, name y role
    // Empoyee employee new Employee("Manuel vega", "student")
    // Long id = 4;
    // employee ---> tabla, tiene unas columnas : id, name y role
    // insert into employee (name, role) values ("manuel vega", "student")
    @PutMapping("/employees/{id}")
    Employee replaceEmployee(@RequestBody Employee employee, @PathVariable Long id) {
        return repository.findById(id).map(employeeTemp -> { // variable temporal llamada employeeTemp
            employeeTemp.setName(employee.getName());
            employeeTemp.setRole(employee.getRole());
            return repository.save(employeeTemp); // update
        }).orElseGet(() -> {
                    employee.setId(id);
                    return repository.save(employee); // insert
                }
        );
    }

    @DeleteMapping("/employees/{id}")
    void deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
