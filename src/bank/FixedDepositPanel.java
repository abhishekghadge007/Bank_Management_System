package bank;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FixedDepositPanel extends JPanel {
    private JTextField tfAccNo, tfPrincipal, tfRate, tfTenure, tfReturn;
    private JPasswordField tfPassword;
    private JButton btnCreate;

    public FixedDepositPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // === Title ===
        JLabel lblTitle = new JLabel("Fixed Deposit", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lblTitle, gbc);

        gbc.gridwidth = 1;

        // === Labels ===
        JLabel lblAccNo = new JLabel("Account Number:");
        JLabel lblPass = new JLabel("Password:");
        JLabel lblPrincipal = new JLabel("Principal Amount (₹):");
        JLabel lblRate = new JLabel("Interest Rate (% per year):");
        JLabel lblTenure = new JLabel("Tenure (months):");
        JLabel lblReturn = new JLabel("Expected Return (₹):");

        // === Fields ===
        tfAccNo = new JTextField();
        tfPassword = new JPasswordField();
        tfPrincipal = new JTextField();
        tfRate = new JTextField();
        tfTenure = new JTextField();
        tfReturn = new JTextField();
        tfReturn.setEditable(false);
        tfReturn.setBackground(Color.LIGHT_GRAY);

        // Smaller input fields
        Dimension fieldSize = new Dimension(180, 28);
        tfAccNo.setPreferredSize(fieldSize);
        tfPassword.setPreferredSize(fieldSize);
        tfPrincipal.setPreferredSize(fieldSize);
        tfRate.setPreferredSize(fieldSize);
        tfTenure.setPreferredSize(fieldSize);
        tfReturn.setPreferredSize(fieldSize);

        // === Button ===
        btnCreate = new JButton("Create Fixed Deposit");

        // === Layout arrangement ===
        gbc.gridx = 0; gbc.gridy = 1; add(lblAccNo, gbc);
        gbc.gridx = 1; add(tfAccNo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(lblPass, gbc);
        gbc.gridx = 1; add(tfPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(lblPrincipal, gbc);
        gbc.gridx = 1; add(tfPrincipal, gbc);

        gbc.gridx = 0; gbc.gridy = 4; add(lblRate, gbc);
        gbc.gridx = 1; add(tfRate, gbc);

        gbc.gridx = 0; gbc.gridy = 5; add(lblTenure, gbc);
        gbc.gridx = 1; add(tfTenure, gbc);

        gbc.gridx = 0; gbc.gridy = 6; add(lblReturn, gbc);
        gbc.gridx = 1; add(tfReturn, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnCreate, gbc);

        // === Event Listeners ===
        tfPrincipal.addCaretListener(e -> calculateReturn());
        tfRate.addCaretListener(e -> calculateReturn());
        tfTenure.addCaretListener(e -> calculateReturn());
        btnCreate.addActionListener(e -> createFD());
    }

    // === Calculate Expected Return ===
    private void calculateReturn() {
        try {
            double principal = Double.parseDouble(tfPrincipal.getText().trim());
            double rate = Double.parseDouble(tfRate.getText().trim());
            int tenure = Integer.parseInt(tfTenure.getText().trim());

            // Convert months to years
            double time = tenure / 12.0;
            // Compound interest (quarterly compounding assumed)
            double maturity = principal * Math.pow(1 + (rate / (4 * 100)), 4 * time);

            tfReturn.setText(String.format("%.2f", maturity));
        } catch (Exception ex) {
            tfReturn.setText("");
        }
    }

    // === Create Fixed Deposit in DB ===
    private void createFD() {
        String acc = tfAccNo.getText().trim();
        String pass = new String(tfPassword.getPassword());
        double principal, rate;
        int tenure;

        try {
            principal = Double.parseDouble(tfPrincipal.getText().trim());
            rate = Double.parseDouble(tfRate.getText().trim());
            tenure = Integer.parseInt(tfTenure.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid numeric value");
            return;
        }

        if (!AuthHelper.verifyAccount(acc, pass)) {
            JOptionPane.showMessageDialog(this, "Invalid account or password");
            return;
        }

        double maturity = 0;
        double time = tenure / 12.0;
        maturity = principal * Math.pow(1 + (rate / (4 * 100)), 4 * time);

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO fixed_deposit (acc_no, principal, rate, tenure_months) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, acc);
                ps.setDouble(2, principal);
                ps.setDouble(3, rate);
                ps.setInt(4, tenure);
                ps.executeUpdate();
            }

            try (PreparedStatement ps2 = con.prepareStatement(
                    "INSERT INTO transactions (acc_no, type, amount) VALUES (?, 'FD_CREATE', ?)")) {
                ps2.setString(1, acc);
                ps2.setDouble(2, principal);
                ps2.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(this,
                    "Fixed Deposit created successfully!\nExpected Maturity Amount: ₹" + String.format("%.2f", maturity));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
