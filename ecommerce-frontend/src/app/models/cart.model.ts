export interface Product {
  id: number;
  name: string;
  price: number;
}

export interface CartItem {
  id: number;
  product: Product;
  quantity: number;
}

export interface Cart {
  id: number;
  customer: Customer;
  type: 'COMMON' | 'SPECIAL_DATE' | 'VIP';
  status: 'OPEN' | 'COMPLETED' | 'CANCELLED';
  items: CartItem[];
}

export interface CartStatus {
  cartId: number;
  customerId: number;
  customerName: string;
  cartType: string;
  status: string;
  items: CartItem[];
  subtotal: number;
  totalDiscount: number;
  total: number;
}

export interface Customer {
  id: number;
  name: string;
  email: string;
  vip: boolean;
}