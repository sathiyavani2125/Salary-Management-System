import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employee, PageResponse } from '../models/employee.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private apiUrl = `${environment.apiUrl}/employees`;

  constructor(private http: HttpClient) {}

  getEmployees(page: number = 0, size: number = 20, sort: string = 'lastName'): Observable<PageResponse<Employee>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);
    return this.http.get<PageResponse<Employee>>(this.apiUrl, { params });
  }

  searchEmployees(
    searchTerm?: string,
    department?: string,
    country?: string,
    minSalary?: number,
    maxSalary?: number,
    page: number = 0,
    size: number = 20,
    sort: string = 'lastName',
    direction: string = 'asc'
  ): Observable<PageResponse<Employee>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort)
      .set('direction', direction);

    if (searchTerm) params = params.set('searchTerm', searchTerm);
    if (department) params = params.set('department', department);
    if (country) params = params.set('country', country);
    if (minSalary !== undefined) params = params.set('minSalary', minSalary.toString());
    if (maxSalary !== undefined) params = params.set('maxSalary', maxSalary.toString());

    return this.http.get<PageResponse<Employee>>(`${this.apiUrl}/search`, { params });
  }

  getEmployee(id: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/${id}`);
  }

  createEmployee(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.apiUrl, employee);
  }

  updateEmployee(id: string, employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.apiUrl}/${id}`, employee);
  }

  deleteEmployee(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  bulkSalaryAdjustment(employeeIds: string[], percentage: number): Observable<string> {
    const params = new HttpParams().set('percentage', percentage.toString());
    return this.http.post<string>(`${this.apiUrl}/bulk-salary-adjustment`, employeeIds, { 
      params,
      responseType: 'text' as 'json'
    });
  }
}
