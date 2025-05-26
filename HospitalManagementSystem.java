import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class HospitalManagementSystem {
    private JFrame frame;
    private Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/hospital_management";
    private static final String USER = "root";
    private static final String PASSWORD = "241206";

    public HospitalManagementSystem() {
        connectDatabase();
        createLoginPage();
    }

    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database Connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createLoginPage() {
        frame = new JFrame("Login");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 2));

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"admin", "doctor", "patient"});
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        frame.add(new JLabel("Select Role:"));
        frame.add(roleBox);
        frame.add(new JLabel("Username:"));
        frame.add(usernameField);
        frame.add(new JLabel("Password:"));
        frame.add(passwordField);
        frame.add(new JLabel(""));
        frame.add(loginButton);

        loginButton.addActionListener(e -> {
            String role = (String) roleBox.getSelectedItem();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                String query = "SELECT * FROM users WHERE username=? AND password=? AND role=?";
                PreparedStatement pst = connection.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, role);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("related_id");
                    switch (role) {
                        case "admin":
                            frame.dispose();
                            createAdminDashboard();
                            break;
                        case "doctor":
                            frame.dispose();
                            createDoctorDashboard(userId);
                            break;
                        case "patient":
                            frame.dispose();
                            createPatientDashboard(userId);
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Credentials");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }

    private JFrame adminFrame;  // Global variable to keep track of the admin frame

private void createAdminDashboard() {
    // Check if the frame already exists, if so, dispose of it.
    if (adminFrame != null) {
        adminFrame.dispose();
    }

    // Create a new admin dashboard frame
    adminFrame = new JFrame("Admin Dashboard");
    adminFrame.setSize(500, 400);
    adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    adminFrame.setLayout(new GridLayout(5, 1));

    JButton addPatientBtn = createButton("Add Patient");
    JButton viewPatientBtn = createButton("View Patients");
    JButton addDoctorBtn = createButton("Add Doctor");
    JButton viewDoctorBtn = createButton("View Doctors");

    // Ensure only one frame is shown at a time, dispose of the previous frame.
    addPatientBtn.addActionListener(e -> openAddPatientForm());
    viewPatientBtn.addActionListener(e -> viewData("patients"));
    addDoctorBtn.addActionListener(e -> openAddDoctorForm());
    viewDoctorBtn.addActionListener(e -> viewData("doctors"));

    adminFrame.add(addPatientBtn);
    adminFrame.add(viewPatientBtn);
    adminFrame.add(addDoctorBtn);
    adminFrame.add(viewDoctorBtn);

    adminFrame.setVisible(true);
}

// The rest of the methods for opening forms (Add Patient, Add Doctor, View Patients, etc.)


    private void openAddPatientForm() {
        JFrame patientFrame = new JFrame("Add Patient");
        patientFrame.setSize(400, 300);
        patientFrame.setLayout(new GridLayout(6, 2));

        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField contactField = new JTextField();
        JTextField passwordField = new JTextField();

        JButton addButton = createButton("Add Patient");
        JButton backButton = createButton("Back");

        addButton.addActionListener(e -> {
            try {
                String insertPatient = "INSERT INTO patients (name, age, gender, contact, password) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = connection.prepareStatement(insertPatient, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, nameField.getText());
                pst.setInt(2, Integer.parseInt(ageField.getText()));
                pst.setString(3, (String) genderBox.getSelectedItem());
                pst.setString(4, contactField.getText());
                pst.setString(5, passwordField.getText());
                pst.executeUpdate();

                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int patientId = rs.getInt(1);
                    String insertUser = "INSERT INTO users (username, password, role, related_id) VALUES (?, ?, 'patient', ?)";
                    PreparedStatement userStmt = connection.prepareStatement(insertUser);
                    userStmt.setString(1, nameField.getText());
                    userStmt.setString(2, passwordField.getText());
                    userStmt.setInt(3, patientId);
                    userStmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(patientFrame, "Patient Added Successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        backButton.addActionListener(e -> {
            patientFrame.dispose();
            createAdminDashboard();
        });

        patientFrame.add(new JLabel("Name:"));
        patientFrame.add(nameField);
        patientFrame.add(new JLabel("Age:"));
        patientFrame.add(ageField);
        patientFrame.add(new JLabel("Gender:"));
        patientFrame.add(genderBox);
        patientFrame.add(new JLabel("Contact:"));
        patientFrame.add(contactField);
        patientFrame.add(new JLabel("Password:"));
        patientFrame.add(passwordField);
        patientFrame.add(addButton);
        patientFrame.add(backButton);

        patientFrame.setVisible(true);
    }

    private void openAddDoctorForm() {
        JFrame doctorFrame = new JFrame("Add Doctor");
        doctorFrame.setSize(400, 300);
        doctorFrame.setLayout(new GridLayout(5, 2));

        JTextField nameField = new JTextField();
        JTextField specialtyField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField passwordField = new JTextField();

        JButton addButton = createButton("Add Doctor");
        JButton backButton = createButton("Back");

        addButton.addActionListener(e -> {
            try {
                String insertDoctor = "INSERT INTO doctors (name, specialty, contact, password) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = connection.prepareStatement(insertDoctor, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, nameField.getText());
                pst.setString(2, specialtyField.getText());
                pst.setString(3, contactField.getText());
                pst.setString(4, passwordField.getText());
                pst.executeUpdate();

                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int doctorId = rs.getInt(1);
                    String insertUser = "INSERT INTO users (username, password, role, related_id) VALUES (?, ?, 'doctor', ?)";
                    PreparedStatement userStmt = connection.prepareStatement(insertUser);
                    userStmt.setString(1, nameField.getText());
                    userStmt.setString(2, passwordField.getText());
                    userStmt.setInt(3, doctorId);
                    userStmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(doctorFrame, "Doctor Added Successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        backButton.addActionListener(e -> {
            doctorFrame.dispose();
            createAdminDashboard();
        });

        doctorFrame.add(new JLabel("Name:"));
        doctorFrame.add(nameField);
        doctorFrame.add(new JLabel("Specialty:"));
        doctorFrame.add(specialtyField);
        doctorFrame.add(new JLabel("Contact:"));
        doctorFrame.add(contactField);
        doctorFrame.add(new JLabel("Password:"));
        doctorFrame.add(passwordField);
        doctorFrame.add(addButton);
        doctorFrame.add(backButton);

        doctorFrame.setVisible(true);
    }

    private void viewData(String tableName) {
        JFrame viewFrame = new JFrame("View " + tableName);
        viewFrame.setSize(700, 500);
        viewFrame.setLayout(new BorderLayout());

        String[] columnNames = tableName.equals("patients") ?
                new String[]{"ID", "Name", "Age", "Gender", "Contact"} :
                new String[]{"ID", "Name", "Specialty", "Contact"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            while (rs.next()) {
                Object[] row = tableName.equals("patients") ?
                        new Object[]{rs.getInt("patient_id"), rs.getString("name"), rs.getInt("age"), rs.getString("gender"), rs.getString("contact")} :
                        new Object[]{rs.getInt("doctor_id"), rs.getString("name"), rs.getString("specialty"), rs.getString("contact")};
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JButton deleteButton = createButton("Delete Selected Record");
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) table.getValueAt(selectedRow, 0);
                try {
                    String deleteQuery = tableName.equals("patients") ?
                            "DELETE FROM patients WHERE patient_id = ?" :
                            "DELETE FROM doctors WHERE doctor_id = ?";
                    PreparedStatement pst = connection.prepareStatement(deleteQuery);
                    pst.setInt(1, id);
                    pst.executeUpdate();
                    model.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(viewFrame, "Record Deleted!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton backButton = createButton("Back");
        backButton.addActionListener(e -> {
            viewFrame.dispose();
            createAdminDashboard();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        viewFrame.add(new JScrollPane(table), BorderLayout.CENTER);
        viewFrame.add(buttonPanel, BorderLayout.SOUTH);
        viewFrame.setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }

    private void createDoctorDashboard(int doctorId) {
        JFrame doctorFrame = new JFrame("Doctor Dashboard");
        doctorFrame.setSize(600, 400);
        doctorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        doctorFrame.setLayout(new BorderLayout());
    
        JLabel welcomeLabel = new JLabel("Welcome Doctor (ID: " + doctorId + ")", SwingConstants.CENTER);
        JButton logoutButton = createButton("Logout");
    
        // Add a table to display appointments
        String[] columnNames = {"Appointment ID", "Patient ID", "Date", "Reason"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable appointmentsTable = new JTable(model);
        JScrollPane tableScrollPane = new JScrollPane(appointmentsTable);
    
        // Fetch today's appointments from the database
        try {
            String query = "SELECT * FROM appointments WHERE doctor_id = ? AND appointment_date >= CURDATE()";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, doctorId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getInt("appointment_id"),
                    rs.getInt("patient_id"),
                    rs.getTimestamp("appointment_date"),
                    rs.getString("reason")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Logout button action
        logoutButton.addActionListener(e -> {
            doctorFrame.dispose();
            createLoginPage();  // Go back to login
        });
    
        // Layout components
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(welcomeLabel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(logoutButton);
    
        doctorFrame.add(panel, BorderLayout.CENTER);
        doctorFrame.add(buttonPanel, BorderLayout.SOUTH);
        doctorFrame.setVisible(true);
    }
    private void createPatientDashboard(int patientId) {
        JFrame patientFrame = new JFrame("Patient Dashboard");
        patientFrame.setSize(600, 400);
        patientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        patientFrame.setLayout(new BorderLayout());
    
        JLabel welcomeLabel = new JLabel("Welcome Patient (ID: " + patientId + ")", SwingConstants.CENTER);
        JButton logoutButton = createButton("Logout");
    
        // Form to book an appointment
        JPanel appointmentPanel = new JPanel(new GridLayout(3, 2));
        JComboBox<String> doctorBox = new JComboBox<>();
        JTextField reasonField = new JTextField();
    
        try {
            String query = "SELECT name FROM doctors";
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                doctorBox.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        JButton bookButton = createButton("Book Appointment");
    
        bookButton.addActionListener(e -> {
            String selectedDoctor = (String) doctorBox.getSelectedItem();
            String reason = reasonField.getText();
            if (selectedDoctor != null && !reason.isEmpty()) {
                try {
                    String insertAppointment = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, reason) " +
                            "VALUES (?, (SELECT doctor_id FROM doctors WHERE name = ?), NOW(), ?)";
                    PreparedStatement pst1 = connection.prepareStatement(insertAppointment);
                    pst1.setInt(1, patientId);
                    pst1.setString(2, selectedDoctor);
                    pst1.setString(3, reason);
                    pst1.executeUpdate();
    
                    JOptionPane.showMessageDialog(patientFrame, "Appointment Booked Successfully!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    
        // Appointment history table
        String[] historyColumns = {"Appointment ID", "Doctor", "Date", "Reason"};
        DefaultTableModel historyModel = new DefaultTableModel(historyColumns, 0);
        JTable historyTable = new JTable(historyModel);
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
    
        // Add 'View History' button
        JButton viewHistoryButton = createButton("View History");
        viewHistoryButton.addActionListener(e -> {
            try {
                String historyQuery = "SELECT a.appointment_id, d.name AS doctor, a.appointment_date, a.reason " +
                        "FROM appointments a JOIN doctors d ON a.doctor_id = d.doctor_id " +
                        "WHERE a.patient_id = ?";
                PreparedStatement pst2 = connection.prepareStatement(historyQuery);
                pst2.setInt(1, patientId);
                ResultSet rs2 = pst2.executeQuery();
                while (rs2.next()) {
                    String doctorName = rs2.getString("doctor");
                    if (doctorName.length() > 20) {
                        doctorName = doctorName.substring(0, 20) + "...";  // Truncate the doctor name if it exceeds 20 characters
                    }
                    Object[] row = new Object[]{
                            rs2.getInt("appointment_id"),
                            doctorName,
                            rs2.getTimestamp("appointment_date"),
                            rs2.getString("reason")
                    };
                    historyModel.addRow(row);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    
        // Layout components
        JPanel bookingPanel = new JPanel();
        bookingPanel.setLayout(new BoxLayout(bookingPanel, BoxLayout.Y_AXIS));
        bookingPanel.add(new JLabel("Select Doctor:"));
        bookingPanel.add(doctorBox);
        bookingPanel.add(new JLabel("Reason:"));
        bookingPanel.add(reasonField);
        bookingPanel.add(bookButton);
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(logoutButton);
        buttonPanel.add(viewHistoryButton); // Add View History button to panel
    
        patientFrame.add(welcomeLabel, BorderLayout.NORTH);
        patientFrame.add(bookingPanel, BorderLayout.WEST);
        patientFrame.add(historyScrollPane, BorderLayout.CENTER);
        patientFrame.add(buttonPanel, BorderLayout.SOUTH);
    
        logoutButton.addActionListener(e -> {
            patientFrame.dispose();
            createLoginPage();  // Go back to login
        });
    
        patientFrame.setVisible(true);
    }
        
    
    
    
    
    public static void main(String[] args) {
        new HospitalManagementSystem();
    }
}






       























