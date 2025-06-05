package OrderView;

import database.DataBaseConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class OrderHistoryView extends JPanel {
    private JTable orderTable;
    private JTable detailTable;
    private DefaultTableModel orderModel, detailModel;
    private int userId;
    private String userName;
    private JButton btnViewInvoice;

    public OrderHistoryView(int userId) {
        this.userId = userId;
        this.userName = getUserName(userId); // Lấy tên user để hiển thị trên hóa đơn
        setLayout(new BorderLayout());

        // Tạo panel chính
        initializeComponents();
        setupLayout();
        loadOrders();
        setupEventHandlers();
    }

    private void initializeComponents() {
        // Bảng đơn hàng
        orderModel = new DefaultTableModel(new Object[]{"Mã đơn", "Ngày đặt", "Tổng tiền", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa
            }
        };
        orderTable = new JTable(orderModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        orderTable.setFont(new Font("Arial", Font.PLAIN, 11));
        orderTable.setRowHeight(25);

        // Bảng chi tiết đơn hàng
        detailModel = new DefaultTableModel(new Object[]{"Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        detailTable = new JTable(detailModel);
        detailTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        detailTable.setFont(new Font("Arial", Font.PLAIN, 11));
        detailTable.setRowHeight(25);

        // Nút xem hóa đơn
        btnViewInvoice = new JButton("🧾 Xem hóa đơn chi tiết");
        btnViewInvoice.setFont(new Font("Arial", Font.BOLD, 12));
        btnViewInvoice.setPreferredSize(new Dimension(180, 35));
        btnViewInvoice.setEnabled(false); // Disabled ban đầu
        btnViewInvoice.setBackground(new Color(52, 152, 219));
        btnViewInvoice.setForeground(Color.WHITE);
        btnViewInvoice.setFocusPainted(false);
    }

    private void setupLayout() {
        // Panel tiêu đề
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        JLabel titleLabel = new JLabel("📋 LỊCH SỬ ĐƠN HÀNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(52, 73, 94));
        titlePanel.add(titleLabel);

        // Panel cho bảng đơn hàng
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)), 
            "Danh sách đơn hàng",
            0, 0, new Font("Arial", Font.BOLD, 12), new Color(52, 73, 94)));
        
        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setPreferredSize(new Dimension(0, 200));
        orderPanel.add(orderScroll, BorderLayout.CENTER);

        // Panel cho nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        buttonPanel.add(btnViewInvoice);

        // Panel cho bảng chi tiết
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "Chi tiết đơn hàng đã chọn",
            0, 0, new Font("Arial", Font.BOLD, 12), new Color(52, 73, 94)));
        
        JScrollPane detailScroll = new JScrollPane(detailTable);
        detailPanel.add(detailScroll, BorderLayout.CENTER);

        // Layout chính
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(orderPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, detailPanel);
        splitPane.setDividerLocation(280);
        splitPane.setResizeWeight(0.6);

        add(titlePanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Thêm padding cho toàn bộ panel
        setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
    }

    private void setupEventHandlers() {
        // Lắng nghe khi chọn đơn hàng để hiện chi tiết
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow != -1) {
                    String orderId = orderModel.getValueAt(selectedRow, 0).toString();
                    loadOrderDetails(orderId);
                    btnViewInvoice.setEnabled(true);
                } else {
                    btnViewInvoice.setEnabled(false);
                    detailModel.setRowCount(0);
                }
            }
        });

        // Xử lý sự kiện nút xem hóa đơn
        btnViewInvoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewInvoiceDetails();
            }
        });

        // Double click để xem hóa đơn
        orderTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewInvoiceDetails();
                }
            }
        });
    }

    public void loadOrders() {
        orderModel.setRowCount(0);
        String sql = "SELECT Order_ID, Order_Date, Total_Amount FROM Orders WHERE User_ID = ? ORDER BY Order_Date DESC";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("Order_ID"));
                row.add(rs.getTimestamp("Order_Date"));
                row.add(String.format("%.0f VNĐ", rs.getDouble("Total_Amount")));
            
                
                orderModel.addRow(row);
            }
            
            // Thông báo nếu không có đơn hàng
            if (orderModel.getRowCount() == 0) {
                Vector<Object> emptyRow = new Vector<>();
                emptyRow.add("Không có đơn hàng nào");
                emptyRow.add("");
                emptyRow.add("");
                emptyRow.add("");
                orderModel.addRow(emptyRow);
                btnViewInvoice.setEnabled(false);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải danh sách đơn hàng: " + e.getMessage(),
                "Lỗi database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOrderDetails(String orderId) {
        detailModel.setRowCount(0);
        String sql = "SELECT p.Name, od.Quantity, od.Unit_Price " +
                     "FROM OrderDetails od " +
                     "JOIN Products p ON od.Product_ID = p.Product_ID " +
                     "WHERE od.Order_ID = ?";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String name = rs.getString("Name");
                int qty = rs.getInt("Quantity");
                double price = rs.getDouble("Unit_Price");
                double total = qty * price;
                
                detailModel.addRow(new Object[]{
                    name, 
                    qty, 
                    String.format("%.0f VNĐ", price), 
                    String.format("%.0f VNĐ", total)
                });
            }
            
            if (detailModel.getRowCount() == 0) {
                detailModel.addRow(new Object[]{"Không có chi tiết", "", "", ""});
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải chi tiết đơn hàng: " + e.getMessage(),
                "Lỗi database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewInvoiceDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một đơn hàng để xem hóa đơn!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = orderModel.getValueAt(selectedRow, 0).toString();
        
        // Kiểm tra nếu là dòng "Không có đơn hàng nào"
        if (orderId.equals("Không có đơn hàng nào")) {
            return;
        }

        try {
            // Mở cửa sổ InvoiceView
            SwingUtilities.invokeLater(() -> {
                new InvoiceView(orderId, userName);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi mở hóa đơn: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getUserName(int userId) {
        String sql = "SELECT User_name FROM Users WHERE Users_ID = ?";
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("User_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Khách hàng #" + userId; // Fallback nếu không tìm thấy
    }

    // Phương thức để refresh dữ liệu từ bên ngoài
    public void refreshData() {
        loadOrders();
        detailModel.setRowCount(0);
        btnViewInvoice.setEnabled(false);
        orderTable.clearSelection();
    }

    // Getter cho userId (có thể cần thiết)
    public int getUserId() {
        return userId;
    }
}