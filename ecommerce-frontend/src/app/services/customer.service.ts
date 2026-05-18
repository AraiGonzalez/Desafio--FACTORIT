import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer } from '../models/cart.model';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private apiUrl = 'http://localhost:8080/api/customers';
  constructor(private http: HttpClient) {}

  getAll(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.apiUrl);
  }

  getVipCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.apiUrl}/vip`);
  }

  getNewVipInMonth(year: number, month: number): Observable<Customer[]> {
    const params = new HttpParams()
      .set('year', year.toString())
      .set('month', month.toString());
    return this.http.get<Customer[]>(`${this.apiUrl}/vip/new`, { params });
  }

  getLostVipInMonth(year: number, month: number): Observable<Customer[]> {
    const params = new HttpParams()
      .set('year', year.toString())
      .set('month', month.toString());
    return this.http.get<Customer[]>(`${this.apiUrl}/vip/lost`, { params });
  }

  recalculateVip(year: number, month: number): Observable<void> {
    const params = new HttpParams()
      .set('year', year.toString())
      .set('month', month.toString());
    return this.http.post<void>(`${this.apiUrl}/recalculate-vip`, null, { params });
  }
}