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
        this.recipientMessage = '振込先口座が見つかりません。';
      }
    });
  }

  onSubmit(): void {
    if (this.isLoading) {
      return;
    }

    this.successMessage = '';
    this.errorMessage = '';
    this.transferRequest.targetAccountNumber = this.transferRequest.targetAccountNumber.trim();

    if (!this.isValidTransferAmount()) {
      this.errorMessage = '振込は1円以上、1,000,000円以下で入力してください。';
      return;
    }

    this.isLoading = true;
    const idempotencyKey = this.accountService.createIdempotencyKey();

    this.accountService.transfer(this.transferRequest, idempotencyKey).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = this.recipientName
          ? `${this.recipientName} さまへの振込が完了しました。`
          : '振込が完了しました。';
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
        this.errorMessage = '振込できませんでした。振込先口座、暗証番号、残高、または1日の上限をご確認ください。';
      }
    });
  }

  private isValidTransferAmount(): boolean {
    const amount = this.transferRequest.amount;
    return amount >= 1 && amount <= 1000000 && amount % 1 === 0;
  }
}
