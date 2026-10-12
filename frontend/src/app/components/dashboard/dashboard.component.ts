import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsService } from '../../services/analytics.service';
import { AnalyticsData } from '../../models/employee.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  analytics: AnalyticsData | null = null;
  loading = true;
  error: string | null = null;

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    this.loading = true;
    this.analyticsService.getOverview().subscribe({
      next: (data) => {
        this.analytics = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load analytics data';
        this.loading = false;
        console.error('Error loading analytics:', err);
      }
    });
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(value);
  }

  getDepartmentNames(): string[] {
    return this.analytics?.departmentBreakdown ? Object.keys(this.analytics.departmentBreakdown) : [];
  }

  getCountryNames(): string[] {
    return this.analytics?.countryBreakdown ? Object.keys(this.analytics.countryBreakdown) : [];
  }

  getSalaryRanges(): string[] {
    return this.analytics?.salaryDistribution ? Object.keys(this.analytics.salaryDistribution) : [];
  }

  getLargestDepartment(): { name: string; count: number; share: number } | null {
    const departments = this.analytics?.departmentBreakdown;
    const totalEmployees = this.analytics?.totalEmployees ?? 0;
    if (!departments || totalEmployees === 0) return null;

    const largest = Object.entries(departments).reduce((top, current) =>
      current[1].count > top[1].count ? current : top
    );
    return {
      name: largest[0],
      count: largest[1].count,
      share: largest[1].count / totalEmployees * 100
    };
  }

  getHighestSalaryDepartment(): { name: string; averageSalary: number } | null {
    const departments = this.analytics?.departmentBreakdown;
    if (!departments || Object.keys(departments).length === 0) return null;

    const highest = Object.entries(departments).reduce((top, current) =>
      current[1].averageSalary > top[1].averageSalary ? current : top
    );
    return { name: highest[0], averageSalary: highest[1].averageSalary };
  }

  getMostPopulatedSalaryBand(): { range: string; count: number; share: number } | null {
    const distribution = this.analytics?.salaryDistribution;
    const totalEmployees = this.analytics?.totalEmployees ?? 0;
    if (!distribution || totalEmployees === 0) return null;

    const mostPopulated = Object.entries(distribution).reduce((top, current) =>
      current[1] > top[1] ? current : top
    );
    if (mostPopulated[1] === 0) return null;

    return {
      range: mostPopulated[0],
      count: mostPopulated[1],
      share: mostPopulated[1] / totalEmployees * 100
    };
  }
}
