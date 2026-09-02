import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config/api.config';
import {
  AmountRequest,
  FundTransferRequest,
  PinRequest,
  PinStatusResponse,
  PinUpdateRequest,
  RecipientResponse
} from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  constructor(private http: HttpClient) {}

  checkPin(): Observable<PinStatusResponse> {
    return this.http.get<PinStatusResponse>(`${API_BASE_URL}/account/pin/check`);
  }

  createPin(request: PinRequest): Observable<string> {
    return this.http.post(`${API_BASE_URL}/account/pin/create`, request, {
      responseType: 'text'
    });
  }

  updatePin(request: PinUpdateRequest): Observable<string> {
    return this.http.post(`${API_BASE_URL}/account/pin/update`, request, {
      responseType: 'text'
    });
  }

  deposit(request: AmountRequest, idempotencyKey: string): Observable<string> {
    return this.http.post(`${API_BASE_URL}/account/deposit`, request, {
      headers: this.createIdempotencyHeaders(idempotencyKey),
      responseType: 'text'
    });
  }

  withdraw(request: AmountRequest, idempotencyKey: string): Observable<string> {
    return this.http.post(`${API_BASE_URL}/account/withdraw`, request, {
      headers: this.createIdempotencyHeaders(idempotencyKey),
      responseType: 'text'
    });
  }

  transfer(request: FundTransferRequest, idempotencyKey: string): Observable<string> {
    return this.http.post(`${API_BASE_URL}/account/fund-transfer`, request, {
      headers: this.createIdempotencyHeaders(idempotencyKey),
      responseType: 'text'
    });
  }

  getRecipient(accountNumber: string): Observable<RecipientResponse> {
    return this.http.get<RecipientResponse>(`${API_BASE_URL}/account/recipient`, {
      params: { accountNumber }
    });
  }

  createIdempotencyKey(): string {
    if (typeof crypto !== 'undefined' && 'randomUUID' in crypto) {
      return crypto.randomUUID();
    }

    return `${Date.now()}-${Math.random().toString(16).slice(2)}`;
  }

  private createIdempotencyHeaders(idempotencyKey: string): HttpHeaders {
    return new HttpHeaders({
      'Idempotency-Key': idempotencyKey
    });
  }
}
