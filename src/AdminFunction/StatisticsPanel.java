package AdminFunction;

import service.StatisticsService;
import model.StatisticsDTO.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import utils.ChartHelper;
import org.jfree.chart.ChartPanel;


/**
 * Panel chính cho chức năng thống kê
 */
public class StatisticsPanel extends JPanel {
    
    private StatisticsService statisticsService;
    private SimpleDateFormat dateFormat;
    
    // Components
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JComboBox<String> timeTypeCombo;
    private JButton refreshButton;
    private JTabbedPane tabbedPane;
    
    // Panels for different statistics
    private JPanel overviewPanel;
    private JPanel revenuePanel;
    private JPanel productPanel;
    private JPanel customerPanel;
    private JPanel inventoryPanel;
    
    // Tables
    private JTable revenueByTimeTable;
    private JTable revenueByProductTable;
    private JTable bestSellingTable;
    private JTable topCustomersTable;
    private JTable loyalCustomersTable;
    private JTable lowStockTable;
    
    // Labels for overview
    private JLabel totalRevenueLabel;
    private JLabel totalOrdersLabel;
    private JLabel avgOrderValueLabel;
    private JLabel totalCustomersLabel;
    private JLabel newCustomersLabel;
    private JLabel lowStockCountLabel;
    
    public StatisticsPanel() {
        this.statisticsService = new StatisticsService();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        initComponents();
        setupLayout();
        setupEventHandlers();
        loadDefaultData();
    }
    
    private void initComponents() {
        // Date choosers
        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();
        
        // Set default date range (last 30 days)
        Date[] defaultRange = statisticsService.getDefaultDateRange();
        startDateChooser.setDate(defaultRange[0]);
        endDateChooser.setDate(defaultRange[1]);
        
        // Time type combo
        timeTypeCombo = new JComboBox<>(new String[]{"Ngày", "Tháng", "Năm"});
        timeTypeCombo.setSelectedItem("Ngày");
        
        // Refresh button
        refreshButton = new JButton("Làm mới");
//        refreshButton.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png"))); // Thêm icon nếu có
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Initialize panels
        initOverviewPanel();
        initRevenuePanel();
        initProductPanel();
        initCustomerPanel();
        initInventoryPanel();
        
        // Add panels to tabbed pane
        tabbedPane.addTab("Tổng quan",null, overviewPanel);
        tabbedPane.addTab("Doanh thu", null, revenuePanel);
        tabbedPane.addTab("Sản phẩm", null, productPanel);
        tabbedPane.addTab("Khách hàng", null, customerPanel);
        tabbedPane.addTab("Tồn kho", null, inventoryPanel);
    }
    
   // Thêm import cho JFreeChart

// Thêm biến instance cho chart
private ChartPanel currentChartPanel;
private JPanel chartContainer;
private JComboBox<String> chartTypeCombo;

// Cập nhật phương thức initOverviewPanel()
private void initOverviewPanel() {
    overviewPanel = new JPanel(new BorderLayout());
    
    // Create overview cards panel (giữ nguyên code cũ)
    JPanel cardsPanel = new JPanel(new GridLayout(0, 3 , 10, 10));
    cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Revenue card
    JPanel revenueCard = createOverviewCard("Tổng doanh thu", "0 VNĐ", Color.BLUE);
    totalRevenueLabel = (JLabel) ((JPanel) revenueCard.getComponent(1)).getComponent(0);
    
    // Orders card
    JPanel ordersCard = createOverviewCard("Tổng đơn hàng", "0", Color.GREEN);
    totalOrdersLabel = (JLabel) ((JPanel) ordersCard.getComponent(1)).getComponent(0);
    
    // Average order value card
    JPanel avgCard = createOverviewCard("Giá trị TB/đơn", "0 VNĐ", Color.ORANGE);
    avgOrderValueLabel = (JLabel) ((JPanel) avgCard.getComponent(1)).getComponent(0);
    
    // Customers card
    JPanel customersCard = createOverviewCard("Tổng khách hàng", "0", Color.MAGENTA);
    totalCustomersLabel = (JLabel) ((JPanel) customersCard.getComponent(1)).getComponent(0);
    
    // New customers card
    JPanel newCustomersCard = createOverviewCard("Khách hàng mới", "0", Color.CYAN);
    newCustomersLabel = (JLabel) ((JPanel) newCustomersCard.getComponent(1)).getComponent(0);
    
    // Low stock card
    JPanel lowStockCard = createOverviewCard("Sản phẩm sắp hết", "0", Color.RED);
    lowStockCountLabel = (JLabel) ((JPanel) lowStockCard.getComponent(1)).getComponent(0);
    
    cardsPanel.add(revenueCard);
    cardsPanel.add(ordersCard);
    cardsPanel.add(avgCard);
    cardsPanel.add(customersCard);
    cardsPanel.add(newCustomersCard);
    cardsPanel.add(lowStockCard);
    JScrollPane scrollPane = new JScrollPane(cardsPanel);
scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
scrollPane.setBorder(null); // nếu muốn bỏ viền

add(scrollPane, BorderLayout.CENTER);
    overviewPanel.add(scrollPane, BorderLayout.CENTER);
    
    // Tạo panel cho biểu đồ với các tùy chọn
    JPanel chartMainPanel = new JPanel(new BorderLayout());
    chartMainPanel.setBorder(BorderFactory.createTitledBorder("Biểu đồ doanh thu"));
    chartMainPanel.setPreferredSize(new Dimension(0, 300));
    
    // Panel điều khiển biểu đồ
    JPanel chartControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    chartControlPanel.add(new JLabel("Loại biểu đồ:"));
    
    chartTypeCombo = new JComboBox<>(new String[]{
        "Biểu đồ cột", "Biểu đồ đường", "Biểu đồ kết hợp"
    });
    chartTypeCombo.addActionListener(e -> updateChart());
    chartControlPanel.add(chartTypeCombo);
    
    JButton refreshChartButton = new JButton("Làm mới biểu đồ");
    refreshChartButton.addActionListener(e -> updateChart());
    chartControlPanel.add(refreshChartButton);
    
    chartMainPanel.add(chartControlPanel, BorderLayout.NORTH);
    
    // Container cho biểu đồ
    chartContainer = new JPanel(new BorderLayout());
    chartContainer.setBackground(Color.WHITE);
    
    // Tạo biểu đồ mặc định
    createDefaultChart();
    
    chartMainPanel.add(chartContainer, BorderLayout.CENTER);
    overviewPanel.add(chartMainPanel, BorderLayout.SOUTH);
}

// Phương thức tạo biểu đồ mặc định
private void createDefaultChart() {
    JLabel placeholder = new JLabel("Đang tải biểu đồ...", JLabel.CENTER);
    placeholder.setFont(new Font("Arial", Font.ITALIC, 14));
    placeholder.setForeground(Color.GRAY);
    chartContainer.removeAll();
    chartContainer.add(placeholder, BorderLayout.CENTER);
    chartContainer.revalidate();
    chartContainer.repaint();
}

// Phương thức cập nhật biểu đồ
private void updateChart() {
    Date startDate = startDateChooser.getDate();
    Date endDate = endDateChooser.getDate();
    
    if (startDate == null || endDate == null) {
        return;
    }
    
    try {
        String timeType = getTimeTypeValue();
        List<RevenueByTimeDTO> revenueData = statisticsService.getRevenueByTime(timeType, startDate, endDate);
        
        if (revenueData == null || revenueData.isEmpty()) {
            showNoDataChart();
            return;
        }
        
        String selectedChartType = (String) chartTypeCombo.getSelectedItem();
        ChartPanel newChartPanel = null;
        
        switch (selectedChartType) {
            case "Biểu đồ cột":
                newChartPanel = ChartHelper.createRevenueBarChart(revenueData, timeType);
                break;
            case "Biểu đồ đường":
                newChartPanel = ChartHelper.createRevenueLineChart(revenueData, timeType);
                break;
            case "Biểu đồ kết hợp":
                newChartPanel = ChartHelper.createCombinedChart(revenueData, timeType);
                break;
            default:
                newChartPanel = ChartHelper.createRevenueBarChart(revenueData, timeType);
        }
        
        // Cập nhật container
        chartContainer.removeAll();
        chartContainer.add(newChartPanel, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
        
        currentChartPanel = newChartPanel;
        
    } catch (Exception ex) {
        showErrorChart("Lỗi khi tải biểu đồ: " + ex.getMessage());
    }
}

// Hiển thị khi không có dữ liệu
private void showNoDataChart() {
    JLabel noDataLabel = new JLabel("Không có dữ liệu để hiển thị biểu đồ", JLabel.CENTER);
    noDataLabel.setFont(new Font("Arial", Font.ITALIC, 14));
    noDataLabel.setForeground(Color.GRAY);
    
    chartContainer.removeAll();
    chartContainer.add(noDataLabel, BorderLayout.CENTER);
    chartContainer.revalidate();
    chartContainer.repaint();
}

// Hiển thị khi có lỗi
private void showErrorChart(String errorMessage) {
    JLabel errorLabel = new JLabel(errorMessage, JLabel.CENTER);
    errorLabel.setFont(new Font("Arial", Font.ITALIC, 14));
    errorLabel.setForeground(Color.RED);
    
    chartContainer.removeAll();
    chartContainer.add(errorLabel, BorderLayout.CENTER);
    chartContainer.revalidate();
    chartContainer.repaint();
}
// Cập nhật phương thức loadOverviewData() để tự động cập nhật biểu đồ


// Thêm phương thức xuất biểu đồ
private void exportChart() {
    if (currentChartPanel == null) {
        JOptionPane.showMessageDialog(this, "Không có biểu đồ để xuất!", 
                                    "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    try {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu biểu đồ");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".png")) {
                filePath += ".png";
            }
            
            currentChartPanel.doSaveAs();
            
            JOptionPane.showMessageDialog(this, 
                "Đã lưu biểu đồ thành công!",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, 
            "Lỗi khi lưu biểu đồ: " + ex.getMessage(),
            "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private JPanel createOverviewCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(color, 2));
        card.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(Color.GRAY);
        
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(color);
        valuePanel.add(valueLabel);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void initRevenuePanel() {
        revenuePanel = new JPanel(new BorderLayout());
        
        // Revenue by time table
        String[] revenueColumns = {"Thời gian", "Doanh thu", "Số đơn hàng"};
        revenueByTimeTable = new JTable(new DefaultTableModel(revenueColumns, 0));
        JScrollPane revenueByTimeScroll = new JScrollPane(revenueByTimeTable);
        revenueByTimeScroll.setBorder(BorderFactory.createTitledBorder("Doanh thu theo thời gian"));
        
        // Revenue by product table
        String[] productRevenueColumns = {"Sản phẩm", "Doanh thu", "Số lượng bán", "Tồn kho"};
        revenueByProductTable = new JTable(new DefaultTableModel(productRevenueColumns, 0));
        JScrollPane revenueByProductScroll = new JScrollPane(revenueByProductTable);
        revenueByProductScroll.setBorder(BorderFactory.createTitledBorder("Doanh thu theo sản phẩm"));
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(revenueByTimeScroll);
        splitPane.setBottomComponent(revenueByProductScroll);
        splitPane.setDividerLocation(300);
        
        revenuePanel.add(splitPane, BorderLayout.CENTER);
    }
    
    private void initProductPanel() {
        productPanel = new JPanel(new BorderLayout());
        
        // Best selling products table
        String[] bestSellingColumns = {"Sản phẩm", "Số lượng bán", "Doanh thu", "Tồn kho"};
        bestSellingTable = new JTable(new DefaultTableModel(bestSellingColumns, 0));
        JScrollPane bestSellingScroll = new JScrollPane(bestSellingTable);
        bestSellingScroll.setBorder(BorderFactory.createTitledBorder("Top sản phẩm bán chạy"));
        
        productPanel.add(bestSellingScroll, BorderLayout.CENTER);
        
        // Add export button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton exportButton = new JButton("Xuất Excel");
        exportButton.addActionListener(e -> exportBestSellingProducts());
        buttonPanel.add(exportButton);
        
        productPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void initCustomerPanel() {
        customerPanel = new JPanel(new BorderLayout());
        
        JTabbedPane customerTabs = new JTabbedPane();
        
        // Top spending customers
        String[] topCustomerColumns = {"Khách hàng", "Email", "Tổng chi tiêu", "Số đơn hàng", "Lần mua cuối"};
        topCustomersTable = new JTable(new DefaultTableModel(topCustomerColumns, 0));
        JScrollPane topCustomersScroll = new JScrollPane(topCustomersTable);
        customerTabs.addTab("Khách chi tiêu nhiều", topCustomersScroll);
        
        // Loyal customers
        String[] loyalCustomerColumns = {"Khách hàng", "Email", "Số đơn hàng", "Tổng chi tiêu", "Lần mua cuối"};
        loyalCustomersTable = new JTable(new DefaultTableModel(loyalCustomerColumns, 0));
        JScrollPane loyalCustomersScroll = new JScrollPane(loyalCustomersTable);
        customerTabs.addTab("Khách hàng thân thiết", loyalCustomersScroll);
        
        customerPanel.add(customerTabs, BorderLayout.CENTER);
    }
    
    private void initInventoryPanel() {
        inventoryPanel = new JPanel(new BorderLayout());
        
        // Low stock products table
        String[] lowStockColumns = {"Sản phẩm", "Tồn kho hiện tại", "Giá bán", "Trạng thái"};
        lowStockTable = new JTable(new DefaultTableModel(lowStockColumns, 0));
        JScrollPane lowStockScroll = new JScrollPane(lowStockTable);
        lowStockScroll.setBorder(BorderFactory.createTitledBorder("Sản phẩm sắp hết hàng"));
        
        inventoryPanel.add(lowStockScroll, BorderLayout.CENTER);
        
        // Settings panel
        JPanel settingsPanel = new JPanel(new FlowLayout());
        settingsPanel.add(new JLabel("Ngưỡng cảnh báo:"));
        JSpinner stockThresholdSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        settingsPanel.add(stockThresholdSpinner);
        
        JButton updateThresholdButton = new JButton("Cập nhật");
        updateThresholdButton.addActionListener(e -> {
            int threshold = (Integer) stockThresholdSpinner.getValue();
            loadLowStockProducts(threshold);
        });
        settingsPanel.add(updateThresholdButton);
        
        inventoryPanel.add(settingsPanel, BorderLayout.NORTH);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc thời gian"));
        
        topPanel.add(new JLabel("Từ ngày:"));
        topPanel.add(startDateChooser);
        topPanel.add(new JLabel("Đến ngày:"));
        topPanel.add(endDateChooser);
        topPanel.add(new JLabel("Loại:"));
        topPanel.add(timeTypeCombo);
        topPanel.add(refreshButton);
        
        // Quick filter buttons
        JButton todayButton = new JButton("Hôm nay");
        JButton thisMonthButton = new JButton("Tháng này");
        JButton thisYearButton = new JButton("Năm này");
        
        todayButton.addActionListener(e -> setTodayRange());
        thisMonthButton.addActionListener(e -> setThisMonthRange());
        thisYearButton.addActionListener(e -> setThisYearRange());
        
        topPanel.add(new JSeparator(JSeparator.VERTICAL));
        topPanel.add(todayButton);
        topPanel.add(thisMonthButton);
        topPanel.add(thisYearButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
    refreshButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshAllData();
        }
    });
    
    // Tự động cập nhật biểu đồ khi thay đổi loại thời gian
    timeTypeCombo.addActionListener(e -> {
        if (tabbedPane.getSelectedIndex() == 0) { // Tab tổng quan
            updateChart();
        }
    });
    
    // Tự động cập nhật khi thay đổi ngày
    startDateChooser.addPropertyChangeListener("date", e -> {
        if (tabbedPane.getSelectedIndex() == 0) {
            updateChart();
        }
    });
    
    endDateChooser.addPropertyChangeListener("date", e -> {
        if (tabbedPane.getSelectedIndex() == 0) {
            updateChart();
        }
    });
    
    // Auto refresh when changing tabs
    tabbedPane.addChangeListener(e -> {
        int selectedIndex = tabbedPane.getSelectedIndex();
        switch (selectedIndex) {
            case 0: 
                loadOverviewData(); 
                break;
            case 1: 
                loadRevenueData(); 
                break;
            case 2: 
                loadProductData(); 
                break;
            case 3: 
                loadCustomerData(); 
                break;
            case 4: 
                loadInventoryData(); 
                break;
        }
    });
}
    private void loadDefaultData() {
        refreshAllData();
    }
    
    private void refreshAllData() {
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();
        
        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoảng thời gian hợp lệ!", 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            loadOverviewData();
            loadRevenueData();
            loadProductData();
            loadCustomerData();
            loadInventoryData();
            
//            JOptionPane.showMessageDialog(this, "Đã cập nhật dữ liệu thành công!", 
//                                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOverviewData() {
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();
        
        OverallStatsDTO stats = statisticsService.getOverallStats(startDate, endDate, 10);
        
        totalRevenueLabel.setText(statisticsService.formatCurrency(stats.getTotalRevenue()));
        totalOrdersLabel.setText(String.valueOf(stats.getTotalOrders()));
        avgOrderValueLabel.setText(statisticsService.formatCurrency(stats.getAverageOrderValue()));
        totalCustomersLabel.setText(String.valueOf(stats.getTotalCustomers()));
        lowStockCountLabel.setText(String.valueOf(stats.getLowStockProducts()));
        
        // Calculate new customers
        List<RevenueByTimeDTO> newCustomers = statisticsService.getNewCustomersByMonth(startDate, endDate);
        int totalNewCustomers = newCustomers.stream().mapToInt(RevenueByTimeDTO::getOrderCount).sum();
        newCustomersLabel.setText(String.valueOf(totalNewCustomers));
    }
    
    private void loadRevenueData() {
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();
        String timeType = getTimeTypeValue();
        
        // Load revenue by time
        DefaultTableModel revenueByTimeModel = (DefaultTableModel) revenueByTimeTable.getModel();
        revenueByTimeModel.setRowCount(0);
        
        List<RevenueByTimeDTO> revenueByTime = statisticsService.getRevenueByTime(timeType, startDate, endDate);
        for (RevenueByTimeDTO item : revenueByTime) {
            revenueByTimeModel.addRow(new Object[]{
                item.getTimePeriod(),
                statisticsService.formatCurrency(item.getTotalRevenue()),
                item.getOrderCount()
            });
        }
        
        // Load revenue by product
        DefaultTableModel revenueByProductModel = (DefaultTableModel) revenueByProductTable.getModel();
        revenueByProductModel.setRowCount(0);
        
        List<ProductStatsDTO> revenueByProduct = statisticsService.getRevenueByProduct(startDate, endDate);
        for (ProductStatsDTO item : revenueByProduct) {
            revenueByProductModel.addRow(new Object[]{
                item.getProductName(),
                statisticsService.formatCurrency(item.getTotalRevenue()),
                item.getQuantitySold(),
                item.getCurrentStock()
            });
        }
    }
    
    private void loadProductData() {
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();
        
        DefaultTableModel bestSellingModel = (DefaultTableModel) bestSellingTable.getModel();
        bestSellingModel.setRowCount(0);
        
        List<ProductStatsDTO> bestSelling = statisticsService.getBestSellingProducts(20, startDate, endDate);
        for (ProductStatsDTO item : bestSelling) {
            bestSellingModel.addRow(new Object[]{
                item.getProductName(),
                item.getQuantitySold(),
                statisticsService.formatCurrency(item.getTotalRevenue()),
                item.getCurrentStock()
            });
        }
    }
    
    private void loadCustomerData() {
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();
        
        // Load top spending customers
        DefaultTableModel topCustomersModel = (DefaultTableModel) topCustomersTable.getModel();
        topCustomersModel.setRowCount(0);
        
        List<CustomerStatsDTO> topCustomers = statisticsService.getTopSpendingCustomers(20, startDate, endDate);
        for (CustomerStatsDTO item : topCustomers) {
            topCustomersModel.addRow(new Object[]{
                item.getUserName(),
                item.getEmail(),
                statisticsService.formatCurrency(item.getTotalSpent()),
                item.getOrderCount(),
                item.getLastOrderDate() != null ? dateFormat.format(item.getLastOrderDate()) : "N/A"
            });
        }
        
        // Load loyal customers
        DefaultTableModel loyalCustomersModel = (DefaultTableModel) loyalCustomersTable.getModel();
        loyalCustomersModel.setRowCount(0);
        
        List<CustomerStatsDTO> loyalCustomers = statisticsService.getLoyalCustomers(20, startDate, endDate);
        for (CustomerStatsDTO item : loyalCustomers) {
            loyalCustomersModel.addRow(new Object[]{
                item.getUserName(),
                item.getEmail(),
                item.getOrderCount(),
                statisticsService.formatCurrency(item.getTotalSpent()),
                item.getLastOrderDate() != null ? dateFormat.format(item.getLastOrderDate()) : "N/A"
                    // Tiếp tục từ phần loadCustomerData()
            });
        }
    }
    
    private void loadInventoryData() {
        loadLowStockProducts(10); // Default threshold
    }
    
    private void loadLowStockProducts(int threshold) {
        DefaultTableModel lowStockModel = (DefaultTableModel) lowStockTable.getModel();
        lowStockModel.setRowCount(0);
        
        List<ProductStatsDTO> lowStock = statisticsService.getLowStockProducts(threshold);
        for (ProductStatsDTO item : lowStock) {
            String status = item.getCurrentStock() == 0 ? "Hết hàng" : 
                           item.getCurrentStock() <= 5 ? "Rất ít" : "Sắp hết";
            
            lowStockModel.addRow(new Object[]{
                item.getProductName(),
                item.getCurrentStock(),
                statisticsService.formatCurrency(item.getUnitPrice()),
                status
            });
        }
    }
    
    private String getTimeTypeValue() {
        String selected = (String) timeTypeCombo.getSelectedItem();
        switch (selected) {
            case "Ngày": return "day";
            case "Tháng": return "month";
            case "Năm": return "year";
            default: return "day";
        }
    }
    
    private void setTodayRange() {
        Date today = new Date();
        startDateChooser.setDate(today);
        endDateChooser.setDate(today);
        refreshAllData();
    }
    
    private void setThisMonthRange() {
        Date[] monthRange = statisticsService.getCurrentMonthRange();
        startDateChooser.setDate(monthRange[0]);
        endDateChooser.setDate(monthRange[1]);
        refreshAllData();
    }
    
    private void setThisYearRange() {
        Date[] yearRange = statisticsService.getCurrentYearRange();
        startDateChooser.setDate(yearRange[0]);
        endDateChooser.setDate(yearRange[1]);
        refreshAllData();
    }
    
    private void exportBestSellingProducts() {
        try {
            // Tạo dialog chọn file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu báo cáo sản phẩm bán chạy");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }
                
                // Export data (cần implement ExcelExporter)
                exportToExcel(filePath);
                
                JOptionPane.showMessageDialog(this, 
                    "Đã xuất báo cáo thành công!\nFile: " + filePath,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi xuất file: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportToExcel(String filePath) {
        // Placeholder cho chức năng export Excel
        // Cần implement với Apache POI hoặc thư viện tương tự
        System.out.println("Exporting to: " + filePath);
    }
    
    /**
     * Method để refresh dữ liệu từ bên ngoài
     */
    public void refreshData() {
        refreshAllData();
    }
    
    /**
     * Method để set khoảng thời gian từ bên ngoài
     * @param startDate
     * @param endDate
     */
    public void setDateRange(Date startDate, Date endDate) {
        startDateChooser.setDate(startDate);
        endDateChooser.setDate(endDate);
        refreshAllData();
    }
    
    /**
     * Method để lấy thống kê tổng quan hiện tại
     * @return 
     */
    public OverallStatsDTO getCurrentOverallStats() {
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();
        return statisticsService.getOverallStats(startDate, endDate, 10);
    }
    
    /**
     * Method để hiển thị dialog báo cáo chi tiết
     */
    private void showDetailedReport() {
        JDialog reportDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                          "Báo cáo chi tiết", true);
        reportDialog.setSize(800, 600);
        reportDialog.setLocationRelativeTo(this);
        
        // Tạo nội dung báo cáo
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        StringBuilder report = new StringBuilder();
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();
        
        report.append("BÁO CÁO THỐNG KÊ CHI TIẾT\n");
        report.append("=====================================\n");
        report.append("Thời gian: ").append(dateFormat.format(startDate))
              .append(" - ").append(dateFormat.format(endDate)).append("\n\n");
        
        OverallStatsDTO stats = getCurrentOverallStats();
        report.append("TỔNG QUAN:\n");
        report.append("- Tổng doanh thu: ").append(statisticsService.formatCurrency(stats.getTotalRevenue())).append("\n");
        report.append("- Tổng đơn hàng: ").append(stats.getTotalOrders()).append("\n");
        report.append("- Giá trị TB/đơn: ").append(statisticsService.formatCurrency(stats.getAverageOrderValue())).append("\n");
        report.append("- Tổng khách hàng: ").append(stats.getTotalCustomers()).append("\n");
        report.append("- Sản phẩm sắp hết: ").append(stats.getLowStockProducts()).append("\n\n");
        
        // Thêm top sản phẩm bán chạy
        report.append("TOP 5 SẢN PHẨM BÁN CHẠY:\n");
        List<ProductStatsDTO> topProducts = statisticsService.getBestSellingProducts(5, startDate, endDate);
        for (int i = 0; i < topProducts.size(); i++) {
            ProductStatsDTO product = topProducts.get(i);
            report.append(String.format("%d. %s - Bán: %d - Doanh thu: %s\n", 
                i + 1, product.getProductName(), product.getQuantitySold(),
                statisticsService.formatCurrency(product.getTotalRevenue())));
        }
        
        reportArea.setText(report.toString());
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        reportDialog.add(scrollPane, BorderLayout.CENTER);
        
        // Thêm nút đóng
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> reportDialog.dispose());
        buttonPanel.add(closeButton);
        
        JButton printButton = new JButton("In báo cáo");
        printButton.addActionListener(e -> {
            try {
                reportArea.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(reportDialog, 
                    "Lỗi khi in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(printButton);
        
        reportDialog.add(buttonPanel, BorderLayout.SOUTH);
        reportDialog.setVisible(true);
    }
    
    /**
     * Method để thêm menu context cho các bảng
     */
    private void setupTableContextMenus() {
        JPopupMenu tableMenu = new JPopupMenu();
        
        JMenuItem detailItem = new JMenuItem("Xem chi tiết");
        detailItem.addActionListener(e -> showDetailedReport());
        tableMenu.add(detailItem);
        
        JMenuItem exportItem = new JMenuItem("Xuất Excel");
        exportItem.addActionListener(e -> exportBestSellingProducts());
        tableMenu.add(exportItem);
        
        // Thêm menu cho các bảng
        revenueByTimeTable.setComponentPopupMenu(tableMenu);
        revenueByProductTable.setComponentPopupMenu(tableMenu);
        bestSellingTable.setComponentPopupMenu(tableMenu);
        topCustomersTable.setComponentPopupMenu(tableMenu);
        loyalCustomersTable.setComponentPopupMenu(tableMenu);
        lowStockTable.setComponentPopupMenu(tableMenu);
    }
    
    /**
     * Method để format bảng (màu sắc, font chữ)
     */
    private void formatTables() {
        JTable[] tables = {revenueByTimeTable, revenueByProductTable, bestSellingTable,
                          topCustomersTable, loyalCustomersTable, lowStockTable};
        
        for (JTable table : tables) {
            table.setRowHeight(25);
            table.setFont(new Font("Arial", Font.PLAIN, 12));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            table.getTableHeader().setBackground(new Color(230, 230, 230));
            table.setSelectionBackground(new Color(184, 207, 229));
            table.setGridColor(Color.LIGHT_GRAY);
            table.setShowGrid(true);
        }
    }
    
    /**
     * Constructor với tham số để khởi tạo với dữ liệu cụ thể
     */
    public StatisticsPanel(Date startDate, Date endDate) {
        this();
        setDateRange(startDate, endDate);
    }
    // Thêm vào cuối constructor của StatisticsPanel
private void setupChartContextMenu() {
    // Tạo context menu cho biểu đồ
    JPopupMenu chartMenu = new JPopupMenu();
    
    JMenuItem saveImageItem = new JMenuItem("Lưu hình ảnh");
    saveImageItem.setIcon(new ImageIcon(getClass().getResource("/icons/save.png"))); // Nếu có icon
    saveImageItem.addActionListener(e -> exportChart());
    chartMenu.add(saveImageItem);
    
    JMenuItem printItem = new JMenuItem("In biểu đồ");
    printItem.setIcon(new ImageIcon(getClass().getResource("/icons/print.png"))); // Nếu có icon
    printItem.addActionListener(e -> printChart());
    chartMenu.add(printItem);
    
    chartMenu.addSeparator();
    
    JMenuItem refreshItem = new JMenuItem("Làm mới");
    refreshItem.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png"))); // Nếu có icon
    refreshItem.addActionListener(e -> updateChart());
    chartMenu.add(refreshItem);
    
    JMenuItem propertiesItem = new JMenuItem("Thuộc tính biểu đồ");
    propertiesItem.addActionListener(e -> showChartProperties());
    chartMenu.add(propertiesItem);
    
    // Gán menu cho chart container
    chartContainer.setComponentPopupMenu(chartMenu);
}

private void printChart() {
    if (currentChartPanel == null) {
        JOptionPane.showMessageDialog(this, "Không có biểu đồ để in!", 
                                    "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    try {
        currentChartPanel.createChartPrintJob();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, 
            "Lỗi khi in biểu đồ: " + ex.getMessage(),
            "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

private void showChartProperties() {
    JDialog propertiesDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                          "Thuộc tính biểu đồ", true);
    propertiesDialog.setSize(400, 300);
    propertiesDialog.setLocationRelativeTo(this);
    
    JPanel propertiesPanel = new JPanel(new GridLayout(6, 2, 5, 5));
    propertiesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Tiêu đề biểu đồ
    propertiesPanel.add(new JLabel("Tiêu đề:"));
    JTextField titleField = new JTextField("Biểu đồ doanh thu");
    propertiesPanel.add(titleField);
    
    // Màu nền
    propertiesPanel.add(new JLabel("Màu nền:"));
    JButton backgroundColorButton = new JButton();
    backgroundColorButton.setBackground(Color.WHITE);
    backgroundColorButton.addActionListener(e -> {
        Color color = JColorChooser.showDialog(propertiesDialog, "Chọn màu nền", Color.WHITE);
        if (color != null) {
            backgroundColorButton.setBackground(color);
        }
    });
    propertiesPanel.add(backgroundColorButton);
    
    // Hiển thị lưới
    propertiesPanel.add(new JLabel("Hiển thị lưới:"));
    JCheckBox gridCheckBox = new JCheckBox("", true);
    propertiesPanel.add(gridCheckBox);
    
    // Hiển thị chú thích
    propertiesPanel.add(new JLabel("Hiển thị chú thích:"));
    JCheckBox legendCheckBox = new JCheckBox("", true);
    propertiesPanel.add(legendCheckBox);
    
    // Định dạng số
}
}