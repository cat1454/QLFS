package service;

import database.DataBaseConfig;  // Cần có class này để lấy connection
import model.User;

import java.sql.*;

public class UserService {

    // Lấy user theo ID
    public User getUserById(int userId) {
        User user = null;
        String sql = "SELECT * FROM Users WHERE Users_ID = ?";
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setUsersID(rs.getInt("Users_ID"));
                user.setUserName(rs.getString("User_Name").trim());
                user.setPassword(rs.getString("Password").trim());
                user.setAddress(rs.getString("Address"));
                user.setEmail(rs.getString("Email").trim());
                user.setPhoneNum(rs.getString("PhoneNum").trim());
                user.setRole(rs.getString("Role"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // Cập nhật thông tin user (email, phone, address)
    public boolean updateUserInfo(User user) {
        String sql = "UPDATE Users SET Email=?, PhoneNum=?, Address=? WHERE Users_ID=?";
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPhoneNum());
            ps.setString(3, user.getAddress());
            ps.setInt(4, user.getUsersID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Đổi mật khẩu có kiểm tra mật khẩu cũ
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (user == null) return false;

        if (!user.getPassword().equals(oldPassword)) {
            return false; // Mật khẩu cũ không đúng
        }

        String sql = "UPDATE Users SET Password=? WHERE Users_ID=?";
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa user theo ID
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE Users_ID = ?";
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
