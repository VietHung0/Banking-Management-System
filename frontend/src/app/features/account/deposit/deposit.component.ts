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
    this.isLoading = true;
    const idempotencyKey = this.accountService.createIdempotencyKey();

    this.accountService.deposit(this.depositRequest, idempotencyKey).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = response;
        this.depositRequest = {
          pin: '',
          amount: 0
        };
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error || 'Nạp tiền thất bại. Vui lòng kiểm tra lại PIN hoặc số tiền.';
      }
    });
  }
}
