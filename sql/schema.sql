-- Digital Wallet & Expense Tracker
-- Run against: mysql -h localhost -P 3307 -u root -p
-- Database name used by the app: facebook (as configured in DBConnection.java)

CREATE DATABASE IF NOT EXISTS facebook;
USE facebook;

-- ------------------------------------------------------------
-- Table 1: users
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL UNIQUE,
    password    VARCHAR(255)  NOT NULL,   -- stores BCrypt hash, not plain text
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Table 2: wallets  (one user -> one wallet)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS wallets (
    wallet_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL UNIQUE,
    balance     DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table 3: transactions
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id      INT AUTO_INCREMENT PRIMARY KEY,
    sender_wallet_id     INT DEFAULT NULL,
    receiver_wallet_id    INT DEFAULT NULL,
    amount               DECIMAL(12,2) NOT NULL,
    transaction_type     ENUM('DEPOSIT','TRANSFER','RECEIVED') NOT NULL,
    status               ENUM('SUCCESS','FAILED') NOT NULL DEFAULT 'SUCCESS',
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_sender   FOREIGN KEY (sender_wallet_id)   REFERENCES wallets(wallet_id) ON DELETE SET NULL,
    CONSTRAINT fk_txn_receiver FOREIGN KEY (receiver_wallet_id) REFERENCES wallets(wallet_id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- Table 4: expenses
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS expenses (
    expense_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    category      VARCHAR(50) NOT NULL,
    amount        DECIMAL(12,2) NOT NULL,
    description   VARCHAR(255),
    expense_date  DATE NOT NULL,
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Helpful indexes for reports / history lookups
CREATE INDEX idx_txn_sender   ON transactions(sender_wallet_id);
CREATE INDEX idx_txn_receiver ON transactions(receiver_wallet_id);
CREATE INDEX idx_expense_user_date ON expenses(user_id, expense_date);
