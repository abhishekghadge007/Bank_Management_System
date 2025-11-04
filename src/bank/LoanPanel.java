package bank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class LoanPanel extends JPanel {
    private JTextField tfAcc = new JTextField(20);
    private JPasswordField tfPass = new JPasswordField(20);
    private JTextField tfPrincipal = new JTextField(20);
    private JTextField tfRate = new JTextField(20);
    private JTextField tfTenure = new JTextField(20);
    private JTextField tfViewAcc = new JTextField(20);
    private JTextField tfLoanId = new JTextField(10);
    private JTable table = new JTable();
    private DefaultTableModel model;

    public LoanPanel() {
        setLayout(null);

        JLabel title = new JLabel("Loan Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        title.setBounds(0, 10, 960, 30);
        add(title);

        int lx = 360, fx = 500;

        // === Apply Loan ===
        JLabel l1 = new JLabel("Account No:");
        l1.setBounds(lx, 80, 120, 25);
        add(l1);
        tfAcc.setBounds(fx, 80, 160, 25);
        add(tfAcc);

        JLabel l2 = new JLabel("Password:");
        l2.setBounds(lx, 120, 120, 25);
        add(l2);
        tfPass.setBounds(fx, 120, 160, 25);
        add(tfPass);

        JLabel l3 = new JLabel("Principal Amount (₹):");
        l3.setBounds(lx, 160, 160, 25);
        add(l3);
        tfPrincipal.setBounds(fx, 160, 160, 25);
        add(tfPrincipal);

        JLabel l4 = new JLabel("Interest Rate (%):");
        l4.setBounds(lx, 200, 160, 25);
        add(l4);
        tfRate.setBounds(fx, 200, 160, 25);
        add(tfRate);

        JLabel l5 = new JLabel("Tenure (months):");
        l5.setBounds(lx, 240, 160, 25);
        add(l5);
        tfTenure.setBounds(fx, 240, 160, 25);
        add(tfTenure);

        JButton btnApply = new JButton("Apply Loan");
        btnApply.setBounds(fx, 280, 160, 30);
        add(btnApply);

        // === View Loans ===
        JLabel lv = new JLabel("View Loans (Acc No):");
        lv.setBounds(lx, 330, 160, 25);
        add(lv);
        tfViewAcc.setBounds(fx, 330, 160, 25);
        add(tfViewAcc);

        JButton btnView = new JButton("View Loans");
        btnView.setBounds(fx, 370, 160, 30);
        add(btnView);

        // === Loan Table ===
        model = new DefaultTableModel(new String[]{
                "ID", "Acc No", "Loan Type", "Principal", "Rate", "Tenure", "EMI", "Outstanding", "Status"
        }, 0);
        table.setModel(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 420, 920, 200);
        add(sp);

        // === Pay EMI Section ===
        JLabel lblPayTitle = new JLabel("Pay Loan EMI");
        lblPayTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPayTitle.setForeground(new Color(0, 128, 0));
        lblPayTitle.setBounds(20, 640, 200, 25);
        add(lblPayTitle);

        JLabel lblLoanId = new JLabel("Loan ID:");
        lblLoanId.setBounds(20, 680, 100, 25);
        add(lblLoanId);
        tfLoanId.setBounds(100, 680, 100, 25);
        add(tfLoanId);

        JLabel lblPayAcc = new JLabel("Account No:");
        lblPayAcc.setBounds(220, 680, 100, 25);
        add(lblPayAcc);
        JTextField tfPayAcc = new JTextField();
        tfPayAcc.setBounds(320, 680, 120, 25);
        add(tfPayAcc);

        JLabel lblPayPass = new JLabel("Password:");
        lblPayPass.setBounds(460, 680, 100, 25);
        add(lblPayPass);
        JPasswordField tfPayPass = new JPasswordField();
        tfPayPass.setBounds(560, 680, 120, 25);
        add(tfPayPass);

        JButton btnPayEMI = new JButton("Pay EMI");
        btnPayEMI.setBounds(700, 680, 120, 30);
        add(btnPayEMI);

        // === Button Actions ===
        btnApply.addActionListener((ActionEvent e) -> createLoan());
        btnView.addActionListener((ActionEvent e) -> viewLoans());
        btnPayEMI.addActionListener((ActionEvent e) -> payLoanEMI(tfLoanId.getText().trim(), tfPayAcc.getText().trim(), new String(tfPayPass.getPassword())));
    }

    // === Create Loan ===
    private void createLoan() {
        String acc = tfAcc.getText().trim();
        String pass = new String(tfPass.getPassword()).trim();
        double principal, rate;
        int tenure;

        try {
            principal = Double.parseDouble(tfPrincipal.getText().trim());
            rate = Double.parseDouble(tfRate.getText().trim());
            tenure = Integer.parseInt(tfTenure.getText().trim());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid numeric values");
            return;
        }

        if (!AuthHelper.verifyAccount(acc, pass)) {
            JOptionPane.showMessageDialog(this, "Invalid account or password");
            return;
        }

        double monthlyRate = rate / (12 * 100);
        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenure)) /
                     (Math.pow(1 + monthlyRate, tenure) - 1);

        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO loans (acc_no, loan_type, principal, interest_rate, tenure_months, monthly_emi, outstanding) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, acc);
                ps.setString(2, "Personal Loan");
                ps.setDouble(3, principal);
                ps.setDouble(4, rate);
                ps.setInt(5, tenure);
                ps.setDouble(6, emi);
                ps.setDouble(7, principal);
                ps.executeUpdate();
            }

            String tx = "INSERT INTO transactions (acc_no, type, amount) VALUES (?, 'LOAN_APPROVED', ?)";
            try (PreparedStatement ps2 = con.prepareStatement(tx)) {
                ps2.setString(1, acc);
                ps2.setDouble(2, principal);
                ps2.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Loan Approved!\nMonthly EMI: ₹" + String.format("%.2f", emi));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating loan: " + ex.getMessage());
        }
    }

    // === View Loans ===
    private void viewLoans() {
        model.setRowCount(0);
        String acc = tfViewAcc.getText().trim();

        String sel = "SELECT id, acc_no, loan_type, principal, interest_rate, tenure_months, monthly_emi, outstanding, status FROM loans WHERE acc_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sel)) {
            ps.setString(1, acc);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("acc_no"),
                            rs.getString("loan_type"),
                            rs.getDouble("principal"),
                            rs.getDouble("interest_rate"),
                            rs.getInt("tenure_months"),
                            rs.getDouble("monthly_emi"),
                            rs.getDouble("outstanding"),
                            rs.getString("status")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading loans: " + ex.getMessage());
        }
    }

    // === Pay EMI ===
    private void payLoanEMI(String loanIdStr, String accNo, String password) {
        if (loanIdStr.isEmpty() || accNo.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required");
            return;
        }

        if (!AuthHelper.verifyAccount(accNo, password)) {
            JOptionPane.showMessageDialog(this, "Invalid account or password");
            return;
        }

        int loanId;
        try {
            loanId = Integer.parseInt(loanIdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Loan ID");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            PreparedStatement ps1 = con.prepareStatement("SELECT monthly_emi, outstanding FROM loans WHERE id = ? AND acc_no = ?");
            ps1.setInt(1, loanId);
            ps1.setString(2, accNo);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Loan not found");
                return;
            }

            double emi = rs.getDouble("monthly_emi");
            double outstanding = rs.getDouble("outstanding");

            PreparedStatement psBal = con.prepareStatement("SELECT balance FROM accounts WHERE acc_no = ?");
            psBal.setString(1, accNo);
            ResultSet rsBal = psBal.executeQuery();
            rsBal.next();
            double balance = rsBal.getDouble("balance");

            if (balance < emi) {
                JOptionPane.showMessageDialog(this, "Insufficient balance to pay EMI");
                return;
            }

            double newOutstanding = outstanding - emi;
            double newBalance = balance - emi;

            PreparedStatement psUpdAcc = con.prepareStatement("UPDATE accounts SET balance = ? WHERE acc_no = ?");
            psUpdAcc.setDouble(1, newBalance);
            psUpdAcc.setString(2, accNo);
            psUpdAcc.executeUpdate();

            String newStatus = (newOutstanding <= 0) ? "Closed" : "Active";

            PreparedStatement psUpdLoan = con.prepareStatement("UPDATE loans SET outstanding = ?, status = ? WHERE id = ?");
            psUpdLoan.setDouble(1, Math.max(0, newOutstanding));
            psUpdLoan.setString(2, newStatus);
            psUpdLoan.setInt(3, loanId);
            psUpdLoan.executeUpdate();

            PreparedStatement psTx = con.prepareStatement("INSERT INTO transactions (acc_no, type, amount) VALUES (?, 'LOAN_EMI_PAID', ?)");
            psTx.setString(1, accNo);
            psTx.setDouble(2, emi);
            psTx.executeUpdate();

            con.commit();
            JOptionPane.showMessageDialog(this, "EMI Paid Successfully!\nRemaining Outstanding: ₹" + String.format("%.2f", newOutstanding));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error paying EMI: " + ex.getMessage());
        }
    }
}
