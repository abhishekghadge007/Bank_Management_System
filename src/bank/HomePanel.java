package bank;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    public HomePanel() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Welcome to Bank Management System", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(lbl, BorderLayout.CENTER);
    }
}
