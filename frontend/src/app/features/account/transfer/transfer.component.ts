import { Component } from '@angular/core';
import { DecimalPipe, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { FundTransferRequest, RecipientResponse } from '../../../core/models/account.model';
import { AccountService } from '../../../core/services/account.service';

@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [DecimalPipe, FormsModule, NgIf],
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
  recipient?: RecipientResponse;
  recipientMessage = '';
  isLoading = false;
  isCheckingRecipient = false;
  isConfirming = false;

  constructor(private accountService: AccountService) {}

  checkRecipient(): void {
    const accountNumber = this.transferRequest.targetAccountNumber.trim();
    this.recipient = undefined;
    this.recipientMessage = '';
    this.isConfirming = false;

    if (!accountNumber) {
      return;
    }

    this.isCheckingRecipient = true;

    this.accountService.getRecipient(accountNumber).subscribe({
      next: (recipient) => {
        this.isCheckingRecipient = false;
        this.recipient = recipient;
      },
      error: () => {
        this.isCheckingRecipient = false;
        this.recipientMessage = '振込先口座が見つかりません。';
      }
    });
  }

  reviewTransfer(): void {
    this.successMessage = '';
    this.errorMessage = '';
    this.transferRequest.targetAccountNumber = this.transferRequest.targetAccountNumber.trim();

    if (!this.recipient || this.recipient.accountNumber !== this.transferRequest.targetAccountNumber) {
      this.errorMessage = '振込先口座を確認してください。';
      return;
    }

    if (!this.isValidTransferAmount()) {
      this.errorMessage = '振込は1円以上、1,000,000円以下で入力してください。';
      return;
    }

    if (!this.transferRequest.pin.trim()) {
      this.errorMessage = '暗証番号を入力してください。';
      return;
    }

    this.isConfirming = true;
  }

  backToEdit(): void {
    this.isConfirming = false;
    this.errorMessage = '';
  }

  onSubmit(): void {
    if (this.isLoading || !this.isConfirming || !this.recipient) {
      return;
    }

    this.successMessage = '';
    this.errorMessage = '';
    this.isLoading = true;
    const idempotencyKey = this.accountService.createIdempotencyKey();

    this.accountService.transfer(this.transferRequest, idempotencyKey).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = `${this.recipient?.name} さまへの振込が完了しました。`;
        this.transferRequest = {
          targetAccountNumber: '',
          pin: '',
          amount: 0,
          message: ''
        };
        this.recipient = undefined;
        this.recipientMessage = '';
        this.isConfirming = false;
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

  getAccountTypeLabel(accountType: string): string {
    return accountType === 'Ordinary Deposit' || accountType === 'Savings'
      ? '普通預金'
      : accountType;
  }
}
