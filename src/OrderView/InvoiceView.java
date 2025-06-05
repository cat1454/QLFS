package OrderView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import model.InvoiceItem;
import service.InvoiceService;

public class InvoiceView extends JFrame {
    private JTable table;
    private JLabel lblTotal, lblDate, lblCustomer, lblOrderID;
    private JLabel lblOriginalAmount, lblDiscountInfo, lblDiscountAmount, lblFinalAmount;
    private String orderId;
    private String userName;
    private InvoiceService service;

    public InvoiceView(String orderId, String userName) {
        this.orderId = orderId;
        this.userName = userName;
        this.service = new InvoiceService();
        
        initializeComponents();
        setupLayout();
        loadInvoiceData();
        setVisible(true);
    }
    
    private void initializeComponents() {
        setTitle("HÓA ĐƠN CHI TIẾT");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Set icon nếu có
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("icons/invoice.png"));
        } catch (Exception e) {
            // Ignore if icon not found
        }
    }
    
    private void setupLayout() {
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        
        // Add padding
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("Thông tin đơn hàng"));
        
        // Company info (optional)
        JPanel companyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel companyName = new JLabel("TIỆM HOA YÊU THƯƠNG");
        companyName.setFont(new Font("Arial", Font.BOLD, 16));
        companyPanel.add(companyName);
        
        // Order info
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        
        lblOrderID = new JLabel("Mã đơn hàng:");
        JLabel lblOrderIDValue = new JLabel();
        lblCustomer = new JLabel("Khách hàng:");
        JLabel lblCustomerValue = new JLabel();
        lblDate = new JLabel("Ngày đặt:");
        JLabel lblDateValue = new JLabel();
        
        // Discount info labels
        lblOriginalAmount = new JLabel("Tổng tiền gốc:");
        JLabel lblOriginalAmountValue = new JLabel();
        lblDiscountInfo = new JLabel("Thông tin giảm giá:");
        JLabel lblDiscountInfoValue = new JLabel();
        lblDiscountAmount = new JLabel("Số tiền giảm:");
        JLabel lblDiscountAmountValue = new JLabel();
        
        // Style labels
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Font valueFont = new Font("Arial", Font.PLAIN, 12);
        
        lblOrderID.setFont(labelFont);
        lblCustomer.setFont(labelFont);
        lblDate.setFont(labelFont);
        lblOriginalAmount.setFont(labelFont);
        lblDiscountInfo.setFont(labelFont);
        lblDiscountAmount.setFont(labelFont);
        
        lblOrderIDValue.setFont(valueFont);
        lblCustomerValue.setFont(valueFont);
        lblDateValue.setFont(valueFont);
        lblOriginalAmountValue.setFont(valueFont);
        lblDiscountInfoValue.setFont(valueFont);
        lblDiscountAmountValue.setFont(valueFont);
        
        infoPanel.add(lblOrderID);
        infoPanel.add(lblOrderIDValue);
        infoPanel.add(lblCustomer);
        infoPanel.add(lblCustomerValue);
        infoPanel.add(lblDate);
        infoPanel.add(lblDateValue);
        infoPanel.add(lblOriginalAmount);
        infoPanel.add(lblOriginalAmountValue);
        infoPanel.add(lblDiscountInfo);
        infoPanel.add(lblDiscountInfoValue);
        infoPanel.add(lblDiscountAmount);
        infoPanel.add(lblDiscountAmountValue);
        
        // Store references to value labels for later use
        lblOrderIDValue.setName("orderIdValue");
        lblCustomerValue.setName("customerValue");
        lblDateValue.setName("dateValue");
        lblOriginalAmountValue.setName("originalAmountValue");
        lblDiscountInfoValue.setName("discountInfoValue");
        lblDiscountAmountValue.setName("discountAmountValue");
        
        headerPanel.add(companyPanel, BorderLayout.NORTH);
        headerPanel.add(infoPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Chi tiết sản phẩm"));
        
        // Create table
        table = new JTable();
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Tên sản phẩm", "Số lượng", "Đơn giá (VNĐ)", "Thành tiền (VNĐ)"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        table.setModel(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(300); // Product name
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Quantity
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Unit price
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Total
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(750, 200));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        
        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBorder(BorderFactory.createTitledBorder("Tổng cộng"));
        
        lblFinalAmount = new JLabel();
        lblFinalAmount.setFont(new Font("Arial", Font.BOLD, 16));
        lblFinalAmount.setForeground(Color.RED);
        totalPanel.add(lblFinalAmount);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton btnExport = new JButton("Xuất hóa đơn PDF");
        btnExport.setPreferredSize(new Dimension(180, 35));
        btnExport.addActionListener(e -> exportToPDF());
    
        
        JButton btnClose = new JButton("Đóng");
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.addActionListener(e -> dispose());
        
        buttonPanel.add(btnExport);
        buttonPanel.add(btnClose);
        
        footerPanel.add(totalPanel, BorderLayout.NORTH);
        footerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return footerPanel;
    }
    
    private void loadInvoiceData() {
        try {
            // Load basic order info
            Component[] components = ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponents();
            
            for (Component comp : components) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    switch (label.getName() != null ? label.getName() : "") {
                        case "orderIdValue":
                            label.setText(orderId);
                            break;
                        case "customerValue":
                            label.setText(userName);
                            break;
                        case "dateValue":
                            label.setText(service.getOrderDate(orderId));
                            break;
                        case "originalAmountValue":
                            double originalAmount = service.getOriginalAmount(orderId);
                            label.setText(String.format("%.0f VNĐ", originalAmount));
                            break;
                        case "discountInfoValue":
                            String discountInfo = service.getDiscountInfo(orderId);
                            label.setText(discountInfo != null && !discountInfo.isEmpty() ? discountInfo : "Không có");
                            break;
                        case "discountAmountValue":
                            double discountAmount = service.getDiscountAmount(orderId);
                            if (discountAmount > 0) {
                                label.setText(String.format("%.0f VNĐ (%.1f%%)", 
                                    discountAmount, service.getDiscountPercent(orderId)*100));
                                label.setForeground(Color.GREEN);
                            } else {
                                label.setText("0 VNĐ");
                            }
                            break;
                    }
                }
            }
            
            // Load invoice items
            List<InvoiceItem> items = service.getInvoiceItems(orderId);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            
            for (InvoiceItem item : items) {
                model.addRow(new Object[]{
                    item.getProductName(),
                    item.getQuantity(),
                    String.format("%.0f", item.getUnitPrice()),
                    String.format("%.0f", item.getTotalItem())
                });
            }
            
            // Set final amount
            double finalAmount = service.getTotalAmount(orderId);
            lblFinalAmount.setText(String.format("THÀNH TIỀN: %.0f VNĐ", finalAmount));
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu hóa đơn: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportToPDF() {
        try {
            // TODO: Implement PDF export
            // PDFExporter.export(orderId, userName, service.getOrderDate(orderId), 
            //                   service.getInvoiceItems(orderId), service.getTotalAmount(orderId));
            JOptionPane.showMessageDialog(this, 
                "Chức năng xuất PDF đang được phát triển!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi xuất PDF: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}