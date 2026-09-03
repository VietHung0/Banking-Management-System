import { CurrencyPipe, NgIf } from '@angular/common';
import { Component } from '@angular/core';

import { AccountResponse } from '../../../core/models/account.model';
import { DashboardService } from '../../../core/services/dashboard.service';

@Component({
  selector: 'app-account-info',
  standalone: true,
  imports: [CurrencyPipe, NgIf],
  templateUrl: './account-info.component.html',
  styleUrl: './account-info.component.css'
})
export class AccountInfoComponent {
  account?: AccountResponse;
  errorMessage = '';
  isLoading = false;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.isLoading = true;

    this.dashboardService.getAccount().subscribe({
      next: (account) => {
        this.account = account;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = '口座情報を取得できませんでした。';
        this.isLoading = false;
      }
    });
  }

  getAccountTypeLabel(accountType?: string): string {
    switch (accountType) {
      case 'Ordinary Deposit':
        return '普通預金';
      case 'Savings':
        return '普通預金';
      default:
        return accountType || '-';
    }
  }

  getAccountStatusLabel(accountStatus?: string): string {
    switch (accountStatus) {
      case 'ACTIVE':
      case 'Active':
        return '利用中';
      case 'INACTIVE':
      case 'Inactive':
        return '停止中';
      default:
        return accountStatus || '-';
    }
  }
}
