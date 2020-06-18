package org.dirnea.lab03.util;

public class EmployeeNotFoundException extends RuntimeException{
    public EmployeeNotFoundException(Long id) {
        super("Could not find employee " + id);
    }
}
