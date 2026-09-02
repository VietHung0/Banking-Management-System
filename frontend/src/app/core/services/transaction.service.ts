import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config/api.config';
import { Transaction, TransactionFilter } from '../models/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  constructor(private http: HttpClient) {}

  getTransactions(filter?: TransactionFilter): Observable<Transaction[]> {
    const params: Record<string, string> = {};

    if (filter?.type) {
      params['type'] = filter.type;
    }

    if (filter?.fromDate) {
      params['fromDate'] = filter.fromDate;
    }

    if (filter?.toDate) {
      params['toDate'] = filter.toDate;
    }

    return this.http.get<Transaction[]>(`${API_BASE_URL}/account/transactions`, { params });
  }
}
