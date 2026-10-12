import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { EmployeeService } from '../../services/employee.service';
import { Employee, PageResponse } from '../../models/employee.model';
import { debounceTime, Subject } from 'rxjs';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent implements OnInit {
  employees: Employee[] = [];
  loading = true;
  error: string | null = null;

  // Pagination
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;

  // Search and filters
  searchTerm = '';
  selectedDepartment = '';
  selectedCountry = '';
  minSalary = '';
  maxSalary = '';
  sortField = 'lastName';
  sortDirection = 'asc';
  private searchSubject = new Subject<string>();

  departments = ['Engineering', 'Sales', 'Marketing', 'Finance', 'Human Resources', 'Operations', 'Customer Support', 'Product Management', 'Legal'];
  countries = ['United States', 'India', 'United Kingdom', 'Canada', 'Germany', 'Singapore', 'Australia', 'France', 'Netherlands'];

  constructor(
    private employeeService: EmployeeService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadEmployees();
    
    // Debounce search input
    this.searchSubject.pipe(
      debounceTime(300)
    ).subscribe(term => {
      this.searchTerm = term;
      this.currentPage = 0;
      this.loadEmployees();
    });
  }

  loadEmployees(): void {
    this.loading = true;
    this.error = null;

    const hasSalaryFilter = this.minSalary.trim() !== '' || this.maxSalary.trim() !== '';
    const parsedMinSalary = this.minSalary.trim() ? Number(this.minSalary) : undefined;
    const parsedMaxSalary = this.maxSalary.trim() ? Number(this.maxSalary) : undefined;

    if (this.searchTerm || this.selectedDepartment || this.selectedCountry || hasSalaryFilter) {
      this.employeeService.searchEmployees(
        this.searchTerm || undefined,
        this.selectedDepartment || undefined,
        this.selectedCountry || undefined,
        Number.isFinite(parsedMinSalary) ? parsedMinSalary : undefined,
        Number.isFinite(parsedMaxSalary) ? parsedMaxSalary : undefined,
        this.currentPage,
        this.pageSize,
        this.sortField,
        this.sortDirection
      ).subscribe({
        next: (response) => this.handleResponse(response),
        error: (err) => this.handleError(err)
      });
    } else {
      this.employeeService.getEmployees(this.currentPage, this.pageSize, `${this.sortField}:${this.sortDirection}`).subscribe({
        next: (response) => this.handleResponse(response),
        error: (err) => this.handleError(err)
      });
    }
  }

  private handleResponse(response: PageResponse<Employee>): void {
    this.employees = response.content;
    this.totalElements = response.totalElements || response.totalSize || 0;
    this.totalPages = response.totalPages || Math.ceil((response.totalElements || response.totalSize || 0) / this.pageSize);
    // Handle different response formats from backend
    if (response.page !== undefined) {
      this.currentPage = response.page;
    } else if (response.pageable?.number !== undefined) {
      this.currentPage = response.pageable.number;
    }
    this.loading = false;
  }

  private handleError(err: any): void {
    this.error = 'Failed to load employees';
    this.loading = false;
    console.error('Error loading employees:', err);
  }

  onSearchChange(term: string): void {
    this.searchSubject.next(term);
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.loadEmployees();
  }

  onSortChange(): void {
    this.currentPage = 0;
    this.loadEmployees();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedDepartment = '';
    this.selectedCountry = '';
    this.minSalary = '';
    this.maxSalary = '';
    this.sortField = 'lastName';
    this.sortDirection = 'asc';
    this.currentPage = 0;
    this.loadEmployees();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadEmployees();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadEmployees();
    }
  }

  deleteEmployee(employee: Employee): void {
    if (confirm(`Are you sure you want to delete ${employee.firstName} ${employee.lastName}?`)) {
      this.employeeService.deleteEmployee(employee.id!).subscribe({
        next: () => {
          this.notificationService.success(
            `Employee ${employee.firstName} ${employee.lastName} deleted successfully!`
          );
          this.loadEmployees();
        },
        error: (err) => {
          this.notificationService.error('Failed to delete employee. Please try again.');
          console.error('Error deleting employee:', err);
        }
      });
    }
  }

  formatCurrency(value: number, currency: string): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency || 'USD'
    }).format(value);
  }
}
