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
    this.isLoading = true;
    const idempotencyKey = this.accountService.createIdempotencyKey();

    this.accountService.withdraw(this.withdrawRequest, idempotencyKey).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = response;
        this.withdrawRequest = {
          pin: '',
          amount: 0
        };
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Rút tiền thất bại. Vui lòng kiểm tra lại PIN, số tiền hoặc số dư.';
      }
    });
  }
}
