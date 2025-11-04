package bank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TransactionPanel extends JPanel {
    private JTextField accField;
    private JButton loadBtn;
    private JTable table;
    private DefaultTableModel model;

    public TransactionPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel();
        top.add(new JLabel("Account No:"));
        accField = new JTextField(15);
        top.add(accField);
        loadBtn = new JButton("Load Transactions");
        top.add(loadBtn);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Type", "Amount", "Date"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadBtn.addActionListener(e -> loadTransactions());
    }

    private void loadTransactions() {
        model.setRowCount(0);
        String acc = accField.getText();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id, type, amount, date FROM transactions WHERE acc_no=? ORDER BY date DESC")) {
            ps.setString(1, acc);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("date")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
