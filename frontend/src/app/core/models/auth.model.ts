export interface LoginRequest {
  identifier: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface RegisterRequest {
  name: string;
  password: string;
  email: string;
  countryCode: string;
  phoneNumber: string;
  address: string;
}
