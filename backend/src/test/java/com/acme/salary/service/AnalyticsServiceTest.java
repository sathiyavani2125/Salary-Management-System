package com.acme.salary.service;

import com.acme.salary.dto.AnalyticsDTO;
import com.acme.salary.model.Employee;
import com.acme.salary.repository.EmployeeRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@MicronautTest
class AnalyticsServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        analyticsService = new AnalyticsService(employeeRepository);
    }

    @Test
    void getOverviewAnalytics_WithEmployees_ReturnsCorrectStatistics() {
        // Arrange
        List<Employee> employees = createSampleEmployees();
        when(employeeRepository.findAllActive()).thenReturn(employees);
        when(employeeRepository.count()).thenReturn((long) employees.size());

        // Act
        AnalyticsDTO result = analyticsService.getOverviewAnalytics();

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getTotalEmployees());
        assertNotNull(result.getAverageSalary());
        assertNotNull(result.getMedianSalary());
        assertNotNull(result.getTotalPayroll());
        assertTrue(result.getAverageSalary().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void getOverviewAnalytics_WithNoEmployees_ReturnsEmptyStatistics() {
        // Arrange
        when(employeeRepository.findAllActive()).thenReturn(Arrays.asList());
        when(employeeRepository.count()).thenReturn(0L);

        // Act
        AnalyticsDTO result = analyticsService.getOverviewAnalytics();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalEmployees());
        assertNull(result.getAverageSalary());
    }

    @Test
    void getOverviewAnalytics_CalculatesMedianCorrectly() {
        // Arrange
        List<Employee> employees = createSampleEmployees();
        when(employeeRepository.findAllActive()).thenReturn(employees);
        when(employeeRepository.count()).thenReturn((long) employees.size());

        // Act
        AnalyticsDTO result = analyticsService.getOverviewAnalytics();

        // Assert
        // With salaries [50000, 60000, 80000, 100000, 120000], median should be 80000
        assertEquals(new BigDecimal("80000.00"), result.getMedianSalary());
    }

    @Test
    void getOverviewAnalytics_IncludesDepartmentBreakdown() {
        // Arrange
        List<Employee> employees = createSampleEmployees();
        when(employeeRepository.findAllActive()).thenReturn(employees);
        when(employeeRepository.count()).thenReturn((long) employees.size());

        // Act
        AnalyticsDTO result = analyticsService.getOverviewAnalytics();

        // Assert
        assertNotNull(result.getDepartmentBreakdown());
        assertTrue(result.getDepartmentBreakdown().containsKey("Engineering"));
        assertTrue(result.getDepartmentBreakdown().containsKey("Sales"));
    }

    @Test
    void getOverviewAnalytics_IncludesSalaryDistribution() {
        // Arrange
        List<Employee> employees = createSampleEmployees();
        when(employeeRepository.findAllActive()).thenReturn(employees);
        when(employeeRepository.count()).thenReturn((long) employees.size());

        // Act
        AnalyticsDTO result = analyticsService.getOverviewAnalytics();

        // Assert
        assertNotNull(result.getSalaryDistribution());
        assertTrue(result.getSalaryDistribution().containsKey("40K-60K"));
        assertTrue(result.getSalaryDistribution().containsKey("60K-80K"));
        assertTrue(result.getSalaryDistribution().containsKey("80K-100K"));
    }

    // Helper method
    private List<Employee> createSampleEmployees() {
        Employee emp1 = createEmployee("EMP001", "Engineering", "United States", new BigDecimal("100000"));
        Employee emp2 = createEmployee("EMP002", "Engineering", "United States", new BigDecimal("120000"));
        Employee emp3 = createEmployee("EMP003", "Sales", "United States", new BigDecimal("80000"));
        Employee emp4 = createEmployee("EMP004", "Sales", "India", new BigDecimal("50000"));
        Employee emp5 = createEmployee("EMP005", "Marketing", "United Kingdom", new BigDecimal("60000"));
        
        return Arrays.asList(emp1, emp2, emp3, emp4, emp5);
    }

    private Employee createEmployee(String code, String dept, String country, BigDecimal salary) {
        Employee emp = new Employee();
        emp.setEmployeeCode(code);
        emp.setFirstName("Test");
        emp.setLastName("Employee");
        emp.setEmail(code.toLowerCase() + "@acme.com");
        emp.setDepartment(dept);
        emp.setCountry(country);
        emp.setJobTitle("Engineer");
        emp.setSalary(salary);
        emp.setCurrency("USD");
        emp.setDateJoined(LocalDate.now());
        emp.setGender("Male");
        emp.setExperienceYears(5);
        emp.setPerformanceRating("Meets Expectations");
        emp.setIsActive(true);
        return emp;
    }
}
