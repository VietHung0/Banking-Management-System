export type TransactionType =
  | 'CASH_DEPOSIT'
  | 'CASH_WITHDRAWAL'
  | 'CASH_TRANSFER';

export interface Transaction {
  id: number;
  amount: number;
  transactionType: TransactionType;
  transactionDate: string;
  sourceAccountNumber: string;
  targetAccountNumber: string;
  message?: string;
}

export interface TransactionFilter {
  type?: TransactionType | '';
  fromDate?: string;
  toDate?: string;
}
