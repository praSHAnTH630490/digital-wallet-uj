package com.wallet.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Wallet {
    private int walletId;
    private int userId;
    private BigDecimal balance;
    private Timestamp createdAt;

    public Wallet() {}

    public Wallet(int walletId, int userId, BigDecimal balance, Timestamp createdAt) {
        this.walletId = walletId;
        this.userId = userId;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public int getWalletId() { return walletId; }
    public void setWalletId(int walletId) { this.walletId = walletId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
