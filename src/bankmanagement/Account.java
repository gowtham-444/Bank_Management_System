package bankmanagement;

public class Account {
    private String accountNumber;
    private String customerId;
    private double balance;

    public Account(String accountNumber, String customerId, double balance) {
        this.accountNumber = accountNumber.trim();
        this.customerId = customerId.trim();
        this.balance = balance;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getCustomerId() { return customerId; }
    public double getBalance() { return balance; }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance += amount;
    }

    public void withdraw(double amount) throws BankException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount > balance) throw new BankException("Insufficient funds");
        balance -= amount;
    }

    @Override
    public String toString() {
        return accountNumber + "," + customerId + "," + balance;
    }
}
