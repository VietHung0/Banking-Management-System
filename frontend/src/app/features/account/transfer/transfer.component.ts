import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { FundTransferRequest } from '../../../core/models/account.model';
import { AccountService } from '../../../core/services/account.service';

@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './transfer.component.html',
  styleUrl: './transfer.component.css'
})
export class TransferComponent {
  transferRequest: FundTransferRequest = {
    targetAccountNumber: '',
    pin: '',
    amount: 0
  };

  successMessage = '';
  errorMessage = '';
  isLoading = false;

  constructor(private accountService: AccountService) {}

  onSubmit(): void {
    this.successMessage = '';
    this.errorMessage = '';
    this.isLoading = true;

    this.accountService.transfer(this.transferRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = response;
        this.transferRequest = {
          targetAccountNumber: '',
          pin: '',
          amount: 0
        };
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Chuyển khoản thất bại. Vui lòng kiểm tra tài khoản nhận, PIN hoặc số dư.';
      }
    });
  }
}
