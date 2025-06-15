import { Balance } from "./Balance.ts";
import { Account } from "./Account.ts";
export class Transaction {
  transactionId!: string
  accountId!: string
  amount!: string
  creditDebitIndicator!: string
  currency: string = 'usdt'
  status!: string
  transactionDate!: string
  transactionType: string = ''
  transactionCode?: string
  txSubCode?: string
  merchantName?: string
  merchantCategory?: string
  description?: string
  transactionReference?: string
  statementReference?: string
  bookingDateTime?: string
  valueDateTime?: string
  chargeAmount?: string
  owner?: string
  crtAt: string = new Date().toISOString()
  balance?: Balance
  creditorAccount?: Account
  debtorAccount?: Account

  constructor(init?: Partial<Transaction>) {
    Object.assign(this, init)
  }

  isCredit(): boolean {
    return this.creditDebitIndicator === 'Credit'
  }
}
