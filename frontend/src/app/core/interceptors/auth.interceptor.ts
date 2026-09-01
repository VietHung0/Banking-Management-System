import { HttpInterceptorFn } from '@angular/common/http';

import { TOKEN_KEY } from '../config/api.config';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
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

  return next(authRequest);
};
