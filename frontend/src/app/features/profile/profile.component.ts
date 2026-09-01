import { Component } from '@angular/core';
import { NgIf } from '@angular/common';

import { UserResponse } from '../../core/models/dashboard.model';
import { DashboardService } from '../../core/services/dashboard.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [NgIf],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  user?: UserResponse;
  errorMessage = '';
  isLoading = false;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.isLoading = true;

    this.dashboardService.getUser().subscribe({
      next: (user) => {
        this.user = user;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Không thể tải thông tin profile.';
        this.isLoading = false;
      }
    });
  }
}
