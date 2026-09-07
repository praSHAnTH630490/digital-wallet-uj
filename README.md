# Digital Wallet & Expense Tracker

Java 17 + JSP/Servlet (Jakarta EE, Tomcat 10) + MySQL + Maven + JDBC.

## Stack
| Layer | Choice |
|---|---|
| Language | Java 17 |
| Web | JSP + Servlets (`jakarta.servlet.*` — required for Tomcat 10) |
| Database | MySQL, `localhost:3307/facebook`, user `root` / pass `root` |
| Build | Maven (packages as `.war`) |
| Server | Apache Tomcat 10 |
| Password hashing | jBCrypt (plain-text passwords are never stored) |

## 1. Create the database
```bash
mysql -h localhost -P 3307 -u root -p < sql/schema.sql
```
This creates the `facebook` database with `users`, `wallets`, `transactions`, and `expenses`.

## 2. Build the WAR
```bash
mvn clean package
```
Produces `target/DigitalWallet.war`.

## 3. Deploy to Tomcat 10
Copy the WAR into Tomcat's `webapps/` folder (or deploy it as an exploded folder from
your IDE), then start Tomcat. Visit:
```
http://localhost:8080/DigitalWallet/
```

## Project layout
```
src/main/java/com/wallet/
  model/      User, Wallet, Transaction, Expense
  dao/        UserDAO, WalletDAO, TransactionDAO, ExpenseDAO
  servlet/    RegisterServlet, LoginServlet, LogoutServlet, AuthFilter,
              DashboardServlet, AddMoneyServlet, TransferServlet,
              ExpenseServlet, HistoryServlet
  util/       DBConnection

src/main/webapp/
  index.jsp, login.jsp, register.jsp, dashboard.jsp,
  add-money.jsp, transfer.jsp, expenses.jsp, history.jsp
  css/style.css
  WEB-INF/web.xml
```

## How the transfer transaction works (the core JDBC concept)
`WalletDAO.transfer()`:
1. `conn.setAutoCommit(false)`
2. Locks both wallets with `SELECT ... FOR UPDATE` (prevents a race where two
   transfers read the same stale balance)
3. Checks the sender has enough balance
4. Debits the sender
5. Credits the receiver
6. Inserts the `transactions` row
7. `conn.commit()` — **only if every step above succeeded**

If any step throws a `SQLException`, the `catch` block calls `conn.rollback()`,
so the debit and credit never happen "halfway" — either all three writes land,
or none of them do.

## Interview-ready talking point
> "I used JDBC transaction management. I disabled auto-commit, performed the
> debit, credit, and transaction-record operations, and committed only when
> all operations succeeded. If any operation failed, I rolled back the entire
> transaction. I also used `SELECT ... FOR UPDATE` to lock the wallet rows so
> two simultaneous transfers can't read the same stale balance."

## Notes / possible extensions
- Passwords are hashed with BCrypt (`jbcrypt`), not stored in plain text.
- `AuthFilter` guards every `/app/*` route so only logged-in users can reach
  the dashboard, transfer, add-money, expenses, and history pages.
- Category and monthly expense reports use SQL `GROUP BY` + `SUM()`.
- Not yet built (left for you to extend): pagination on history/expenses,
  CSV export of the monthly report, email verification on registration.
