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
  cashFlowRows: CashFlowRow[] = [];
  moneyIn = 0;
  moneyOut = 0;
  netChange = 0;
  selectedRange: CashFlowRange = 'THIS_MONTH';
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
        this.buildCashFlow(transactions);
      },
      error: () => {
        this.recentTransactions = [];
        this.buildCashFlow([]);
      }
    });
  }

  changeRange(range: CashFlowRange): void {
    this.selectedRange = range;
    this.transactionService.getTransactions().subscribe({
      next: (transactions) => this.buildCashFlow(transactions),
      error: () => this.buildCashFlow([])
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

  private buildCashFlow(transactions: Transaction[]): void {
    const filteredTransactions = this.filterByRange(transactions);
    const rowsByDate = new Map<string, CashFlowRow>();

    filteredTransactions.forEach((transaction) => {
      const dateKey = transaction.transactionDate.slice(0, 10);
      const row = rowsByDate.get(dateKey) ?? {
        date: dateKey,
        moneyIn: 0,
        moneyOut: 0,
        inPercent: 0,
        outPercent: 0
      };

      if (transaction.transactionType === 'CASH_DEPOSIT') {
        row.moneyIn += transaction.amount;
      } else {
        row.moneyOut += transaction.amount;
      }

      rowsByDate.set(dateKey, row);
    });

    this.moneyIn = filteredTransactions
      .filter((transaction) => transaction.transactionType === 'CASH_DEPOSIT')
      .reduce((total, transaction) => total + transaction.amount, 0);
    this.moneyOut = filteredTransactions
      .filter((transaction) => transaction.transactionType !== 'CASH_DEPOSIT')
      .reduce((total, transaction) => total + transaction.amount, 0);
    this.netChange = this.moneyIn - this.moneyOut;

    const maxAmount = Math.max(...Array.from(rowsByDate.values()).map((row) => Math.max(row.moneyIn, row.moneyOut)), 1);
    this.cashFlowRows = Array.from(rowsByDate.values())
      .sort((a, b) => a.date.localeCompare(b.date))
      .slice(-7)
      .map((row) => ({
        ...row,
        inPercent: this.toPercent(row.moneyIn, maxAmount),
        outPercent: this.toPercent(row.moneyOut, maxAmount)
      }));
  }

  private filterByRange(transactions: Transaction[]): Transaction[] {
    const now = new Date();

    if (this.selectedRange === 'ALL') {
      return transactions;
    }

    const startDate = new Date(now);
    if (this.selectedRange === 'LAST_7_DAYS') {
      startDate.setDate(now.getDate() - 6);
    } else {
      startDate.setDate(1);
    }
    startDate.setHours(0, 0, 0, 0);

    return transactions.filter((transaction) => new Date(transaction.transactionDate) >= startDate);
  }

  private toPercent(amount: number, maxAmount: number): number {
    return amount === 0 ? 0 : Math.max((amount / maxAmount) * 100, 8);
  }
}

type CashFlowRange = 'THIS_MONTH' | 'LAST_7_DAYS' | 'ALL';

interface CashFlowRow {
  date: string;
  moneyIn: number;
  moneyOut: number;
  inPercent: number;
  outPercent: number;
}
