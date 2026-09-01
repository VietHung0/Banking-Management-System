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
        this.errorMessage = 'Không thể tải thông tin tài khoản.';
        this.isLoading = false;
      }
    });
  }
}
