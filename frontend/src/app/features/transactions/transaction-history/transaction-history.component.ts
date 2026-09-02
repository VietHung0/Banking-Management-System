import { Component } from '@angular/core';
import { CurrencyPipe, DatePipe, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Transaction, TransactionFilter, TransactionType } from '../../../core/models/transaction.model';
import { TransactionService } from '../../../core/services/transaction.service';

@Component({
  selector: 'app-transaction-history',
  standalone: true,
  imports: [NgFor, NgIf, DatePipe, CurrencyPipe, FormsModule],
  templateUrl: './transaction-history.component.html',
  styleUrl: './transaction-history.component.css'
})
export class TransactionHistoryComponent {
  transactions: Transaction[] = [];
  filter: TransactionFilter = {
    type: '',
    fromDate: '',
    toDate: ''
  };
  transactionTypes: TransactionType[] = ['CASH_DEPOSIT', 'CASH_WITHDRAWAL', 'CASH_TRANSFER'];
  errorMessage = '';
  isLoading = false;

  constructor(private transactionService: TransactionService) { }

  ngOnInit(): void {
    this.loadTransactions();
  }

  loadTransactions(): void {
    this.errorMessage = '';
    this.isLoading = true;

    this.transactionService.getTransactions(this.filter).subscribe({
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

  clearFilter(): void {
    this.filter = {
      type: '',
      fromDate: '',
      toDate: ''
    };
    this.loadTransactions();
  }
}
