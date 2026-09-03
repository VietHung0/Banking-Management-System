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
        this.statusMessage = response.hasPin
          ? '暗証番号は登録済みです。'
          : '暗証番号は未登録です。';
        this.hasPin = response.hasPin;
      },
      error: () => {
        this.errorMessage = '暗証番号の登録状況を確認できませんでした。';
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
        this.statusMessage = '暗証番号の手続きが完了しました。';
        this.checkPin();
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = '暗証番号の手続きができませんでした。入力内容をご確認ください。';
      }
    });
  }
}
