import { Component, OnInit } from '@angular/core';
import { CustomerService } from '../../services/customer.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Customer } from '../../models/cart.model';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css'
})
export class CustomersComponent implements OnInit {

  // DATA
  allCustomers: Customer[] = [];
  newVipCustomers: Customer[] = [];
  lostVipCustomers: Customer[] = [];

  // VIEW STATE
  viewMode: 'ALL' | 'NEW_VIP' | 'LOST_VIP' = 'ALL';

  // FILTERS
  filterYear: number = new Date().getFullYear();
  filterMonth: number = new Date().getMonth() + 1;

  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  // ALL CUSTOMERS
  loadAll(): void {
    this.viewMode = 'ALL';

    this.customerService.getAll()
      .subscribe((customers: Customer[]) => {
        this.allCustomers = customers;
      });
  }

  // NEW VIP
  loadNewVip(): void {
    this.viewMode = 'NEW_VIP';

    this.customerService.getNewVipInMonth(this.filterYear, this.filterMonth)
      .subscribe((customers: Customer[]) => {
        this.newVipCustomers = customers;
      });
  }

  // LOST VIP
  loadLostVip(): void {
    this.viewMode = 'LOST_VIP';

    this.customerService.getLostVipInMonth(this.filterYear, this.filterMonth)
      .subscribe((customers: Customer[]) => {
        this.lostVipCustomers = customers;
      });
  }

  // RECALCULATE VIP
  recalculate(): void {
    this.customerService.recalculateVip(this.filterYear, this.filterMonth)
      .subscribe(() => {

        this.loadAll();

        if (this.viewMode === 'NEW_VIP') {
          this.loadNewVip();
        }

        if (this.viewMode === 'LOST_VIP') {
          this.loadLostVip();
        }
      });
  }
}