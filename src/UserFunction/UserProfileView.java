package UserFunction;

import model.User;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UserProfileView extends JPanel {
    private JTextField txtUsername, txtEmail, txtPhone;
    private JTextArea txtAddress;
    private JPasswordField txtOldPassword, txtNewPassword, txtConfirmPassword;
    private JButton btnUpdateInfo, btnChangePassword, btnDeleteAccount;

    private User currentUser;
    private UserService userService = new UserService();

    public UserProfileView(int userId) {
        currentUser = userService.getUserById(userId);
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin cá nhân"));

        infoPanel.add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField();
        txtUsername.setEditable(false);
        infoPanel.add(txtUsername);

        infoPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        infoPanel.add(txtEmail);

        infoPanel.add(new JLabel("Số điện thoại:"));
        txtPhone = new JTextField();
        infoPanel.add(txtPhone);

        infoPanel.add(new JLabel("Địa chỉ:"));
        txtAddress = new JTextArea(3, 20);
        infoPanel.add(new JScrollPane(txtAddress));

        btnUpdateInfo = new JButton("Lưu thông tin");
        btnUpdateInfo.addActionListener(this::updateInfoAction);

        btnDeleteAccount = new JButton("Xóa tài khoản");
        btnDeleteAccount.addActionListener(this::deleteAccountAction);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(btnUpdateInfo);
        buttonsPanel.add(btnDeleteAccount);

        // Panel đổi mật khẩu
        JPanel passwordPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        passwordPanel.setBorder(BorderFactory.createTitledBorder("Đổi mật khẩu"));

        passwordPanel.add(new JLabel("Mật khẩu cũ:"));
        txtOldPassword = new JPasswordField();
        passwordPanel.add(txtOldPassword);

        passwordPanel.add(new JLabel("Mật khẩu mới:"));
        txtNewPassword = new JPasswordField();
        passwordPanel.add(txtNewPassword);

        passwordPanel.add(new JLabel("Xác nhận mật khẩu mới:"));
        txtConfirmPassword = new JPasswordField();
        passwordPanel.add(txtConfirmPassword);

        btnChangePassword = new JButton("Đổi mật khẩu");
        btnChangePassword.addActionListener(this::changePasswordAction);
        passwordPanel.add(new JLabel()); // ô trống
        passwordPanel.add(btnChangePassword);

        add(infoPanel, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
        add(passwordPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        if (currentUser != null) {
            txtUsername.setText(currentUser.getUserName());
            txtEmail.setText(currentUser.getEmail());
            txtPhone.setText(currentUser.getPhoneNum());
            txtAddress.setText(currentUser.getAddress());
        }
    }

    private void updateInfoAction(ActionEvent e) {
        currentUser.setEmail(txtEmail.getText().trim());
        currentUser.setPhoneNum(txtPhone.getText().trim());
        currentUser.setAddress(txtAddress.getText().trim());

        boolean success = userService.updateUserInfo(currentUser);
        if (success) {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thất bại!");
        }
    }

    private void changePasswordAction(ActionEvent e) {
        String oldPass = new String(txtOldPassword.getPassword());
        String newPass = new String(txtNewPassword.getPassword());
        String confirmPass = new String(txtConfirmPassword.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin mật khẩu!");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới và xác nhận không khớp!");
            return;
        }
        boolean success = userService.changePassword(currentUser.getUsersID(), oldPass, newPass);
        if (success) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            // Xóa dữ liệu ô mật khẩu sau khi đổi thành công
            txtOldPassword.setText("");
            txtNewPassword.setText("");
            txtConfirmPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng hoặc đổi mật khẩu thất bại!");
        }
    }

    private void deleteAccountAction(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa tài khoản? Hành động này không thể hoàn tác!",
                "Xác nhận xóa tài khoản",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = userService.deleteUser(currentUser.getUsersID());
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Tài khoản đã được xóa!");
                // Có thể thêm logic thoát hoặc quay về trang đăng nhập
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(this, "Xóa tài khoản thất bại!");
            }
        }
    }
}
