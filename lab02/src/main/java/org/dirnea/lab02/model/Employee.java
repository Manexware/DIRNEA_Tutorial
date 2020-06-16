package org.dirnea.lab02.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Employee extends RepresentationModel<Employee> {

    private @Id @GeneratedValue Long id;
    private String name;
    private String role;

    @JsonCreator //{"nombre": "-----", "cargo": "---"}
    public Employee(@JsonProperty("nombre") String name, @JsonProperty("cargo") String role) {
        this.name = name;
        this.role = role;
    }

    Employee(){}

}
