import { Component } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';

import { Transaction } from '../../../core/models/transaction.model';
import { TransactionService } from '../../../core/services/transaction.service';

@Component({
  selector: 'app-transaction-history',
  standalone: true,
  imports: [NgFor, NgIf, DatePipe],
  templateUrl: './transaction-history.component.html',
  styleUrl: './transaction-history.component.css'
})
export class TransactionHistoryComponent {
  transactions: Transaction[] = [];
  errorMessage = '';
  isLoading = false;

  constructor(private transactionService: TransactionService) { }

  ngOnInit(): void {
    this.loadTransactions();
  }

  loadTransactions(): void {
    this.errorMessage = '';
    this.isLoading = true;

    this.transactionService.getTransactions().subscribe({
      next: (transactions) => {
        this.transactions = transactions;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Không thể tải lịch sử giao dịch.';
      }
    });
  }
}
