import javax.swing.*;
import java.awt.*;

public class HospitalManagementApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hospital Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLayout(new BorderLayout());

            JLabel welcomeLabel = new JLabel("Welcome to the Hospital Management System", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
            welcomeLabel.setForeground(Color.DARK_GRAY);

            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
            welcomeLabel.setForeground(Color.BLUE);
            frame.add(welcomeLabel, BorderLayout.NORTH);

            // Create a panel for user input
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new GridLayout(4, 2));
            inputPanel.setBackground(new Color(220, 240, 255));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // User name input
            inputPanel.add(new JLabel("Name:"));
            JTextField nameField = new JTextField(10);
            inputPanel.add(nameField);

            // Password input
            inputPanel.add(new JLabel("Password:"));
            JPasswordField passwordField = new JPasswordField(10);
            inputPanel.add(passwordField);

            // User role selection
            inputPanel.add(new JLabel("Login Type:"));
            String[] roles = {"Doctor", "Patient", "Admin"};
            JComboBox<String> roleComboBox = new JComboBox<>(roles);
            inputPanel.add(roleComboBox);

            JButton loginButton = new JButton("Login");
            loginButton.setPreferredSize(new Dimension(100, 30));
            loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            loginButton.setBackground(Color.BLUE);
            loginButton.setForeground(Color.WHITE);
            loginButton.setFont(new Font("Arial", Font.BOLD, 15));

            loginButton.addActionListener(e -> {
                String selectedRole = (String) roleComboBox.getSelectedItem();
                String name = nameField.getText();
                String password = new String(passwordField.getPassword());

                if (name.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Invalid username and password. Please enter both.");
                    return;
                }
                switch (selectedRole) {
                    case "Doctor":
                        new DoctorDashboard();
                        break;
                    case "Patient":
                        new PatientDashboard();
                        break;
                    case "Admin":
                        new AdminDashboard();
                        break;
                }
            });

            inputPanel.add(loginButton);
            inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            frame.add(inputPanel, BorderLayout.CENTER);

            frame.setVisible(true);
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    int confirm = JOptionPane.showConfirmDialog(frame, 
                        "Are you sure you want to exit?", "Exit Confirmation", 
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        frame.dispose();
                    }
                }
            });
        });
    }
}
