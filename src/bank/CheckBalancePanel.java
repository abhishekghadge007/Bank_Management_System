package bank;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CheckBalancePanel extends JPanel {
    private JTextField tfAccNo;
    private JPasswordField tfPassword;
    private JButton btnCheck;
    private JLabel lblBalance;

    public CheckBalancePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Check Account Balance", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));

        JLabel lblAccNo = new JLabel("Account Number:");
        JLabel lblPass = new JLabel("Password:");

        tfAccNo = new JTextField();
        tfPassword = new JPasswordField();

        Dimension fieldSize = new Dimension(180, 28);
        tfAccNo.setPreferredSize(fieldSize);
        tfPassword.setPreferredSize(fieldSize);

        btnCheck = new JButton("Check Balance");
        lblBalance = new JLabel("", SwingConstants.CENTER);
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBalance.setForeground(new Color(34, 139, 34));

        // 🟣 Layout
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; add(lblAccNo, gbc);
        gbc.gridx = 1; add(tfAccNo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(lblPass, gbc);
        gbc.gridx = 1; add(tfPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnCheck, gbc);

        gbc.gridy = 4; add(lblBalance, gbc);

        btnCheck.addActionListener(e -> checkBalance());
    }

    private void checkBalance() {
        String acc = tfAccNo.getText().trim();
        String pass = new String(tfPassword.getPassword());

        if (!AuthHelper.verifyAccount(acc, pass)) {
            JOptionPane.showMessageDialog(this, "Invalid credentials");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT balance FROM accounts WHERE acc_no=?");
            ps.setString(1, acc);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                lblBalance.setText("Available Balance: ₹ " + balance);
            } else {
                JOptionPane.showMessageDialog(this, "Account not found");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}
