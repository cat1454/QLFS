package service;

import database.StatisticsDAO;
import model.StatisticsDTO.*;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

/**
 * Service class xử lý logic nghiệp vụ cho thống kê
 */
public class StatisticsService {
    
    private StatisticsDAO statisticsDAO;
    private DecimalFormat currencyFormat;
    
    public StatisticsService() {
        this.statisticsDAO = new StatisticsDAO();
        this.currencyFormat = new DecimalFormat("#,###");
    }
    
    /**
     * Lấy thống kê doanh thu theo thời gian với validation
     */
    public List<RevenueByTimeDTO> getRevenueByTime(String timeType, Date startDate, Date endDate) {
        validateDateRange(startDate, endDate);
        return statisticsDAO.getRevenueByTime(timeType, startDate, endDate);
    }
    
    /**
     * Lấy thống kê doanh thu theo sản phẩm
     */
    public List<ProductStatsDTO> getRevenueByProduct(Date startDate, Date endDate) {
        validateDateRange(startDate, endDate);
        return statisticsDAO.getRevenueByProduct(startDate, endDate);
    }
    
    /**
     * Lấy top sản phẩm bán chạy nhất
     */
    public List<ProductStatsDTO> getBestSellingProducts(int limit, Date startDate, Date endDate) {
        validateDateRange(startDate, endDate);
        if (limit <= 0) limit = 10; // Mặc định 10
        return statisticsDAO.getBestSellingProducts(limit, startDate, endDate);
    }
    
    /**
     * Lấy sản phẩm sắp hết hàng
     */
    public List<ProductStatsDTO> getLowStockProducts(int threshold) {
        if (threshold < 0) threshold = 10; // Mặc định 10
        return statisticsDAO.getLowStockProducts(threshold);
    }
    
    /**
     * Lấy top khách hàng chi tiêu nhiều nhất
     */
    public List<CustomerStatsDTO> getTopSpendingCustomers(int limit, Date startDate, Date endDate) {
        validateDateRange(startDate, endDate);
        if (limit <= 0) limit = 10;
        return statisticsDAO.getTopSpendingCustomers(limit, startDate, endDate);
    }
    
    /**
     * Lấy khách hàng thân thiết
     */
    public List<CustomerStatsDTO> getLoyalCustomers(int limit, Date startDate, Date endDate) {
        validateDateRange(startDate, endDate);
        if (limit <= 0) limit = 10;
        return statisticsDAO.getLoyalCustomers(limit, startDate, endDate);
    }
    
    /**
     * Lấy thống kê tổng quan
     */
    public OverallStatsDTO getOverallStats(Date startDate, Date endDate, int lowStockThreshold) {
        validateDateRange(startDate, endDate);
        if (lowStockThreshold < 0) lowStockThreshold = 10;
        return statisticsDAO.getOverallStats(startDate, endDate, lowStockThreshold);
    }
    
    /**
     * Lấy số khách hàng mới theo tháng
     */
    public List<RevenueByTimeDTO> getNewCustomersByMonth(Date startDate, Date endDate) {
        validateDateRange(startDate, endDate);
        return statisticsDAO.getNewCustomersByMonth(startDate, endDate);
    }
    
    /**
     * Tạo dữ liệu cho biểu đồ cột doanh thu
     */
    public double[][] getRevenueChartData(String timeType, Date startDate, Date endDate) {
        List<RevenueByTimeDTO> data = getRevenueByTime(timeType, startDate, endDate);
        double[][] chartData = new double[data.size()][2];
        
        for (int i = 0; i < data.size(); i++) {
            chartData[i][0] = i; // X axis (time index)
            chartData[i][1] = data.get(i).getTotalRevenue(); // Y axis (revenue)
        }
        
        return chartData;
    }
    
    /**
     * Tạo labels cho biểu đồ
     */
    public String[] getRevenueChartLabels(String timeType, Date startDate, Date endDate) {
        List<RevenueByTimeDTO> data = getRevenueByTime(timeType, startDate, endDate);
        String[] labels = new String[data.size()];
        
        for (int i = 0; i < data.size(); i++) {
            labels[i] = data.get(i).getTimePeriod();
        }
        
        return labels;
    }
    
    /**
     * Format tiền tệ
     */
    public String formatCurrency(double amount) {
        return currencyFormat.format(amount) + " VNĐ";
    }
    
    /**
     * Tính phần trăm thay đổi doanh thu
     */
    public double calculateRevenueGrowth(Date currentPeriodStart, Date currentPeriodEnd,
                                       Date previousPeriodStart, Date previousPeriodEnd) {
        
        List<RevenueByTimeDTO> currentData = getRevenueByTime("day", currentPeriodStart, currentPeriodEnd);
        List<RevenueByTimeDTO> previousData = getRevenueByTime("day", previousPeriodStart, previousPeriodEnd);
        
        double currentRevenue = currentData.stream().mapToDouble(RevenueByTimeDTO::getTotalRevenue).sum();
        double previousRevenue = previousData.stream().mapToDouble(RevenueByTimeDTO::getTotalRevenue).sum();
        
        if (previousRevenue == 0) return 0;
        return ((currentRevenue - previousRevenue) / previousRevenue) * 100;
    }
    
    /**
     * Lấy khoảng thời gian mặc định (30 ngày gần nhất)
     */
    public Date[] getDefaultDateRange() {
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date startDate = cal.getTime();
        
        return new Date[]{startDate, endDate};
    }
    
    /**
     * Lấy khoảng thời gian tháng hiện tại
     */
    public Date[] getCurrentMonthRange() {
        Calendar cal = Calendar.getInstance();
        
        // Ngày cuối tháng
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();
        
        // Ngày đầu tháng
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        
        return new Date[]{startDate, endDate};
    }
    
    /**
     * Lấy khoảng thời gian năm hiện tại
     */
    public Date[] getCurrentYearRange() {
        Calendar cal = Calendar.getInstance();
        
        // Ngày cuối năm
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        Date endDate = cal.getTime();
        
        // Ngày đầu năm
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        
        return new Date[]{startDate, endDate};
    }
    
    /**
     * Validate khoảng thời gian
     */
    private void validateDateRange(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống");
        }
        
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
        }
        
        // Kiểm tra khoảng thời gian quá dài (>2 năm)
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.YEAR, 2);
        
        if (endDate.after(cal.getTime())) {
            throw new IllegalArgumentException("Khoảng thời gian không được vượt quá 2 năm");
        }
    }
    
    /**
     * Kiểm tra xem có dữ liệu trong khoảng thời gian không
     */
    public boolean hasDataInRange(Date startDate, Date endDate) {
        OverallStatsDTO stats = getOverallStats(startDate, endDate, 0);
        return stats.getTotalOrders() > 0;
    }
    
    /**
     * Tính tỷ lệ chuyển đổi khách hàng mới
     */
    public double calculateNewCustomerRate(Date startDate, Date endDate) {
        OverallStatsDTO stats = getOverallStats(startDate, endDate, 0);
        List<RevenueByTimeDTO> newCustomers = getNewCustomersByMonth(startDate, endDate);
        
        int totalNewCustomers = newCustomers.stream().mapToInt(RevenueByTimeDTO::getOrderCount).sum();
        
        if (stats.getTotalCustomers() == 0) return 0;
        return ((double) totalNewCustomers / stats.getTotalCustomers()) * 100;
    }
    
    /**
     * Lấy thống kê so sánh theo kỳ
     */
    public ComparisonStatsDTO getComparisonStats(Date currentStart, Date currentEnd,
                                               Date previousStart, Date previousEnd) {
        OverallStatsDTO currentStats = getOverallStats(currentStart, currentEnd, 10);
        OverallStatsDTO previousStats = getOverallStats(previousStart, previousEnd, 10);
        
        return new ComparisonStatsDTO(currentStats, previousStats);
    }
    
    /**
     * DTO cho thống kê so sánh
     */
    public static class ComparisonStatsDTO {
        private OverallStatsDTO currentPeriod;
        private OverallStatsDTO previousPeriod;
        private double revenueGrowth;
        private double orderGrowth;
        private double customerGrowth;
        
        public ComparisonStatsDTO(OverallStatsDTO current, OverallStatsDTO previous) {
            this.currentPeriod = current;
            this.previousPeriod = previous;
            
            // Tính tỷ lệ tăng trưởng
            this.revenueGrowth = calculateGrowthRate(
                current.getTotalRevenue(), previous.getTotalRevenue());
            this.orderGrowth = calculateGrowthRate(
                current.getTotalOrders(), previous.getTotalOrders());
            this.customerGrowth = calculateGrowthRate(
                current.getTotalCustomers(), previous.getTotalCustomers());
        }
        
        private double calculateGrowthRate(double current, double previous) {
            if (previous == 0) return current > 0 ? 100 : 0;
            return ((current - previous) / previous) * 100;
        }
        
        // Getters
        public OverallStatsDTO getCurrentPeriod() { return currentPeriod; }
        public OverallStatsDTO getPreviousPeriod() { return previousPeriod; }
        public double getRevenueGrowth() { return revenueGrowth; }
        public double getOrderGrowth() { return orderGrowth; }
        public double getCustomerGrowth() { return customerGrowth; }
    }
}