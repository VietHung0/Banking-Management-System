export interface PinRequest {
  pin: string;
  password: string;
}

export interface PinUpdateRequest {
  oldPin: string;
  newPin: string;
  password: string;
}

export interface AmountRequest {
  pin: string;
  amount: number;
}

export interface FundTransferRequest {
  targetAccountNumber: string;
  pin: string;
  amount: number;
}

export interface AccountResponse {
  accountNumber: string;
  balance: number;
  accountType: string;
  branch: string;
  ifscCode: string;
  accountStatus: string;
}
