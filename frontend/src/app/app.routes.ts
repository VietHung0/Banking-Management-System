import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { DepositComponent } from './features/account/deposit/deposit.component';
import { PinComponent } from './features/account/pin/pin.component';
import { TransferComponent } from './features/account/transfer/transfer.component';
import { WithdrawComponent } from './features/account/withdraw/withdraw.component';
import { DashboardComponent } from './features/dashboard/dashboard/dashboard.component';
import { TransactionHistoryComponent } from './features/transactions/transaction-history/transaction-history.component';
import { MainLayoutComponent } from './shared/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent
      },
      {
        path: 'account/pin',
        component: PinComponent
      },
      {
        path: 'account/deposit',
        component: DepositComponent
      },
      {
        path: 'account/withdraw',
        component: WithdrawComponent
      },
      {
        path: 'account/fund-transfer',
        component: TransferComponent
      },
      {
        path: 'account/transactions',
        component: TransactionHistoryComponent
      }
    ]
  }
];
