import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, finalize, tap } from 'rxjs';

import { API_BASE_URL, TOKEN_KEY } from '../config/api.config';
import { LoginRequest, LoginResponse, RegisterRequest } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_BASE_URL}/users/login`, request).pipe(
      tap((response) => this.saveToken(response.token))
    );
  }

  register(request: RegisterRequest): Observable<string> {
    return this.http.post(`${API_BASE_URL}/users/register`, request, {
      responseType: 'text'
    });
  }

  saveToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  logout(): Observable<string> {
    return this.http.post(`${API_BASE_URL}/auth/logout`, null, {
      responseType: 'text'
    }).pipe(
      finalize(() => this.clearToken())
    );
  }

  clearToken(): void {
    localStorage.removeItem(TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    try {
      const payloadPart = token.split('.')[1];
      const normalizedPayload = payloadPart.replace(/-/g, '+').replace(/_/g, '/');
      const paddedPayload = normalizedPayload.padEnd(Math.ceil(normalizedPayload.length / 4) * 4, '=');
      const payload = JSON.parse(atob(paddedPayload));
      const isActive = typeof payload.exp === 'number' && Date.now() < payload.exp * 1000;

      if (!isActive) {
        this.clearToken();
      }

      return isActive;
    } catch {
      this.clearToken();
      return false;
    }
  }
}
