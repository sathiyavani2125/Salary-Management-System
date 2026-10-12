package com.acme.salary.dto;

import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import java.util.Map;

@Serdeable
public class AnalyticsDTO {
    
    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
    private String topDepartment;
    private String topCountry;
    private BigDecimal totalPayroll;
    private BigDecimal averageSalary;
    private BigDecimal medianSalary;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private Map<String, DepartmentStats> departmentBreakdown;
    private Map<String, CountryStats> countryBreakdown;
    private Map<String, Long> salaryDistribution;

    // Constructors
    public AnalyticsDTO() {}

    // Getters and Setters
    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getActiveEmployees() {
        return activeEmployees;
    }

    public void setActiveEmployees(long activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public long getInactiveEmployees() {
        return inactiveEmployees;
    }

    public void setInactiveEmployees(long inactiveEmployees) {
        this.inactiveEmployees = inactiveEmployees;
    }

    public String getTopDepartment() {
        return topDepartment;
    }

    public void setTopDepartment(String topDepartment) {
        this.topDepartment = topDepartment;
    }

    public String getTopCountry() {
        return topCountry;
    }

    public void setTopCountry(String topCountry) {
        this.topCountry = topCountry;
    }

    public BigDecimal getTotalPayroll() {
        return totalPayroll;
    }

    public void setTotalPayroll(BigDecimal totalPayroll) {
        this.totalPayroll = totalPayroll;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(BigDecimal averageSalary) {
        this.averageSalary = averageSalary;
    }

    public BigDecimal getMedianSalary() {
        return medianSalary;
    }

    public void setMedianSalary(BigDecimal medianSalary) {
        this.medianSalary = medianSalary;
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }

    public BigDecimal getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(BigDecimal maxSalary) {
        this.maxSalary = maxSalary;
    }

    public Map<String, DepartmentStats> getDepartmentBreakdown() {
        return departmentBreakdown;
    }

    public void setDepartmentBreakdown(Map<String, DepartmentStats> departmentBreakdown) {
        this.departmentBreakdown = departmentBreakdown;
    }

    public Map<String, CountryStats> getCountryBreakdown() {
        return countryBreakdown;
    }

    public void setCountryBreakdown(Map<String, CountryStats> countryBreakdown) {
        this.countryBreakdown = countryBreakdown;
    }

    public Map<String, Long> getSalaryDistribution() {
        return salaryDistribution;
    }

    public void setSalaryDistribution(Map<String, Long> salaryDistribution) {
        this.salaryDistribution = salaryDistribution;
    }

    // Inner classes for nested stats
    @Serdeable
    public static class DepartmentStats {
        private long count;
        private BigDecimal averageSalary;

        public DepartmentStats() {}

        public DepartmentStats(long count, BigDecimal averageSalary) {
            this.count = count;
            this.averageSalary = averageSalary;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public BigDecimal getAverageSalary() {
            return averageSalary;
        }

        public void setAverageSalary(BigDecimal averageSalary) {
            this.averageSalary = averageSalary;
        }
    }

    @Serdeable
    public static class CountryStats {
        private long count;
        private BigDecimal averageSalary;

        public CountryStats() {}

        public CountryStats(long count, BigDecimal averageSalary) {
            this.count = count;
            this.averageSalary = averageSalary;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public BigDecimal getAverageSalary() {
            return averageSalary;
        }

        public void setAverageSalary(BigDecimal averageSalary) {
            this.averageSalary = averageSalary;
        }
    }
}
