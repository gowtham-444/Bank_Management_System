package bankmanagement;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Bank bank = new Bank();
            new LoginWindow(bank).setVisible(true);
        });
    }
}
