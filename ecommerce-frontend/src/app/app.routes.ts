import { Routes } from '@angular/router';
import { CartComponent } from './components/cart/cart.component';
import { CustomersComponent } from './components/customers/customers.component';
export const routes: Routes = [
  { path: '', redirectTo: 'cart', pathMatch: 'full' },
  { path: 'cart', component: CartComponent },
  { path: 'customers', component: CustomersComponent },
];