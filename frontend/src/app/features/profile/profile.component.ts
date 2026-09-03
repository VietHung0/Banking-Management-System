import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { UpdateUserRequest, UserResponse } from '../../core/models/dashboard.model';
import { DashboardService } from '../../core/services/dashboard.service';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [NgIf, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  user?: UserResponse;
  updateRequest: UpdateUserRequest = {
    name: '',
    countryCode: '',
    phoneNumber: '',
    address: ''
  };

  errorMessage = '';
  successMessage = '';
  isLoading = false;
  isEditing = false;
  isSaving = false;

  constructor(
    private dashboardService: DashboardService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.isLoading = true;

    this.dashboardService.getUser().subscribe({
      next: (user) => {
        this.user = user;
        this.fillUpdateForm(user);
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'お客さま情報を取得できませんでした。';
        this.isLoading = false;
      }
    });
  }

  startEdit(): void {
    if (this.user) {
      this.fillUpdateForm(this.user);
    }

    this.successMessage = '';
    this.errorMessage = '';
    this.isEditing = true;
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.successMessage = '';
    this.errorMessage = '';
  }

  saveProfile(): void {
    this.successMessage = '';
    this.errorMessage = '';
    this.isSaving = true;

    this.userService.updateProfile(this.updateRequest).subscribe({
      next: (user) => {
        this.user = user;
        this.fillUpdateForm(user);
        this.isSaving = false;
        this.isEditing = false;
        this.successMessage = 'お客さま情報を更新しました。';
      },
      error: () => {
        this.isSaving = false;
        this.errorMessage = 'お客さま情報を更新できませんでした。入力内容をご確認ください。';
      }
    });
  }

  private fillUpdateForm(user: UserResponse): void {
    this.updateRequest = {
      name: user.name,
      countryCode: user.countryCode,
      phoneNumber: user.phoneNumber,
      address: user.address
    };
  }

  getAccountTypeLabel(accountType?: string): string {
    switch (accountType) {
      case 'Ordinary Deposit':
      case 'Savings':
        return '普通預金';
      default:
        return accountType || '-';
    }
  }
}
