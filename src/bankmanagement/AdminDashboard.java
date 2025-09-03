package bankmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private Bank bank;
    private DefaultTableModel custModel, accModel, trModel;

    public AdminDashboard(Bank bank) {
        this.bank = bank;
        initUI();
    }

    private void initUI() {
        setTitle("Admin Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JToolBar toolbar = new JToolBar();
        JButton btnCreate = new JButton("Create Customer");
        JButton btnCreateAcc = new JButton("Create Account");
        JButton btnDeposit = new JButton("Deposit to Account");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnLogout = new JButton("Logout");
        toolbar.add(btnCreate);
        toolbar.add(btnCreateAcc);
        toolbar.add(btnDeposit);
        toolbar.add(btnRefresh);
        toolbar.addSeparator();
        toolbar.add(btnLogout);
        add(toolbar, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        custModel = new DefaultTableModel(new String[]{"ID","Name","Phone","PIN"}, 0);
        JTable custTable = new JTable(custModel);
        tabs.add("Customers", new JScrollPane(custTable));

        accModel = new DefaultTableModel(new String[]{"Account No","Customer ID","Balance"}, 0);
        JTable accTable = new JTable(accModel);
        tabs.add("Accounts", new JScrollPane(accTable));

        trModel = new DefaultTableModel(new String[]{"From","To","Type","Amount","Timestamp"}, 0);
        JTable trTable = new JTable(trModel);
        tabs.add("Transactions", new JScrollPane(trTable));

        add(tabs, BorderLayout.CENTER);

        btnCreate.addActionListener(e -> {
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField phoneField = new JTextField();
            JPasswordField pinField = new JPasswordField();
            Object[] msg = {"User ID:", idField, "Name:", nameField, "Phone:", phoneField, "PIN:", pinField};
            int ok = JOptionPane.showConfirmDialog(this, msg, "Create Customer", JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                String pin = new String(pinField.getPassword()).trim();
                if (id.isEmpty() || name.isEmpty() || pin.length() < 4) { JOptionPane.showMessageDialog(this, "Invalid data"); return; }
                if (bank.findCustomerById(id) != null) { JOptionPane.showMessageDialog(this, "Customer exists"); return; }
                bank.createCustomer(id, name, phone, pin);
                JOptionPane.showMessageDialog(this, "Customer created");
                refreshAll();
            }
        });

        btnCreateAcc.addActionListener(e -> {
            String custId = JOptionPane.showInputDialog(this, "Customer ID:");
            if (custId == null || custId.trim().isEmpty()) return;
            if (bank.findCustomerById(custId.trim()) == null) { JOptionPane.showMessageDialog(this, "Customer not found"); return; }
            Account acc = bank.createAccountForCustomer(custId.trim());
            JOptionPane.showMessageDialog(this, "Account created: " + acc.getAccountNumber());
            refreshAll();
        });

        btnDeposit.addActionListener(e -> {
            String accNo = JOptionPane.showInputDialog(this, "Account Number:");
            if (accNo == null || accNo.trim().isEmpty()) return;
            String amtS = JOptionPane.showInputDialog(this, "Amount to deposit:");
            if (amtS == null) return;
            try {
                double amt = Double.parseDouble(amtS.trim());
                bank.depositAdmin(accNo.trim(), amt);
                JOptionPane.showMessageDialog(this, "Deposited " + amt);
                refreshAll();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid amount");
            } catch (BankException be) {
                JOptionPane.showMessageDialog(this, be.getMessage());
            }
        });

        btnRefresh.addActionListener(e -> refreshAll());

        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginWindow(bank).setVisible(true));
        });

        refreshAll();
    }

    private void refreshAll() {
        // customers
        custModel.setRowCount(0);
        for (Customer c : bank.getAllCustomers()) custModel.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getPin()});
        // accounts
        accModel.setRowCount(0);
        for (Account a : bank.getAllAccounts()) accModel.addRow(new Object[]{a.getAccountNumber(), a.getCustomerId(), a.getBalance()});
        // transactions
        trModel.setRowCount(0);
        for (Transaction t : bank.getAllTransactions()) trModel.addRow(new Object[]{t.getFromAccount(), t.getToAccount(), t.getType(), t.getAmount(), t.getTimestamp().toString()});
    }
}
