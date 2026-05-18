import { Component, OnInit } from '@angular/core';
import { CartStatus, Customer, Product} from '../../models/cart.model';
import { CartService } from '../../services/cart.service';
import { ProductService } from '../../services/product.service';
import { CustomerService } from '../../services/customer.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cart',
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css'
})
export class CartComponent implements OnInit {

  customers: Customer[] = [];
  products: Product[] = [];
  currentCart: CartStatus | null = null;

  selectedCustomerId: number | null = null;
  selectedProductId: number | null = null;
  quantity: number = 1;
  simulatedDate: string = '';
  message: string = '';
  isError: boolean = false;

  constructor(
    private cartService: CartService,
    private productService: ProductService,
    private customerService: CustomerService
  ) {}

  ngOnInit(): void {
    this.customerService.getAll().subscribe(c => this.customers = c);
    this.productService.getAll().subscribe(p => this.products = p);
  }

  createCart(): void {
    if (!this.selectedCustomerId) return;
    this.cartService.createCart(
      this.selectedCustomerId,
      this.simulatedDate || undefined
    ).subscribe({
      next: (cart) => {
        this.notify(`Carrito #${cart.id} creado — Tipo: ${this.typeLabel(cart.type)}`, false);
        this.refreshCart(cart.id);
      },
      error: () => this.notify('Error al crear el carrito', true)
    });
  }

  addProduct(): void {
    if (!this.currentCart || !this.selectedProductId) return;
    this.cartService.addProduct(
      this.currentCart.cartId, this.selectedProductId, this.quantity
    ).subscribe({
      next: (cart) => {
        this.notify('Producto agregado', false);
        this.refreshCart(cart.id);
      },
      error: () => this.notify('Error al agregar producto', true)
    });
  }

  removeOneUnit(productId: number): void {
    if (!this.currentCart) return;
    this.cartService.removeProduct(this.currentCart.cartId, productId, 1)
      .subscribe({
        next: (cart) => {
          this.notify('Unidad eliminada', false);
          this.refreshCart(cart.id);
        }
      });
  }

  removeProduct(productId: number): void {
    if (!this.currentCart) return;
    // Buscar la cantidad actual del producto para eliminarlo completo
    const item = this.currentCart.items.find(i => i.product.id === productId);
    if (!item) return;
    this.cartService.removeProduct(this.currentCart.cartId, productId, item.quantity)
      .subscribe({
        next: (cart) => {
          this.notify('Producto eliminado del carrito', false);
          this.refreshCart(cart.id);
        }
      });
  }

  checkout(): void {
    if (!this.currentCart) return;
    this.cartService.checkout(this.currentCart.cartId).subscribe({
      next: () => {
        this.notify('¡Compra finalizada con éxito!', false);
        this.currentCart = null;
      },
      error: () => this.notify('Error al finalizar la compra', true)
    });
  }

  deleteCart(): void {
    if (!this.currentCart) return;
    this.cartService.deleteCart(this.currentCart.cartId).subscribe({
      next: () => {
        this.notify('Carrito eliminado', false);
        this.currentCart = null;
      },
      error: () => this.notify('Error al eliminar el carrito', true)
    });
  }

  private refreshCart(cartId: number): void {
    this.cartService.getCartStatus(cartId).subscribe(c => this.currentCart = c);
  }

  private notify(msg: string, error: boolean): void {
    this.message = msg;
    this.isError = error;
    setTimeout(() => this.message = '', 3000);
  }

  typeLabel(type: string): string {
    const map: any = {
      COMMON: '🛒 Común',
      SPECIAL_DATE: '📅 Fecha Especial',
      VIP: '⭐ VIP'
    };
    return map[type] || type;
  }

  getProductName(id: number): string {
    return this.products.find(p => p.id === id)?.name || '';
  }
}