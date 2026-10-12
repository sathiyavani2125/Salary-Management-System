export interface Employee {
  id?: string;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  country: string;
  jobTitle: string;
  salary: number;
  currency: string;
  dateJoined: string;
  gender: string;
  experienceYears: number;
  performanceRating: string;
  isActive: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface PageResponse<T> {
  content: T[];
  page?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
  totalSize?: number;
  pageable?: {
    number: number;
    size: number;
    sort?: any;
  };
}

export interface AnalyticsData {
  totalEmployees: number;
  activeEmployees: number;
  inactiveEmployees: number;
  topDepartment: string;
  topCountry: string;
  totalPayroll: number;
  averageSalary: number;
  medianSalary: number;
  minSalary: number;
  maxSalary: number;
  departmentBreakdown: { [key: string]: DepartmentStats };
  countryBreakdown: { [key: string]: CountryStats };
  salaryDistribution: { [key: string]: number };
}

export interface DepartmentStats {
  count: number;
  averageSalary: number;
}

export interface CountryStats {
  count: number;
  averageSalary: number;
}
