import { Component } from '@angular/core';
import { CurrencyPipe, DatePipe, NgFor, NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';

import { AccountResponse } from '../../../core/models/account.model';
import { UserResponse } from '../../../core/models/dashboard.model';
import { Transaction } from '../../../core/models/transaction.model';
import { DashboardService } from '../../../core/services/dashboard.service';
import { TransactionService } from '../../../core/services/transaction.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NgIf, NgFor, CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  user?: UserResponse;
  account?: AccountResponse;
  recentTransactions: Transaction[] = [];
  errorMessage = '';
  isLoading = false;

  constructor(
    private dashboardService: DashboardService,
    private transactionService: TransactionService
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.errorMessage = '';
    this.isLoading = true;

    this.dashboardService.getUser().subscribe({
      next: (user) => {
        this.user = user;
        this.finishLoadingIfReady();
      },
      error: () => this.handleError()
    });

    this.dashboardService.getAccount().subscribe({
      next: (account) => {
        this.account = account;
        this.finishLoadingIfReady();
      },
      error: () => this.handleError()
    });

    this.transactionService.getTransactions().subscribe({
      next: (transactions) => {
        this.recentTransactions = transactions.slice(0, 5);
      },
      error: () => {
        this.recentTransactions = [];
      }
    });
  }

  private finishLoadingIfReady(): void {
    if (this.user && this.account) {
      this.isLoading = false;
    }
  }

  private handleError(): void {
    this.isLoading = false;
    this.errorMessage = 'Không thể tải thông tin dashboard.';
  }
}
