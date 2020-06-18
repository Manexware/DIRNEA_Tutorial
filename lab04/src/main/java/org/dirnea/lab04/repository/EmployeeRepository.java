package org.dirnea.lab04.repository;

import org.dirnea.lab04.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
