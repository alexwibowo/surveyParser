package com.github.wibowo.survey.model;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class Employee {

    private final String id;

    private final String email;

    public Employee(final String id,
                    final String email) {
        this.id = requireNonNull(id);
        this.email = requireNonNull(email);
    }


    public String id() {
        return id;
    }

    public String email() {
        return email;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) &&
                Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
