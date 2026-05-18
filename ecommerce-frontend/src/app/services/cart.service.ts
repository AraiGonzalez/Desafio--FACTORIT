import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cart, CartStatus } from '../models/cart.model';

@Injectable({ providedIn: 'root' })
export class CartService {

  private apiUrl = 'http://localhost:8080/api/cart';

  constructor(private http: HttpClient) {}

  createCart(customerId: number, simulatedDate?: string): Observable<Cart> {
    let params = new HttpParams().set('customerId', customerId.toString());
    if (simulatedDate) params = params.set('simulatedDate', simulatedDate);
    return this.http.post<Cart>(this.apiUrl, null, { params });
  }

  deleteCart(cartId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${cartId}`);
  }

  addProduct(cartId: number, productId: number, quantity: number): Observable<Cart> {
    const params = new HttpParams()
      .set('productId', productId.toString())
      .set('quantity', quantity.toString());
    return this.http.post<Cart>(`${this.apiUrl}/${cartId}/items`, null, { params });
  }

  removeProduct(cartId: number, productId: number, quantity: number): Observable<Cart> {
    const params = new HttpParams()
      .set('productId', productId.toString())
      .set('quantity', quantity.toString());
    return this.http.delete<Cart>(`${this.apiUrl}/${cartId}/items`, { params });
  }

  getCartStatus(cartId: number): Observable<CartStatus> {
    return this.http.get<CartStatus>(`${this.apiUrl}/${cartId}/status`);
  }

  checkout(cartId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${cartId}/checkout`, null);
  }
}