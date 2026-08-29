import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config/api.config';
import { AccountResponse } from '../models/account.model';
import { UserResponse } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  constructor(private http: HttpClient) {}

  getUser(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${API_BASE_URL}/dashboard/user`);
  }

  getAccount(): Observable<AccountResponse> {
    return this.http.get<AccountResponse>(`${API_BASE_URL}/dashboard/account`);
  }
}
