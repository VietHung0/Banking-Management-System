import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config/api.config';
import { UpdateUserRequest, UserResponse } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient) {}

  updateProfile(request: UpdateUserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${API_BASE_URL}/users/update`, request);
  }
}
