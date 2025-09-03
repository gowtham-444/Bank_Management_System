package bankmanagement;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    private Bank bank;

    public LoginWindow(Bank bank) {
        this.bank = bank;
        initUI();
    }

    private void initUI() {
        setTitle("Bank - Login");
        setSize(380, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("User ID:");
        JTextField txtUser = new JTextField();
        JLabel lblPin = new JLabel("PIN:");
        JPasswordField txtPin = new JPasswordField();

        JButton btnAdmin = new JButton("Login as Admin");
        JButton btnCustomer = new JButton("Login as Customer");
        JButton btnRegister = new JButton("Register (Customer)");

        gbc.gridx = 0; gbc.gridy = 0; add(lblUser, gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(txtUser, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(lblPin, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(txtPin, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(btnCustomer, gbc);
        gbc.gridx = 1; gbc.gridy = 2; add(btnAdmin, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; add(btnRegister, gbc);

        // Admin login (hardcoded)
        btnAdmin.addActionListener(e -> {
            String id = txtUser.getText().trim();
            String pin = new String(txtPin.getPassword());
            if ("admin".equals(id) && "admin123".equals(pin)) {
                dispose();
                SwingUtilities.invokeLater(() -> new AdminDashboard(bank).setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials");
            }
        });

        // Customer login
        btnCustomer.addActionListener(e -> {
            String id = txtUser.getText().trim();
            String pin = new String(txtPin.getPassword());
            if (id.isEmpty() || pin.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter id and pin"); return; }
            bank.authenticateCustomer(id, pin).ifPresentOrElse(c -> {
                dispose();
                SwingUtilities.invokeLater(() -> new CustomerDashboard(bank, c).setVisible(true));
            }, () -> JOptionPane.showMessageDialog(this, "Invalid customer id / pin"));
        });

        // Register new customer (simple)
        btnRegister.addActionListener(e -> {
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField phoneField = new JTextField();
            JPasswordField pinField = new JPasswordField();
            Object[] msg = {
                    "User ID:", idField,
                    "Name:", nameField,
                    "Phone:", phoneField,
                    "PIN (4 digits):", pinField
            };
            int opt = JOptionPane.showConfirmDialog(this, msg, "Register", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                String pinVal = new String(pinField.getPassword()).trim();
                if (id.isEmpty() || name.isEmpty() || pinVal.length() < 4) {
                    JOptionPane.showMessageDialog(this, "Invalid input. Ensure PIN is at least 4 digits.");
                    return;
                }
                if (bank.findCustomerById(id) != null) {
                    JOptionPane.showMessageDialog(this, "User ID already exists.");
                    return;
                }
                bank.createCustomer(id, name, phone, pinVal);
                // create an account for the customer automatically
                Account acc = bank.createAccountForCustomer(id);
                JOptionPane.showMessageDialog(this, "Registered. Default account: " + acc.getAccountNumber());
            }
        });

        setVisible(true);
    }
}
