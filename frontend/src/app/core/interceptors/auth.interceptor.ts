import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { TOKEN_KEY } from '../config/api.config';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const router = inject(Router);
  const isPublicAuthRequest =
    request.url.includes('/users/login') ||
    request.url.includes('/users/register') ||
    request.url.includes('/auth/login');

  if (isPublicAuthRequest) {
    return next(request);
  }

  const token = localStorage.getItem(TOKEN_KEY);

  if (!token) {
    return next(request);
  }

  const authRequest = request.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authRequest).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 || error.status === 403) {
        localStorage.removeItem(TOKEN_KEY);
        void router.navigate(['/login']);
      }

      return throwError(() => error);
    })
  );
};
