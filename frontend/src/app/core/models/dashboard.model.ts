export interface UserResponse {
  name: string;
  email: string;
  countryCode: string;
  phoneNumber: string;
  address: string;
  accountNumber: string;
  bankName: string;
  bankCode: string;
  bankAddress: string;
  branchCode: string;
  branch: string;
  accountType: string;
}

export interface UpdateUserRequest {
  name: string;
  countryCode: string;
  phoneNumber: string;
  address: string;
}
