package bank;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SplashScreen extends JFrame {
    private JProgressBar progressBar;
    private JLabel lblTitle, lblStatus, lblLogo;

    public SplashScreen() {
        setTitle("Bank Management System - Loading");
        setSize(1000, 800);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 247, 250));

        // 🟣 Title
        lblTitle = new JLabel("Welcome to Bank Management System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 🟢 Load and resize logo
        lblLogo = new JLabel("", SwingConstants.CENTER);
        ImageIcon logoIcon = loadAndResizeImage("images/bank logo.png", 1000, 800);
        if (logoIcon != null) {
            lblLogo.setIcon(logoIcon);
        } else {
            lblLogo.setText("⚠️ Image Not Found");
            lblLogo.setForeground(Color.RED);
        }
        add(lblLogo, BorderLayout.CENTER);

        // 🔵 Bottom panel with progress and status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));
        bottomPanel.setBackground(new Color(245, 247, 250));

        lblStatus = new JLabel("Initializing...", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 102, 204));
        progressBar.setPreferredSize(new Dimension(450, 25));

        bottomPanel.add(lblStatus, BorderLayout.NORTH);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
        startLoading();
    }

    // 🧩 Function to load & resize image safely
    private ImageIcon loadAndResizeImage(String path, int width, int height) {
        File imgFile = new File(path);
        if (!imgFile.exists()) return null;

        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void startLoading() {
        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                try {
                    Thread.sleep(30);
                    progressBar.setValue(i);

                    if (i < 30) lblStatus.setText("Connecting to Database...");
                    else if (i < 60) lblStatus.setText("Loading Modules...");
                    else if (i < 90) lblStatus.setText("Preparing Interface...");
                    else lblStatus.setText("Starting Application...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            SwingUtilities.invokeLater(() -> {
                dispose();
                new MainDashboard(); // Redirect to dashboard
            });
        }).start();
    }

    public static void main(String[] args) {
        new SplashScreen();
    }
}
