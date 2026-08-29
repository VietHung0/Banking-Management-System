import { Component } from '@angular/core';
import { NgIf } from '@angular/common';

import { AccountResponse } from '../../../core/models/account.model';
import { UserResponse } from '../../../core/models/dashboard.model';
import { DashboardService } from '../../../core/services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NgIf],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  user?: UserResponse;
  account?: AccountResponse;
  errorMessage = '';
  isLoading = false;

  constructor(private dashboardService: DashboardService) {}

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
