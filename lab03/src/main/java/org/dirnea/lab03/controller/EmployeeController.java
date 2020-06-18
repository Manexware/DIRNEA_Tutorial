package org.dirnea.lab03.controller;

//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.headers.Header;
//import io.swagger.v3.oas.annotations.media.ArraySchema;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.tags.Tag;
import org.dirnea.lab03.model.Employee;
import org.dirnea.lab03.repository.EmployeeRepository;
import org.dirnea.lab03.util.EmployeeNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
//@Tag(name = "Empleado")
public class EmployeeController {

    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    // Aggregate root
    // http://localohots:8080/employees
    @GetMapping("/employees")
//    @Operation(description = "Listar todos los empleados", responses = {
//            @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = Employee.class))), responseCode = "200"),
//            @ApiResponse(responseCode = "404", description = "No hay empleados")})
    List<Employee> all() {
        List<Employee> employees = repository.findAll();
        for (final Employee employee : employees) {
            Link link = linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel();
            employee.add(link);
        }

        return employees;//repository.findAll();
    }

    @PostMapping("/employees")
//    @Operation(description = "Crear un nuevo empleado", responses = {
//            @ApiResponse(content = @Content(schema = @Schema(implementation = Employee.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = @Header(name = "Empleado"), responseCode = "201"),
//            @ApiResponse(responseCode = "409", description = "El empleado ya existe") })
    Employee newEmployee(@RequestBody Employee employee) {
        return repository.save(employee);
    }

    // Single item
    // http://localohots:8080/employees/4
    @GetMapping("/employees/{id}")
    Employee one(@PathVariable Long id) {
        //{ "id": 1, "name":"Manuel Vega", "role":"Instructor"}
        // utilizando los mensaje de HTTP
//        Optional<Employee> employee = repository.findById(id);
//        Link link = linkTo(methodOn(EmployeeController.class).one(employee.get().getId())).withSelfRel();
//        employee.get().add(link);
//        link = linkTo(methodOn(EmployeeController.class).all()).withRel("todos");
//        employee.get().add(link);
//        return employee.get();
        //utilizando mis propiios mensaje a las excepciones
        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        Link link = linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel();
        employee.add(link);
        link = linkTo(methodOn(EmployeeController.class).all()).withRel("todos");
        employee.add(link);
        return  employee;

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
//    @Operation(description = "Borrar un empleado", responses = {
//            @ApiResponse(responseCode = "204", description = "PEl empleado fue borrado"),
//            @ApiResponse(responseCode = "404", description = "Empleado no existe") })
    void deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
