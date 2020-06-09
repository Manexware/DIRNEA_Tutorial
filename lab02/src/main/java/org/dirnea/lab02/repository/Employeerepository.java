package org.dirnea.lab02.repository;

import org.dirnea.lab02.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Employeerepository  extends JpaRepository<Employee, Long> {
}
