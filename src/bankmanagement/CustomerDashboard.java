package bankmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerDashboard extends JFrame {
    private Bank bank;
    private Customer customer;
    private DefaultTableModel accModel;
    private DefaultTableModel trModel;

    public CustomerDashboard(Bank bank, Customer customer) {
        this.bank = bank;
        this.customer = customer;
        initUI();
    }

    private void initUI() {
        setTitle("Customer Dashboard - " + customer.getName());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Welcome, " + customer.getName() + " (ID: " + customer.getId() + ")"));

        JButton btnDeposit = new JButton("Deposit");
        JButton btnWithdraw = new JButton("Withdraw");
        JButton btnTransfer = new JButton("Send Money");
        JButton btnRecharge = new JButton("Recharge (mobile)");
        JButton btnViewTr = new JButton("View Transactions for Selected Acc");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnLogout = new JButton("Logout");

        top.add(btnDeposit);
        top.add(btnWithdraw);
        top.add(btnTransfer);
        top.add(btnRecharge);
        top.add(btnViewTr);
        top.add(btnRefresh);
        top.add(btnLogout);

        add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        accModel = new DefaultTableModel(new String[]{"Account No","Balance"}, 0);
        JTable accTable = new JTable(accModel);
        tabs.add("My Accounts", new JScrollPane(accTable));

        trModel = new DefaultTableModel(new String[]{"From","To","Type","Amount","Timestamp"}, 0);
        JTable trTable = new JTable(trModel);
        tabs.add("Transactions", new JScrollPane(trTable));

        add(tabs, BorderLayout.CENTER);

        btnDeposit.addActionListener(e -> {
            int sel = accTable.getSelectedRow();
            if (sel < 0) { JOptionPane.showMessageDialog(this, "Select an account"); return; }
            String accNo = accModel.getValueAt(sel, 0).toString();
            String amtS = JOptionPane.showInputDialog(this, "Amount to deposit:");
            if (amtS == null) return;
            try {
                double amt = Double.parseDouble(amtS.trim());
                String pin = JOptionPane.showInputDialog(this, "Enter PIN to confirm:");
                if (!customer.getPin().equals(pin)) { JOptionPane.showMessageDialog(this, "Invalid PIN"); return; }
                bank.deposit(accNo, amt);
                JOptionPane.showMessageDialog(this, "Deposited " + amt);
                refreshAccounts();
            } catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(this, "Invalid amount"); }
            catch (BankException be) { JOptionPane.showMessageDialog(this, be.getMessage()); }
        });

        btnWithdraw.addActionListener(e -> {
            int sel = accTable.getSelectedRow();
            if (sel < 0) { JOptionPane.showMessageDialog(this, "Select an account"); return; }
            String accNo = accModel.getValueAt(sel, 0).toString();
            String amtS = JOptionPane.showInputDialog(this, "Amount to withdraw:");
            if (amtS == null) return;
            try {
                double amt = Double.parseDouble(amtS.trim());
                String pin = JOptionPane.showInputDialog(this, "Enter PIN to confirm:");
                if (!customer.getPin().equals(pin)) { JOptionPane.showMessageDialog(this, "Invalid PIN"); return; }
                bank.withdraw(accNo, amt);
                JOptionPane.showMessageDialog(this, "Withdrawn " + amt);
                refreshAccounts();
            } catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(this, "Invalid amount"); }
            catch (BankException be) { JOptionPane.showMessageDialog(this, be.getMessage()); }
        });

        btnTransfer.addActionListener(e -> {
            int sel = accTable.getSelectedRow();
            if (sel < 0) { JOptionPane.showMessageDialog(this, "Select your source account"); return; }
            String fromAcc = accModel.getValueAt(sel, 0).toString();
            String toAcc = JOptionPane.showInputDialog(this, "Enter destination account number:");
            if (toAcc == null || toAcc.trim().isEmpty()) return;
            String amtS = JOptionPane.showInputDialog(this, "Amount to send:");
            if (amtS == null) return;
            try {
                double amt = Double.parseDouble(amtS.trim());
                String pin = JOptionPane.showInputDialog(this, "Enter PIN to confirm:");
                if (!customer.getPin().equals(pin)) { JOptionPane.showMessageDialog(this, "Invalid PIN"); return; }
                bank.transfer(fromAcc, toAcc.trim(), amt);
                JOptionPane.showMessageDialog(this, "Transferred " + amt + " to " + toAcc);
                refreshAccounts();
            } catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(this, "Invalid amount"); }
            catch (BankException be) { JOptionPane.showMessageDialog(this, be.getMessage()); }
        });

        btnRecharge.addActionListener(e -> {
            int sel = accTable.getSelectedRow();
            if (sel < 0) { JOptionPane.showMessageDialog(this, "Select your account"); return; }
            String fromAcc = accModel.getValueAt(sel, 0).toString();
            String phone = JOptionPane.showInputDialog(this, "Enter phone number to recharge:");
            if (phone == null || phone.trim().isEmpty()) return;
            String amtS = JOptionPane.showInputDialog(this, "Amount to recharge:");
            if (amtS == null) return;
            try {
                double amt = Double.parseDouble(amtS.trim());
                String pin = JOptionPane.showInputDialog(this, "Enter PIN to confirm:");
                if (!customer.getPin().equals(pin)) { JOptionPane.showMessageDialog(this, "Invalid PIN"); return; }
                bank.recharge(fromAcc, phone.trim(), amt);
                JOptionPane.showMessageDialog(this, "Recharged " + phone + " with " + amt);
                refreshAccounts();
            } catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(this, "Invalid amount"); }
            catch (BankException be) { JOptionPane.showMessageDialog(this, be.getMessage()); }
        });

        btnViewTr.addActionListener(e -> {
            int sel = accTable.getSelectedRow();
            if (sel < 0) { JOptionPane.showMessageDialog(this, "Select an account"); return; }
            String accNo = accModel.getValueAt(sel, 0).toString();
            refreshTransactionsForAccount(accNo);
        });

        btnRefresh.addActionListener(e -> {
            refreshAccounts();
            trModel.setRowCount(0);
        });

        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginWindow(bank).setVisible(true));
        });

        refreshAccounts();
    }

    private void refreshAccounts() {
        accModel.setRowCount(0);
        for (Account a : bank.getAccountsForCustomer(customer.getId())) accModel.addRow(new Object[]{a.getAccountNumber(), a.getBalance()});
    }

    private void refreshTransactionsForAccount(String accNo) {
        trModel.setRowCount(0);
        for (Transaction t : bank.getTransactionsForAccount(accNo)) {
            trModel.addRow(new Object[]{t.getFromAccount(), t.getToAccount(), t.getType(), t.getAmount(), t.getTimestamp().toString()});
        }
    }
}
