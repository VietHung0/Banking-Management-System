import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AmountRequest } from '../../../core/models/account.model';
import { AccountService } from '../../../core/services/account.service';

@Component({
  selector: 'app-withdraw',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './withdraw.component.html',
  styleUrl: './withdraw.component.css'
})
export class WithdrawComponent {
  withdrawRequest: AmountRequest = {
    pin: '',
    amount: 0
  };

  successMessage = '';
  errorMessage = '';
  isLoading = false;

  constructor(private accountService: AccountService) {}

  onSubmit(): void {
    if (this.isLoading) {
      return;
    }

    this.successMessage = '';
    this.errorMessage = '';

    if (!this.isValidWithdrawAmount()) {
      this.errorMessage = '出金は1,000円以上、500,000円以下、1,000円単位で入力してください。';
      return;
    }

    this.isLoading = true;
    const idempotencyKey = this.accountService.createIdempotencyKey();

    this.accountService.withdraw(this.withdrawRequest, idempotencyKey).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = '出金が完了しました。';
        this.withdrawRequest = {
          pin: '',
          amount: 0
        };
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = '出金できませんでした。暗証番号、金額、残高、または1日の上限をご確認ください。';
      }
    });
  }

  private isValidWithdrawAmount(): boolean {
    const amount = this.withdrawRequest.amount;
    return amount >= 1000 && amount <= 500000 && amount % 1000 === 0;
  }
}
