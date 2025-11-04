package bank;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class WithdrawPanel extends JPanel {
    private JTextField tfAccNo, tfAmount;
    private JPasswordField tfPassword;
    private JButton btnWithdraw;

    public WithdrawPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 🟢 Title Label
        JLabel lblTitle = new JLabel("Withdraw Your Money", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));

        JLabel lblAccNo = new JLabel("Account Number:");
        JLabel lblPass = new JLabel("Password:");
        JLabel lblAmount = new JLabel("Amount:");

        tfAccNo = new JTextField();
        tfPassword = new JPasswordField();
        tfAmount = new JTextField();

        Dimension fieldSize = new Dimension(180, 28);
        tfAccNo.setPreferredSize(fieldSize);
        tfPassword.setPreferredSize(fieldSize);
        tfAmount.setPreferredSize(fieldSize);

        btnWithdraw = new JButton("Withdraw");

        // 🟣 Add components with positioning
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(lblTitle, gbc); // ✅ Title added at top center

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; add(lblAccNo, gbc);
        gbc.gridx = 1; add(tfAccNo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(lblPass, gbc);
        gbc.gridx = 1; add(tfPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(lblAmount, gbc);
        gbc.gridx = 1; add(tfAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnWithdraw, gbc);

        // 🟢 Button Action
        btnWithdraw.addActionListener(e -> withdrawMoney());
    }

    private void withdrawMoney() {
        String acc = tfAccNo.getText().trim();
        String pass = new String(tfPassword.getPassword());
        double amount;

        try {
            amount = Double.parseDouble(tfAmount.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount");
            return;
        }

        if (!AuthHelper.verifyAccount(acc, pass)) {
            JOptionPane.showMessageDialog(this, "Invalid credentials");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            double balance = 0;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT balance FROM accounts WHERE acc_no=?")) {
                ps.setString(1, acc);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) balance = rs.getDouble(1);
            }

            if (balance < amount) {
                JOptionPane.showMessageDialog(this, "Insufficient balance");
                return;
            }

            try (PreparedStatement ps1 = con.prepareStatement(
                    "UPDATE accounts SET balance = balance - ? WHERE acc_no=?")) {
                ps1.setDouble(1, amount);
                ps1.setString(2, acc);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = con.prepareStatement(
                    "INSERT INTO transactions (acc_no, type, amount) VALUES (?, 'WITHDRAW', ?)")) {
                ps2.setString(1, acc);
                ps2.setDouble(2, amount);
                ps2.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(this, "Withdrawal successful!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}
