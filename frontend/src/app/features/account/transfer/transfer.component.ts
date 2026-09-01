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
    amount: 0,
    message: ''
  };

  successMessage = '';
  errorMessage = '';
  recipientName = '';
  recipientMessage = '';
  isLoading = false;
  isCheckingRecipient = false;

  constructor(private accountService: AccountService) {}

  checkRecipient(): void {
    const accountNumber = this.transferRequest.targetAccountNumber.trim();
    this.recipientName = '';
    this.recipientMessage = '';

    if (!accountNumber) {
      return;
    }

    this.isCheckingRecipient = true;

    this.accountService.getRecipient(accountNumber).subscribe({
      next: (recipient) => {
        this.isCheckingRecipient = false;
        this.recipientName = recipient.name;
      },
      error: () => {
        this.isCheckingRecipient = false;
        this.recipientMessage = 'Không tìm thấy tài khoản nhận.';
      }
    });
  }

  onSubmit(): void {
    this.successMessage = '';
    this.errorMessage = '';
    this.isLoading = true;

    this.accountService.transfer(this.transferRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = this.recipientName
          ? `${response} tới ${this.recipientName}.`
          : response;
        this.transferRequest = {
          targetAccountNumber: '',
          pin: '',
          amount: 0,
          message: ''
        };
        this.recipientName = '';
        this.recipientMessage = '';
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Chuyển khoản thất bại. Vui lòng kiểm tra tài khoản nhận, PIN hoặc số dư.';
      }
    });
  }
}
