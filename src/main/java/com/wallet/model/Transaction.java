package com.wallet.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private int transactionId;
    private Integer senderWalletId;   // nullable (e.g. pure deposit has no sender)
    private Integer receiverWalletId; // nullable (rare, but kept nullable for symmetry)
    private BigDecimal amount;
    private String transactionType; // DEPOSIT / TRANSFER / RECEIVED
    private String status;          // SUCCESS / FAILED
    private Timestamp createdAt;

    // extra display-only fields populated by JOINs (not columns on the table)
    private String counterpartyName;

    public Transaction() {}

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public Integer getSenderWalletId() { return senderWalletId; }
    public void setSenderWalletId(Integer senderWalletId) { this.senderWalletId = senderWalletId; }

    public Integer getReceiverWalletId() { return receiverWalletId; }
    public void setReceiverWalletId(Integer receiverWalletId) { this.receiverWalletId = receiverWalletId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getCounterpartyName() { return counterpartyName; }
    public void setCounterpartyName(String counterpartyName) { this.counterpartyName = counterpartyName; }
}
