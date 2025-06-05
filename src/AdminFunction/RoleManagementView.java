package AdminFunction;

import database.DataBaseConfig;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import utils.AuthUtils;

public class RoleManagementView extends JPanel {

    private JTable tblUsers;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbRoles;
    private JButton btnUpdate, btnClose;

    public RoleManagementView() {
        
        setLayout(new BorderLayout());
        initComponents();

        if (!AuthUtils.isAdmin()) {
            JOptionPane.showMessageDialog(this,
                "Bạn không có quyền truy cập vào quản lý phân quyền",
                "Lỗi phân quyền",
                JOptionPane.ERROR_MESSAGE);

            // Xoá toàn bộ nội dung panel khi không có quyền
            removeAll();
            revalidate();
            repaint();
            return;
        }
        
        loadUserData(); 
    }

    private void initComponents() {
        JPanel titlePanel = new JPanel();
        JLabel lblTitle = new JLabel("QUẢN LÝ PHÂN QUYỀN NGƯỜI DÙNG");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        titlePanel.add(lblTitle);

        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Tên người dùng", "Mật khẩu", "Địa chỉ", "Email", "Số điện thoại", "Vai trò"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        tblUsers = new JTable(tableModel);
        tblUsers.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cmbRoles = new JComboBox<>(new String[]{"Admin", "User"});
        tblUsers.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(cmbRoles));
        JScrollPane scrollPane = new JScrollPane(tblUsers);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnUpdate = new JButton("Cập nhật quyền");
        btnUpdate.addActionListener(evt -> updateUserRole());

        btnClose = new JButton("Đóng");
        btnClose.addActionListener(evt -> {
            // Nếu Panel nằm trong CardLayout hoặc vùng trung tâm của giao diện chính,
            // bạn có thể gọi phương thức từ bên ngoài để ẩn hoặc thay thế panel này.
            // Ở đây mình sẽ làm cho panel trống:
            removeAll();
            revalidate();
            repaint();
        });

        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnClose);

        add(titlePanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
    }

    public void loadUserData() {
        tableModel.setRowCount(0);

        try {
            Connection con = DataBaseConfig.getConnection();
            String sql = "SELECT * FROM Users ORDER BY User_Name";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("Users_ID");
                String userName = rs.getString("User_Name");
                String password = rs.getString("Password");
                String email = rs.getString("Email");
                String address = rs.getString("Address");
                String phoneNum = rs.getString("PhoneNum");
                String role = rs.getString("Role");

                if (role == null || role.isEmpty()) {
                    role = "User";
                }

                tableModel.addRow(new Object[]{id, userName, password, address, email, phoneNum, role});
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu người dùng: " + e.getMessage(),
                "Lỗi database",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateUserRole() {
        if (tblUsers.isEditing()) {
            tblUsers.getCellEditor().stopCellEditing();
        }

        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một người dùng để cập nhật quyền",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);
        String newRole = (String) tableModel.getValueAt(selectedRow, 6);
        String oldRole = getUserCurrentRole(userId);
        int currentUserId = AuthUtils.getLoggedInUserId();

        if (userId == currentUserId && !newRole.equals("Admin")) {
            JOptionPane.showMessageDialog(this,
                "Bạn không thể hạ cấp chính mình khỏi vai trò Admin!",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
            loadUserData();
            return;
        }

        if (oldRole.equals("User") && newRole.equals("Admin")) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn cấp quyền Admin cho '" + userName + "'?",
                "Xác nhận phân quyền",
                JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                loadUserData();
                return;
            }
        }

        try {
            Connection con = DataBaseConfig.getConnection();
            String sql = "UPDATE Users SET Role = ? WHERE Users_ID = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, newRole);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                    "Đã cập nhật quyền cho người dùng '" + userName + "' thành '" + newRole + "'",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

                System.out.println("Phân quyền: Người dùng '" + userName + "' được chuyển thành '" + newRole + "'");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể cập nhật quyền. Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }

            pstmt.close();
            con.close();

            loadUserData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi cập nhật quyền: " + e.getMessage(),
                "Lỗi database",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String getUserCurrentRole(int userId) {
        try {
            Connection con = DataBaseConfig.getConnection();
            String sql = "SELECT Role FROM Users WHERE Users_ID = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("Role");
                rs.close();
                pstmt.close();
                con.close();
                return role != null ? role : "User";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "User";
    }



}
