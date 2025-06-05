package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import database.DataBaseConfig;
import static javax.swing.UIManager.getInt;
import utils.AuthUtils;

public class LoginView extends JFrame {

    private JTextField txtUserName;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;

    public static String currentUserName;
    public static String currentUserRole;

    public LoginView() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Đăng nhập hệ thống");

        // Tên shop to, đậm, màu hồng
        JLabel lblTitle = new JLabel("🌸 SHOP HOA YÊU THƯƠNG 🌸", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Serif", Font.BOLD, 24));
        lblTitle.setForeground(new Color(204, 0, 102));

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblPass.setFont(new Font("SansSerif", Font.PLAIN, 16));

        txtUserName = new JTextField(15);
        txtPassword = new JPasswordField(15);

        btnLogin = new JButton("Đăng nhập");
        btnRegister = new JButton("Đăng ký");

        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnLogin.addActionListener(this::handleLogin);
        btnRegister.addActionListener(e -> {
            new RegisterView().setVisible(true);
            dispose();
        });

        // Bố cục giao diện
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(lblUser, gbc);

        gbc.gridx = 1;
        panel.add(txtUserName, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(lblPass, gbc);

        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnRegister);
        btnPanel.add(btnLogin);
        panel.add(btnPanel, gbc);

        // Đưa panel chính vào frame
        getContentPane().add(panel);
        pack();
    }

    private void handleLogin(ActionEvent evt) {
        String username = txtUserName.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không để thông tin trống", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DataBaseConfig.getConnection()) {
            String sql = "SELECT * FROM Users WHERE User_Name = ? AND Password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                
                currentUserName = rs.getString("User_Name");
                currentUserRole = rs.getString("Role");
                int userId = rs.getInt("Users_ID");
                AuthUtils.setCurrentUserId(userId);
                AuthUtils.setCurrentUserName(currentUserName);
             
                if ("admin".equalsIgnoreCase(currentUserRole)) {
                    new AdminView().setVisible(true);
                } else {
                    new UserView().setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
