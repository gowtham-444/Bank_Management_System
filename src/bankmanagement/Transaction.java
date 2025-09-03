package bankmanagement;

import java.time.LocalDateTime;

public class Transaction {
    private String fromAccount; // can be "BANK" or account no
    private String toAccount;   // can be "RECHARGE:PHONE" or account no
    private String type;        // Deposit, Withdraw, Transfer, Recharge, AdminDeposit
    private double amount;
    private LocalDateTime timestamp;

    public Transaction(String fromAccount, String toAccount, String type, double amount) {
        this.fromAccount = fromAccount == null ? "" : fromAccount.trim();
        this.toAccount = toAccount == null ? "" : toAccount.trim();
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    // used when loading from file (timestamp passed as string parse)
    public Transaction(String fromAccount, String toAccount, String type, double amount, LocalDateTime ts) {
        this.fromAccount = fromAccount.trim();
        this.toAccount = toAccount.trim();
        this.type = type;
        this.amount = amount;
        this.timestamp = ts;
    }

    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        // CSV: from,to,type,amount,timestamp
        return fromAccount + "," + toAccount + "," + type + "," + amount + "," + timestamp.toString();
    }
}
