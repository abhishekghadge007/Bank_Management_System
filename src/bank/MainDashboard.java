package bank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainDashboard extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cards = new JPanel(cardLayout);

    public MainDashboard() {
        setTitle("Bank Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(3, 82, 102));
        header.setPreferredSize(new Dimension(0, 60));
        JLabel title = new JLabel("Bank Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(0, 1, 0, 6));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(new Color(24, 124, 209));

        String[] buttons = {
            "Home", "Create Account", "Deposit", "Withdraw",
            "Fixed Deposit", "Loan", "Check Balance", "Transactions"
        };

        for (String b : buttons) {
            JButton btn = new JButton(b);
            btn.setFocusPainted(false);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setBackground(new Color(33, 150, 243));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btn.addActionListener((ActionEvent e) -> cardLayout.show(cards, b));
            sidebar.add(btn);
        }

        add(sidebar, BorderLayout.WEST);

        // Panels (cards)
        cards.add(new HomePanel(), "Home");
        cards.add(new CreateAccountPanel(), "Create Account");
        cards.add(new DepositPanel(), "Deposit");
        cards.add(new WithdrawPanel(), "Withdraw");
        cards.add(new FixedDepositPanel(), "Fixed Deposit");
        cards.add(new LoanPanel(), "Loan");
        cards.add(new CheckBalancePanel(), "Check Balance");
        cards.add(new TransactionPanel(), "Transactions");

        add(cards, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> new MainDashboard());
    }
}
