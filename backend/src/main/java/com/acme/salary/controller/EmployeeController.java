package com.acme.salary.controller;

import com.acme.salary.dto.EmployeeDTO;
import com.acme.salary.model.Employee;
import com.acme.salary.service.EmployeeService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Get
    public Page<Employee> listEmployees(
            @QueryValue(defaultValue = "0") int page,
            @QueryValue(defaultValue = "20") int size,
            @QueryValue(defaultValue = "lastName") String sort) {
        Pageable pageable = Pageable.from(page, size).order(sort);
        return employeeService.findAll(pageable);
    }

    @Get("/search")
    public Page<Employee> searchEmployees(
            @QueryValue @Nullable String searchTerm,
            @QueryValue @Nullable String department,
            @QueryValue @Nullable String country,
            @QueryValue @Nullable BigDecimal minSalary,
            @QueryValue @Nullable BigDecimal maxSalary,
            @QueryValue(defaultValue = "lastName") String sort,
            @QueryValue(defaultValue = "asc") String direction,
            @QueryValue(defaultValue = "0") int page,
            @QueryValue(defaultValue = "20") int size) {
        Pageable pageable = Pageable.from(page, size).order(sort + ":" + direction);
        return employeeService.searchEmployees(searchTerm, department, country, minSalary, maxSalary, pageable);
    }

    @Get("/{id}")
    public HttpResponse<Employee> getEmployee(@PathVariable UUID id) {
        return employeeService.findById(id)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Post
    @Status(HttpStatus.CREATED)
    public Employee createEmployee(@Body @Valid EmployeeDTO dto) {
        return employeeService.createEmployee(dto);
    }

    @Put("/{id}")
    public Employee updateEmployee(@PathVariable UUID id, @Body @Valid EmployeeDTO dto) {
        return employeeService.updateEmployee(id, dto);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
    }

    @Post("/bulk-salary-adjustment")
    public HttpResponse<String> bulkSalaryAdjustment(
            @Body List<UUID> employeeIds,
            @QueryValue BigDecimal percentage) {
        try {
            employeeService.bulkSalaryAdjustment(employeeIds, percentage);
            return HttpResponse.ok("Salary adjustment completed successfully");
        } catch (IllegalArgumentException e) {
            return HttpResponse.badRequest(e.getMessage());
        }
    }
}
