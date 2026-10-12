package com.acme.salary.service;

import com.acme.salary.dto.AnalyticsDTO;
import com.acme.salary.model.Employee;
import com.acme.salary.repository.EmployeeRepository;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class AnalyticsService {

    private final EmployeeRepository employeeRepository;

    public AnalyticsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public AnalyticsDTO getOverviewAnalytics() {
        List<Employee> allEmployees = employeeRepository.findAllActive();
        long totalEmployees = employeeRepository.count();
        
        AnalyticsDTO analytics = new AnalyticsDTO();
        analytics.setTotalEmployees(allEmployees.size());
        analytics.setActiveEmployees(allEmployees.size());
        analytics.setInactiveEmployees(Math.max(0, (int) (totalEmployees - allEmployees.size())));
        
        if (allEmployees.isEmpty()) {
            return analytics;
        }

        // Calculate salary statistics
        List<BigDecimal> salaries = allEmployees.stream()
                .map(Employee::getSalary)
                .sorted()
                .collect(Collectors.toList());

        BigDecimal totalPayroll = salaries.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageSalary = totalPayroll.divide(
                new BigDecimal(allEmployees.size()), 2, RoundingMode.HALF_UP);
        
        BigDecimal medianSalary = calculateMedian(salaries);
        BigDecimal minSalary = salaries.get(0);
        BigDecimal maxSalary = salaries.get(salaries.size() - 1);

        analytics.setTotalPayroll(totalPayroll);
        analytics.setAverageSalary(averageSalary);
        analytics.setMedianSalary(medianSalary);
        analytics.setMinSalary(minSalary);
        analytics.setMaxSalary(maxSalary);

        // Department breakdown
        Map<String, AnalyticsDTO.DepartmentStats> deptBreakdown = allEmployees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                employees -> new AnalyticsDTO.DepartmentStats(
                                        employees.size(),
                                        calculateAverage(employees)
                                )
                        )
                ));
        analytics.setDepartmentBreakdown(deptBreakdown);
        analytics.setTopDepartment(deptBreakdown.entrySet().stream()
                .max(Comparator.comparingLong(entry -> entry.getValue().getCount()))
                .map(Map.Entry::getKey)
                .orElse(null));

        // Country breakdown
        Map<String, AnalyticsDTO.CountryStats> countryBreakdown = allEmployees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getCountry,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                employees -> new AnalyticsDTO.CountryStats(
                                        employees.size(),
                                        calculateAverage(employees)
                                )
                        )
                ));
        analytics.setCountryBreakdown(countryBreakdown);
        analytics.setTopCountry(countryBreakdown.entrySet().stream()
                .max(Comparator.comparingLong(entry -> entry.getValue().getCount()))
                .map(Map.Entry::getKey)
                .orElse(null));

        // Salary distribution (histogram)
        Map<String, Long> distribution = new LinkedHashMap<>();
        distribution.put("0-40K", salaries.stream().filter(s -> s.compareTo(new BigDecimal("40000")) < 0).count());
        distribution.put("40K-60K", salaries.stream().filter(s -> s.compareTo(new BigDecimal("40000")) >= 0 && s.compareTo(new BigDecimal("60000")) < 0).count());
        distribution.put("60K-80K", salaries.stream().filter(s -> s.compareTo(new BigDecimal("60000")) >= 0 && s.compareTo(new BigDecimal("80000")) < 0).count());
        distribution.put("80K-100K", salaries.stream().filter(s -> s.compareTo(new BigDecimal("80000")) >= 0 && s.compareTo(new BigDecimal("100000")) < 0).count());
        distribution.put("100K-150K", salaries.stream().filter(s -> s.compareTo(new BigDecimal("100000")) >= 0 && s.compareTo(new BigDecimal("150000")) < 0).count());
        distribution.put("150K+", salaries.stream().filter(s -> s.compareTo(new BigDecimal("150000")) >= 0).count());
        analytics.setSalaryDistribution(distribution);

        return analytics;
    }

    private BigDecimal calculateMedian(List<BigDecimal> sortedSalaries) {
        int size = sortedSalaries.size();
        if (size % 2 == 0) {
            return sortedSalaries.get(size / 2 - 1)
                    .add(sortedSalaries.get(size / 2))
                    .divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        } else {
            return sortedSalaries.get(size / 2);
        }
    }

    private BigDecimal calculateAverage(List<Employee> employees) {
        if (employees.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = employees.stream()
                .map(Employee::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(employees.size()), 2, RoundingMode.HALF_UP);
    }
}
