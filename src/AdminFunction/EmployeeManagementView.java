package AdminFunction;

import database.DataBaseConfig;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EmployeeManagementView extends JPanel {

    private JTable tblEmployees;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtPassword, txtEmail, txtAddress, txtPhone, txtPosition, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch;

    public EmployeeManagementView() {
        initComponents();
        
    } 
   private void initComponents() {
    setLayout(new BorderLayout());

    // Table
    tableModel = new DefaultTableModel(
        new String[]{"ID", "Tên", "Mật khẩu", "Email", "Địa chỉ", "Số điện thoại", "Chức vụ"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tblEmployees = new JTable(tableModel);
    tblEmployees.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(tblEmployees);
    
    // Top panel for search
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    txtSearch = new JTextField(20);
    btnSearch = new JButton("Tìm kiếm");
    topPanel.add(new JLabel("Tìm tên:"));
    topPanel.add(txtSearch);
    topPanel.add(btnSearch);

    // Form input
    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 10, 5, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    txtName = new JTextField(15);
    txtPassword = new JTextField(15);
    txtEmail = new JTextField(15);
    txtAddress = new JTextField(15);
    txtPhone = new JTextField(15);
    txtPosition = new JTextField(15);

    String[] labels = {"Tên:", "Mật khẩu:", "Email:", "Địa chỉ:", "Số điện thoại:", "Chức vụ:"};
    JTextField[] fields = {txtName, txtPassword, txtEmail, txtAddress, txtPhone, txtPosition};

    for (int i = 0; i < labels.length; i++) {
        gbc.gridx = 0;
        gbc.gridy = i;
        formPanel.add(new JLabel(labels[i]), gbc);

        gbc.gridx = 1;
        formPanel.add(fields[i], gbc);
    }

    // Buttons
    btnAdd = new JButton("Thêm");
    btnUpdate = new JButton("Sửa");
    btnDelete = new JButton("Xóa");

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    buttonPanel.add(btnAdd);
    buttonPanel.add(btnUpdate);
    buttonPanel.add(btnDelete);

    JPanel inputArea = new JPanel(new BorderLayout());
    inputArea.add(formPanel, BorderLayout.CENTER);
    inputArea.add(buttonPanel, BorderLayout.SOUTH);

    // Add to main panel
    add(topPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
    add(inputArea, BorderLayout.SOUTH);

    // Action Listeners
    btnAdd.addActionListener(e -> addEmployee());
    btnUpdate.addActionListener(e -> updateEmployee());
    btnDelete.addActionListener(e -> deleteEmployee());
    btnSearch.addActionListener(e -> searchEmployee());
    tblEmployees.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
}

    public void loadUsersData() {
        tableModel.setRowCount(0);
        try (Connection con = DataBaseConfig.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Users")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("Users_ID"),
                        rs.getString("User_Name"),
                        rs.getString("Password"),
                        rs.getString("Email"),
                        rs.getString("Address"),
                        rs.getString("PhoneNum"),
                        rs.getString("Role")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void addEmployee() {
        String name = txtName.getText();
        String password = txtPassword.getText();
        String email = txtEmail.getText();
        String address = txtAddress.getText();
        String phone = txtPhone.getText();
        String position = txtPosition.getText();

        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên, mật khẩu và email là bắt buộc.");
            return;
        }

        String sql = "INSERT INTO Users (User_Name, Password, Email, Address, PhoneNum, Role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, address);
            ps.setString(5, phone);
            ps.setString(6, position);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            clearForm();
            loadUsersData();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + e.getMessage());
        }
    }

    private void updateEmployee() {
        int row = tblEmployees.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để sửa.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = txtName.getText();
        String password = txtPassword.getText();
        String email = txtEmail.getText();
        String address = txtAddress.getText();
        String phone = txtPhone.getText();
        String position = txtPosition.getText();

        String sql = "UPDATE Users SET User_Name=?, Password=?, Email=?, Address=?, PhoneNum=?, Role=? WHERE Users_ID=?";

        try (Connection con = DataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, address);
            ps.setString(5, phone);
            ps.setString(6, position);
            ps.setInt(7, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            clearForm();
            loadUsersData();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi sửa: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        int row = tblEmployees.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn nhân viên để xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa nhân viên?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) tableModel.getValueAt(row, 0);

        try (Connection con = DataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Users WHERE Users_ID = ?")) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Đã xóa.");
            clearForm();
            loadUsersData();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
        }
    }

    private void searchEmployee() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);

        try (Connection con = DataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Users WHERE User_Name LIKE ?")) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("Users_ID"),
                        rs.getString("User_Name"),
                        rs.getString("Password"),
                        rs.getString("Email"),
                        rs.getString("Address"),
                        rs.getString("PhoneNum"),
                        rs.getString("Role")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    private void fillFormFromTable() {
        int row = tblEmployees.getSelectedRow();
        if (row != -1) {
            txtName.setText((String) tableModel.getValueAt(row, 1));
            txtPassword.setText((String) tableModel.getValueAt(row, 2));
            txtEmail.setText((String) tableModel.getValueAt(row, 3));
            txtAddress.setText((String) tableModel.getValueAt(row, 4));
            txtPhone.setText((String) tableModel.getValueAt(row, 5));
            txtPosition.setText((String) tableModel.getValueAt(row, 6));
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        txtPosition.setText("");
        txtSearch.setText("");
    }

   
}
