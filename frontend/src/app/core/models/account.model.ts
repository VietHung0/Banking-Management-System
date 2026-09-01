export interface PinRequest {
  pin: string;
  password: string;
}

export interface PinUpdateRequest {
  oldPin: string;
  newPin: string;
  password: string;
}

export interface PinStatusResponse {
  hasPin: boolean;
  message: string;
}

export interface AmountRequest {
  pin: string;
  amount: number;
}

export interface FundTransferRequest {
  targetAccountNumber: string;
  pin: string;
  amount: number;
  message: string;
}

export interface RecipientResponse {
  accountNumber: string;
  name: string;
}

export interface AccountResponse {
  accountNumber: string;
  balance: number;
  accountType: string;
  bankCode: string;
  bankAddress: string;
  branch: string;
  branchCode: string;
  accountStatus: string;
}
