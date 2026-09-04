import { Component } from '@angular/core';
import { DatePipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Transaction, TransactionFilter, TransactionType } from '../../../core/models/transaction.model';
import { TransactionService } from '../../../core/services/transaction.service';

@Component({
  selector: 'app-transaction-history',
  standalone: true,
  imports: [NgFor, NgIf, DatePipe, DecimalPipe, FormsModule],
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
        this.errorMessage = 'お取引明細を取得できませんでした。';
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

  getTransactionTypeLabel(type: string): string {
    switch (type) {
      case 'CASH_DEPOSIT':
        return '入金';
      case 'CASH_WITHDRAWAL':
        return '出金';
      case 'CASH_TRANSFER':
        return '振込';
      default:
        return type;
    }
  }
}
