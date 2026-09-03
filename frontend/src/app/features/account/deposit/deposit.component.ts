import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AmountRequest } from '../../../core/models/account.model';
import { AccountService } from '../../../core/services/account.service';

@Component({
  selector: 'app-deposit',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './deposit.component.html',
  styleUrl: './deposit.component.css'
})
export class DepositComponent {
  depositRequest: AmountRequest = {
    pin: '',
    amount: 0
  };

  successMessage = '';
  errorMessage = '';
  isLoading = false;

  constructor(private accountService: AccountService) { }

  onSubmit(): void {
    if (this.isLoading) {
      return;
    }

    this.successMessage = '';
    this.errorMessage = '';

    if (!this.isValidDepositAmount()) {
      this.errorMessage = '入金は1,000円以上、1,000,000円以下、1,000円単位で入力してください。';
      return;
    }

    this.isLoading = true;
    const idempotencyKey = this.accountService.createIdempotencyKey();

    this.accountService.deposit(this.depositRequest, idempotencyKey).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = '入金が完了しました。';
        this.depositRequest = {
          pin: '',
          amount: 0
        };
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = '入金できませんでした。暗証番号、金額、または入金上限をご確認ください。';
      }
    });
  }

  private isValidDepositAmount(): boolean {
    const amount = this.depositRequest.amount;
    return amount >= 1000 && amount <= 1000000 && amount % 1000 === 0;
  }
}
