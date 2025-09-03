package bankmanagement;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String CUSTOMER_FILE = "customers.txt";
    private static final String ACCOUNT_FILE = "accounts.txt";
    private static final String TRANSACTION_FILE = "transactions.txt";

    /* Customers */
    public static List<Customer> loadCustomers() {
        List<Customer> list = new ArrayList<>();
        File f = new File(CUSTOMER_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", 4);
                if (p.length == 4) list.add(new Customer(p[0], p[1], p[2], p[3]));
            }
        } catch (IOException ignored) {}
        return list;
    }

    public static void saveCustomers(List<Customer> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CUSTOMER_FILE))) {
            for (Customer c : list) bw.write(c.toString() + System.lineSeparator());
        } catch (IOException e) { e.printStackTrace(); }
    }

    /* Accounts */
    public static List<Account> loadAccounts() {
        List<Account> list = new ArrayList<>();
        File f = new File(ACCOUNT_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", 3);
                if (p.length == 3) {
                    double bal = 0.0;
                    try { bal = Double.parseDouble(p[2]); } catch (Exception ex) {}
                    list.add(new Account(p[0], p[1], bal));
                }
            }
        } catch (IOException ignored) {}
        return list;
    }

    public static void saveAccounts(List<Account> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ACCOUNT_FILE))) {
            for (Account a : list) bw.write(a.toString() + System.lineSeparator());
        } catch (IOException e) { e.printStackTrace(); }
    }

    /* Transactions */
    public static List<Transaction> loadTransactions() {
        List<Transaction> list = new ArrayList<>();
        File f = new File(TRANSACTION_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                // from,to,type,amount,timestamp
                String[] p = line.split(",", 5);
                if (p.length == 5) {
                    double amt = 0.0;
                    try { amt = Double.parseDouble(p[3]); } catch (Exception ex) {}
                    try {
                        list.add(new Transaction(p[0], p[1], p[2], amt, LocalDateTime.parse(p[4])));
                    } catch (Exception ex) {
                        list.add(new Transaction(p[0], p[1], p[2], amt));
                    }
                }
            }
        } catch (IOException ignored) {}
        return list;
    }

    public static void saveTransactions(List<Transaction> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANSACTION_FILE))) {
            for (Transaction t : list) bw.write(t.toString() + System.lineSeparator());
        } catch (IOException e) { e.printStackTrace(); }
    }
}
