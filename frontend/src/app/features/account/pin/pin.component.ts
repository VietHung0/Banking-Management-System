import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { PinRequest, PinUpdateRequest } from '../../../core/models/account.model';
import { AccountService } from '../../../core/services/account.service';

@Component({
  selector: 'app-pin',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './pin.component.html',
  styleUrl: './pin.component.css'
})
export class PinComponent {
  hasPin = false;
  statusMessage = '';
  errorMessage = '';
  isLoading = false;

  createPinRequest: PinRequest = {
    pin: '',
    password: ''
  };

  updatePinRequest: PinUpdateRequest = {
    oldPin: '',
    newPin: '',
    password: ''
  };

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.checkPin();
  }

  checkPin(): void {
    this.errorMessage = '';
    this.accountService.checkPin().subscribe({
      next: (response) => {
        this.statusMessage = response;
        this.hasPin = response.includes('đã được tạo');
      },
      error: () => {
        this.errorMessage = 'Không thể kiểm tra trạng thái PIN.';
      }
    });
  }

  createPin(): void {
    this.submit(() => this.accountService.createPin(this.createPinRequest));
  }

  updatePin(): void {
    this.submit(() => this.accountService.updatePin(this.updatePinRequest));
  }

  private submit(action: () => ReturnType<AccountService['createPin']>): void {
    this.errorMessage = '';
    this.statusMessage = '';
    this.isLoading = true;

    action().subscribe({
      next: (response) => {
        this.isLoading = false;
        this.statusMessage = response;
        this.checkPin();
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Thao tác PIN thất bại. Vui lòng kiểm tra lại thông tin.';
      }
    });
  }
}
