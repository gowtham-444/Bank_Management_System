package bankmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Bank {
    private List<Customer> customers;
    private List<Account> accounts;
    private List<Transaction> transactions;

    public Bank() {
        customers = FileHandler.loadCustomers();
        accounts = FileHandler.loadAccounts();
        transactions = FileHandler.loadTransactions();
    }

    public synchronized Customer createCustomer(String id, String name, String phone, String pin) {
        if (findCustomerById(id) != null) return null;
        Customer c = new Customer(id, name, phone, pin);
        customers.add(c);
        FileHandler.saveCustomers(customers);
        return c;
    }

    public Customer findCustomerById(String id) {
        for (Customer c : customers) if (c.getId().equals(id)) return c;
        return null;
    }

    public List<Customer> getAllCustomers() { return new ArrayList<>(customers); }

    public synchronized Account createAccountForCustomer(String customerId) {
        Customer c = findCustomerById(customerId);
        if (c == null) return null;
        String accNo = generateAccountNumber();
        Account a = new Account(accNo, customerId, 0.0);
        accounts.add(a);
        FileHandler.saveAccounts(accounts);
        return a;
    }

    private String generateAccountNumber() {
        int max = 0;
        for (Account a : accounts) {
            String digits = a.getAccountNumber().replaceAll("[^0-9]", "");
            try { int v = Integer.parseInt(digits); if (v > max) max = v; } catch (Exception ignored) {}
        }
        return "ACC" + (max + 1);
    }

    public Account findAccount(String accNo) {
        for (Account a : accounts) if (a.getAccountNumber().equals(accNo)) return a;
        return null;
    }

    public List<Account> getAccountsForCustomer(String customerId) {
        List<Account> out = new ArrayList<>();
        for (Account a : accounts) if (a.getCustomerId().equals(customerId)) out.add(a);
        return out;
    }

    public List<Account> getAllAccounts() { return new ArrayList<>(accounts); }

    public synchronized void depositAdmin(String accNo, double amount) throws BankException {
        Account a = findAccount(accNo);
        if (a == null) throw new BankException("Account not found");
        a.deposit(amount);
        transactions.add(new Transaction("ADMIN", accNo, "AdminDeposit", amount));
        persistAll();
    }

    public synchronized void deposit(String accNo, double amount) throws BankException {
        Account a = findAccount(accNo);
        if (a == null) throw new BankException("Account not found");
        a.deposit(amount);
        transactions.add(new Transaction("CASH", accNo, "Deposit", amount));
        persistAll();
    }

    public synchronized void withdraw(String accNo, double amount) throws BankException {
        Account a = findAccount(accNo);
        if (a == null) throw new BankException("Account not found");
        a.withdraw(amount);
        transactions.add(new Transaction(accNo, "CASH", "Withdraw", amount));
        persistAll();
    }

    public synchronized void transfer(String fromAcc, String toAcc, double amount) throws BankException {
        if (fromAcc.equals(toAcc)) throw new BankException("Cannot transfer to same account");
        Account from = findAccount(fromAcc);
        Account to = findAccount(toAcc);
        if (from == null) throw new BankException("Source account not found");
        if (to == null) throw new BankException("Destination account not found");
        from.withdraw(amount);
        to.deposit(amount);
        transactions.add(new Transaction(fromAcc, toAcc, "Transfer", amount));
        persistAll();
    }

    public synchronized void recharge(String fromAcc, String phone, double amount) throws BankException {
        Account from = findAccount(fromAcc);
        if (from == null) throw new BankException("Account not found");
        from.withdraw(amount);
        transactions.add(new Transaction(fromAcc, "RECHARGE:" + phone, "Recharge", amount));
        persistAll();
    }

    public List<Transaction> getAllTransactions() { return new ArrayList<>(transactions); }

    public List<Transaction> getTransactionsForAccount(String accNo) {
        List<Transaction> out = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getFromAccount().equals(accNo) || t.getToAccount().equals(accNo) ||
                    t.getToAccount().equals("RECHARGE:" + accNo) || t.getFromAccount().equals("RECHARGE:" + accNo)) {
                out.add(t);
            } else {
                if (t.getFromAccount().equals(accNo) || t.getToAccount().equals(accNo)) out.add(t);
            }
        }
        return out;
    }

    private void persistAll() {
        FileHandler.saveAccounts(accounts);
        FileHandler.saveTransactions(transactions);
        FileHandler.saveCustomers(customers);
    }

    public java.util.Optional<Customer> authenticateCustomer(String id, String pin) {
        Customer c = findCustomerById(id);
        if (c != null && c.getPin().equals(pin)) return java.util.Optional.of(c);
        return java.util.Optional.empty();
    }
}
