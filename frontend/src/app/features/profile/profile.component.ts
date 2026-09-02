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
        this.errorMessage = 'Không thể tải thông tin profile.';
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
        this.successMessage = 'Cập nhật profile thành công.';
      },
      error: () => {
        this.isSaving = false;
        this.errorMessage = 'Cập nhật profile thất bại. Vui lòng kiểm tra lại thông tin.';
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
}
