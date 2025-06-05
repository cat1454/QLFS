package view;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import database.DataBaseConfig;
import utils.ValidationUtils;

public class RegisterView extends javax.swing.JFrame {

    public RegisterView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Register - Flower Shop");
        setSize(450, 450); // Tăng chiều cao để có thêm không gian
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Labels với font styling
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        JLabel lblUser = new JLabel("Username: *");
        JLabel lblPass = new JLabel("Password: *");
        JLabel lblAddress = new JLabel("Address: *");
        JLabel lblEmail = new JLabel("Email: *");
        JLabel lblPhone = new JLabel("Phone: *");
        
        lblUser.setFont(labelFont);
        lblPass.setFont(labelFont);
        lblAddress.setFont(labelFont);
        lblEmail.setFont(labelFont);
        lblPhone.setFont(labelFont);

        // Text fields với font styling
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 12);
        txtUserName = new JTextField(20);
        txtPass = new JPasswordField(20); // Sử dụng JPasswordField thay vì JTextField
        txtAddress = new JTextField(20);
        txtEmail = new JTextField(20);
        txtPhoneNum = new JTextField(20);
        
        txtUserName.setFont(fieldFont);
        txtPass.setFont(fieldFont);
        txtAddress.setFont(fieldFont);
        txtEmail.setFont(fieldFont);
        txtPhoneNum.setFont(fieldFont);

        // Thêm placeholder text bằng FocusListener
        addPlaceholderText(txtUserName, ValidationUtils.PLACEHOLDER_USERNAME);
        addPlaceholderText(txtAddress, ValidationUtils.PLACEHOLDER_ADDRESS);
        addPlaceholderText(txtEmail, ValidationUtils.PLACEHOLDER_EMAIL);
        addPlaceholderText(txtPhoneNum, ValidationUtils.PLACEHOLDER_PHONE);

        // Nút Đăng nhập - cải thiện styling
        buttonLogin = createButton("Quay lại đăng nhập", new Color(255, 193, 7), new Color(255, 160, 0));
        buttonLogin.setPreferredSize(new Dimension(160, 35));

        // Nút Đăng ký - cải thiện styling
        buttonRegister = createButton("Đăng ký", new Color(76, 175, 80), new Color(56, 142, 60));
        buttonRegister.setPreferredSize(new Dimension(120, 35));
        buttonRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Event listeners
        buttonLogin.addActionListener(evt -> {
            new LoginView().setVisible(true);
            dispose();
        });

        buttonRegister.addActionListener(evt -> registerAction());

        // Panel chính với layout cải thiện
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Thêm các components với GridBagLayout
        addFormField(mainPanel, lblUser, txtUserName, gbc, 0);
        addFormField(mainPanel, lblPass, txtPass, gbc, 1);
        addFormField(mainPanel, lblAddress, txtAddress, gbc, 2);
        addFormField(mainPanel, lblEmail, txtEmail, gbc, 3);
        addFormField(mainPanel, lblPhone, txtPhoneNum, gbc, 4);

        // Panel cho buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        buttonPanel.add(buttonLogin);
        buttonPanel.add(buttonRegister);

        // Layout chính
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(titleLabel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    // Helper method để tạo button với styling nhất quán
    private JButton createButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBorder(BorderFactory.createLineBorder(hoverColor, 2, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // Helper method để thêm field vào form
    private void addFormField(JPanel panel, JLabel label, JTextField field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        gbc.weightx = 0;
    }

    // Thêm placeholder text cho text fields
    private void addPlaceholderText(JTextField textField, String placeholder) {
        textField.setForeground(Color.GRAY);
        textField.setText(placeholder);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }

    // Validation methods
    private boolean validateInput() {
        String username = ValidationUtils.getCleanText(txtUserName);
        String password = txtPass instanceof JPasswordField ? 
            new String(((JPasswordField) txtPass).getPassword()) : txtPass.getText();
        String address = ValidationUtils.getCleanText(txtAddress);
        String email = ValidationUtils.getCleanText(txtEmail);
        String phone = ValidationUtils.getCleanText(txtPhoneNum);

        ValidationUtils.ValidationResult result = ValidationUtils.validateRegistrationForm(
            username, password, address, email, phone);

        if (!result.isValid()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng kiểm tra lại thông tin:\n" + result.getErrorMessage(), 
                "Thông tin không hợp lệ", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void registerAction() {
        if (!validateInput()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có muốn đăng ký tài khoản này?", 
            "Xác nhận đăng ký", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm != JOptionPane.YES_OPTION) return;

        // Hiển thị loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        buttonRegister.setEnabled(false);

        // Sử dụng SwingWorker để thực hiện database operation trong background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try (Connection con = DataBaseConfig.getConnection()) {
                    // Kiểm tra username đã tồn tại chưa
                    String checkSql = "SELECT COUNT(*) FROM Users WHERE User_name = ?";
                    try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
                        checkPs.setString(1, ValidationUtils.getCleanText(txtUserName));
                        ResultSet rs = checkPs.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            throw new SQLException("Tên đăng nhập đã tồn tại!");
                        }
                    }

                    // Insert user mới
                    String sql = "INSERT INTO Users (User_name, Password, Address, Email, PhoneNum, Role) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setString(1, ValidationUtils.getCleanText(txtUserName));
                        
                        if (txtPass instanceof JPasswordField) {
                            ps.setString(2, new String(((JPasswordField) txtPass).getPassword()));
                        } else {
                            ps.setString(2, ValidationUtils.getCleanText(txtPass));
                        }
                        
                        ps.setString(3, ValidationUtils.getCleanText(txtAddress));
                        ps.setString(4, ValidationUtils.getCleanText(txtEmail));
                        ps.setString(5, ValidationUtils.getCleanText(txtPhoneNum));
                        ps.setString(6, "User");

                        return ps.executeUpdate() > 0;
                    }
                }
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                buttonRegister.setEnabled(true);

                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(RegisterView.this, 
                            "Đăng ký thành công!\nBạn có thể đăng nhập ngay bây giờ.", 
                            "Thành công", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Chuyển về login form
                        new LoginView().setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegisterView.this, 
                            "Đăng ký thất bại. Vui lòng thử lại!", 
                            "Lỗi", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    JOptionPane.showMessageDialog(RegisterView.this, 
                        "Lỗi: " + errorMessage, 
                        "Lỗi đăng ký", 
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    // Biến giao diện
    private JTextField txtUserName;
    private JTextField txtPass; // Sẽ được cast thành JPasswordField
    private JTextField txtAddress;
    private JTextField txtEmail;
    private JTextField txtPhoneNum;
    private JButton buttonLogin;
    private JButton buttonRegister;
}