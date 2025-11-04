package bank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class CreateAccountPanel extends JPanel {
    private JTextField tfName = new JTextField(20);
    private JPasswordField tfPass = new JPasswordField(20);
    private JTextField tfInitial = new JTextField(20);
    private JTextArea console = new JTextArea(4, 40);

    public CreateAccountPanel() {
        setLayout(new BorderLayout());

        // Title
        JLabel lblTitle = new JLabel("Create Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Center Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel l1 = new JLabel("Name:");
        JLabel l2 = new JLabel("Password:");
        JLabel l3 = new JLabel("Initial Deposit (₹):");

        l1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l3.setFont(new Font("Segoe UI", Font.BOLD, 16));

        tfName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tfPass.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tfInitial.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JButton btnCreate = new JButton("Create Account");
        btnCreate.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnCreate.setBackground(new Color(0, 102, 204));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFocusPainted(false);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(l1, gbc);
        gbc.gridx = 1;
        formPanel.add(tfName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(l2, gbc);
        gbc.gridx = 1;
        formPanel.add(tfPass, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(l3, gbc);
        gbc.gridx = 1;
        formPanel.add(tfInitial, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnCreate, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Console Output
        console.setEditable(false);
        console.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane sp = new JScrollPane(console);
        sp.setBorder(BorderFactory.createTitledBorder("Activity Log"));
        sp.setPreferredSize(new Dimension(900, 150));
        add(sp, BorderLayout.SOUTH);

        btnCreate.addActionListener((ActionEvent e) -> createAccount());
    }

    private void createAccount() {
        String name = tfName.getText().trim();
        String password = new String(tfPass.getPassword()).trim();
        double init;

        try {
            init = Double.parseDouble(tfInitial.getText().trim());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid amount");
            return;
        }

        if (name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Provide all fields");
            return;
        }

        // Generate random account number
        String accNo = "ACC" + (100000 + new Random().nextInt(900000));

        String insert = "INSERT INTO accounts (acc_no, name, password, balance) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insert)) {
            ps.setString(1, accNo);
            ps.setString(2, name);
            ps.setString(3, password);
            ps.setDouble(4, init);
            ps.executeUpdate();

            // Record transaction
            String tx = "INSERT INTO transactions (acc_no, type, amount) VALUES (?, 'CREATE', ?)";
            try (PreparedStatement ps2 = con.prepareStatement(tx)) {
                ps2.setString(1, accNo);
                ps2.setDouble(2, init);
                ps2.executeUpdate();
            }

            console.append("✅ Account created successfully\n");
            console.append("Account No: " + accNo + " | Name: " + name + "\n");
            console.append(String.format("Deposited ₹%.2f to %s%n\n", init, accNo));
            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating account: " + ex.getMessage());
        }
    }

    private void clearFields() {
        tfName.setText("");
        tfPass.setText("");
        tfInitial.setText("");
    }
}
