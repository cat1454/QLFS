package OrderView;

import database.DataBaseConfig;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import service.CartService;
import service.OrderService;
import utils.AuthUtils;


  
public class FlowerMenuView extends JPanel {

    private JPanel panelContainer;
    private final JScrollPane scrollPane;
    private static int availableQuantity;
    private LuckyWheel Wheel;


    public FlowerMenuView(boolean par) {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        if (AuthUtils.isAdmin()) {
            JButton btnAddFlower = new JButton("Thêm");
            topPanel.add(btnAddFlower);
            btnAddFlower.addActionListener(e -> showAddFlowerDialog());
        } 

        add(topPanel, BorderLayout.NORTH);

        panelContainer = new JPanel();
        panelContainer.setLayout(new GridLayout(0, 3, 20, 20));
        panelContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        scrollPane = new JScrollPane(panelContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        loadFlowers();
        this.availableQuantity = 0;
    }



    private void showAddFlowerDialog() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField descField = new JTextField();
        JTextField imageField = new JTextField();
        imageField.setEditable(false);

        final File[] selectedImageFile = new File[1];

        JButton browseButton = new JButton("Chọn ảnh...");
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn ảnh sản phẩm");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImageFile[0] = fileChooser.getSelectedFile();
                imageField.setText(selectedImageFile[0].getName());
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Tên hoa:")); panel.add(nameField);
        panel.add(new JLabel("Giá:")); panel.add(priceField);
        panel.add(new JLabel("Số lượng:")); panel.add(quantityField);
        panel.add(new JLabel("Mô tả:")); panel.add(descField);
        panel.add(new JLabel("Ảnh sản phẩm:"));

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageField, BorderLayout.CENTER);
        imagePanel.add(browseButton, BorderLayout.EAST);
        panel.add(imagePanel);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm hoa mới", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                String desc = descField.getText();

                String newFileName = null;
                if (selectedImageFile[0] != null) {
                    String ext = selectedImageFile[0].getName().substring(selectedImageFile[0].getName().lastIndexOf("."));
                    newFileName = UUID.randomUUID().toString() + ext;

                    File destDir = new File("images/");
                    if (!destDir.exists()) destDir.mkdirs();

                    File destFile = new File(destDir, newFileName);
                    Files.copy(selectedImageFile[0].toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                String query = "INSERT INTO Products (Name, Price, Quantity, Description, Image) VALUES (?, ?, ?, ?, ?)";
                try (Connection con = DataBaseConfig.getConnection();
                     PreparedStatement stmt = con.prepareStatement(query)) {
                    stmt.setString(1, name);
                    stmt.setDouble(2, price);
                    stmt.setInt(3, quantity);
                    stmt.setString(4, desc);
                    stmt.setString(5, newFileName != null ? "images/" + newFileName : null);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Đã thêm hoa thành công!");
                    reloadFlowers();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm hoa: " + ex.getMessage());
            }
        }
    }

    private void loadFlowers() {
        String query = "SELECT * FROM Products";

        try (Connection con = DataBaseConfig.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("Product_ID");
                String name = rs.getString("Name");
                double price = rs.getDouble("Price");
                String imagePath = rs.getString("Image");
                String desc = rs.getString("Description");
                int quantity = rs.getInt("Quantity");

                JPanel flowerCard = createFlowerCard(id, name, price, quantity, desc, imagePath);
                panelContainer.add(flowerCard);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private JPanel createFlowerCard(int id, String name, double price, int quantity, String desc, String imagePath) throws SQLException {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);

        try {
            File imgFile = new File(imagePath);
            ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            imageLabel.setToolTipText("<html><b>Mô tả:</b> " + desc + "<br><b>Số lượng:</b> " + quantity + "</html>");
            card.add(imageLabel);
        } catch (Exception e) {
            System.out.println("Không thể load ảnh: " + imagePath);
        }

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel priceLabel = new JLabel(String.format("%,.0f VNĐ", price));
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        priceLabel.setForeground(Color.RED);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
         JLabel quantityLabel = new JLabel("Số lượng: " + quantity);
    quantityLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    quantityLabel.setForeground(Color.GRAY);
    quantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(priceLabel);
        card.add(quantityLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        if (AuthUtils.isAdmin()) {
            JButton editBtn = new JButton("Sửa");
            JButton deleteBtn = new JButton("Xóa");
            

            editBtn.addActionListener(e -> showEditFlowerDialog(id));
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa hoa này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection con = DataBaseConfig.getConnection();
                         PreparedStatement stmt = con.prepareStatement("DELETE FROM Products WHERE Product_ID = ?")) {
                        stmt.setInt(1, id);
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Đã xóa hoa!");
                        reloadFlowers();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi xóa hoa: " + ex.getMessage());
                    }
                }
            });

            buttonPanel.add(editBtn);
            buttonPanel.add(deleteBtn);
        } else {
           // Khi tạo card cho mỗi sản phẩm:
           // Lấy số lượng tồn kho hiện tại
     
Connection con0 = DataBaseConfig.getConnection();
String checkQuantitySql = "SELECT Quantity FROM Products WHERE Product_ID = ?";
try (PreparedStatement checkStmt = con0.prepareStatement(checkQuantitySql)) {
    checkStmt.setInt(1, id);
    try (ResultSet rs = checkStmt.executeQuery()) {
        if (rs.next()) {
            availableQuantity = rs.getInt("Quantity");
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!");
            return card ;
        } 
        int orderQuantity = 0;
        if (orderQuantity > availableQuantity) {
    JOptionPane.showMessageDialog(this, "Số lượng đặt vượt quá số lượng tồn khol! Chỉ còn " + availableQuantity + " sản phẩm.");
    return card;
}
    } catch (SQLException ex) {
        Logger.getLogger(FlowerMenuView.class.getName()).log(Level.SEVERE, null, ex);
    } 
}           catch (SQLException ex) {
                Logger.getLogger(FlowerMenuView.class.getName()).log(Level.SEVERE, null, ex);
            }

           
            
            int intial= availableQuantity > 0 ? 1 : 0;
            int min= availableQuantity > 0 ? 1 : 0;
            JSpinner spnQuantity = new JSpinner(new SpinnerNumberModel(intial, min, availableQuantity, 1));
            

            buttonPanel.add(new JLabel("Số lượng:"));
            buttonPanel.add(spnQuantity);
            JButton btnOrder = new JButton("Đặt ngay");
            JButton btnAddToCart = new JButton("Thêm vào giỏ");
            if (availableQuantity <= 0) {
            spnQuantity.setEnabled(false);
            btnOrder.setEnabled(false);
            btnAddToCart.setEnabled(false);
            
}

            buttonPanel.add(btnOrder);
            buttonPanel.add(btnAddToCart);


            btnAddToCart.addActionListener(e -> {
            int CartQuantity = (Integer) spnQuantity.getValue();
            CartService.getInstance().addToCart(id,name,CartQuantity,price);
            
            JOptionPane.showMessageDialog(this, "Đã thêm vào giỏ hàng!");
            
              
            });
              
 
   btnOrder.addActionListener(e -> {
   int orderQuantity = (Integer) spnQuantity.getValue();
    OrderService orderService = OrderService.getInstance();
    
    // Hỏi có muốn tham gia vòng quay may mắn không
    int choice = JOptionPane.showOptionDialog(
        this,
        "<html><center>" +
        "<h2>🎯 VÒNG QUAY MAY MẮN</h2>" +
        "<p>Bạn có muốn tham gia vòng quay may mắn để nhận mã giảm giá không?</p>" +
        "<br><b>Quy tắc:</b>" +
        "<li>• Có tối đa 3 lượt quay miễn phí</li>" +
        "<li>• Cơ hội nhận giảm giá từ 5% đến 50%</li>" +
        "<li>• Có thể dừng bất kỳ lúc nào để sử dụng mã đã có</li>" +
        "<li>• Nếu không tham gia sẽ đặt hàng với giá gốc</li>" +
        "</center></html>",
        "Vòng Quay May Mắn",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        new String[]{"🎲 THAM GIA", "❌ BỎ QUA"},
        "🎲 THAM GIA"
    );
    
    if (choice == 0) { // Chọn tham gia
        // Tạo LuckyWheel với callback
        LuckyWheel wheel = new LuckyWheel(new SpinResultCallback() {
            @Override
            public void onSpinComplete(String prizeLabel) {
                System.out.println("Lần quay vừa rồi: " + prizeLabel);
            }
            
            @Override
            public void onAllSpinsCompleted(String finalPrizeLabel) {
                float discount = 0f;
                if (finalPrizeLabel != null && finalPrizeLabel.endsWith("%")) {
                    System.out.println("Nhận được giảm giá cuối cùng: " + finalPrizeLabel);
                    String discountStr = finalPrizeLabel.replace("%", "");
                    discount = Float.parseFloat(discountStr) / 100f;
                }
                
            
               
                if (orderService.placeSingleOrder(id, name, orderQuantity, price, discount)) {
                    reloadFlowers();
                }
            }
        });
        
        wheel.setVisible(true);
        
    } else { // Chọn bỏ qua - đặt hàng ngay
        if (orderService.placeSingleOrder(id, name, orderQuantity, price, 0f)) {
            
            reloadFlowers();
        }
    }
});
         }
        card.add(buttonPanel);
        card.add(Box.createVerticalStrut(5));
        return card;
    }

    private void showEditFlowerDialog(int flowerId) {
        try (Connection con = DataBaseConfig.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT * FROM Products WHERE Product_ID = ?")) {
            stmt.setInt(1, flowerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String oldName = rs.getString("Name");
                double oldPrice = rs.getDouble("Price");
                int oldQuantity = rs.getInt("Quantity");
                String oldDesc = rs.getString("Description");

                JTextField nameField = new JTextField(oldName);
                JTextField priceField = new JTextField(String.valueOf(oldPrice));
                JTextField quantityField = new JTextField(String.valueOf(oldQuantity));
                JTextField descField = new JTextField(oldDesc);

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Tên hoa:")); panel.add(nameField);
                panel.add(new JLabel("Giá:")); panel.add(priceField);
                panel.add(new JLabel("Số lượng:")); panel.add(quantityField);
                panel.add(new JLabel("Mô tả:")); panel.add(descField);

                int confirm = JOptionPane.showConfirmDialog(this, panel, "Chỉnh sửa hoa", JOptionPane.OK_CANCEL_OPTION);
                if (confirm == JOptionPane.OK_OPTION) {
                    String newName = nameField.getText();
                    double newPrice = Double.parseDouble(priceField.getText());
                    int newQuantity = Integer.parseInt(quantityField.getText());
                    String newDesc = descField.getText();

                    try (PreparedStatement updateStmt = con.prepareStatement(
                            "UPDATE Products SET Name = ?, Price = ?, Quantity = ?, Description = ? WHERE Product_ID = ?")) {
                        updateStmt.setString(1, newName);
                        updateStmt.setDouble(2, newPrice);
                        updateStmt.setInt(3, newQuantity);
                        updateStmt.setString(4, newDesc);
                        updateStmt.setInt(5, flowerId);
                        updateStmt.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Đã cập nhật thành công!");
                        reloadFlowers();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hoa cần sửa.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi sửa hoa: " + ex.getMessage());
        }
    }

    public void reloadFlowers() {
        panelContainer.removeAll();
        loadFlowers();
        panelContainer.revalidate();
        panelContainer.repaint();
    }

   
  

}