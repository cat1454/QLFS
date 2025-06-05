package database;

import model.StatisticsDTO.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object cho các thống kê
 */
public class StatisticsDAO {
    
    /**
     * Lấy thống kê doanh thu theo ngày/tháng/năm
     */
    public List<RevenueByTimeDTO> getRevenueByTime(String timeType, Date startDate, Date endDate) {
        List<RevenueByTimeDTO> result = new ArrayList<>();
        String sql = "";
        
        switch (timeType.toLowerCase()) {
            case "day":
                sql = "SELECT CAST(Order_Date AS DATE) as TimePeriod, " +
                      "SUM(Total_Amount) as TotalRevenue, " +
                      "COUNT(*) as OrderCount " +
                      "FROM Orders " +
                      "WHERE Order_Date BETWEEN ? AND ? " +
                      "GROUP BY CAST(Order_Date AS DATE) " +
                      "ORDER BY TimePeriod";
                break;
            case "month":
                sql = "SELECT FORMAT(Order_Date, 'yyyy-MM') as TimePeriod, " +
                      "SUM(Total_Amount) as TotalRevenue, " +
                      "COUNT(*) as OrderCount " +
                      "FROM Orders " +
                      "WHERE Order_Date BETWEEN ? AND ? " +
                      "GROUP BY FORMAT(Order_Date, 'yyyy-MM') " +
                      "ORDER BY TimePeriod";
                break;
            case "year":
                sql = "SELECT YEAR(Order_Date) as TimePeriod, " +
                      "SUM(Total_Amount) as TotalRevenue, " +
                      "COUNT(*) as OrderCount " +
                      "FROM Orders " +
                      "WHERE Order_Date BETWEEN ? AND ? " +
                      "GROUP BY YEAR(Order_Date) " +
                      "ORDER BY TimePeriod";
                break;
        }
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                RevenueByTimeDTO dto = new RevenueByTimeDTO(
                    rs.getString("TimePeriod"),
                    rs.getDouble("TotalRevenue"),
                    rs.getInt("OrderCount")
                );
                result.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Lấy thống kê doanh thu theo sản phẩm
     */
    public List<ProductStatsDTO> getRevenueByProduct(Date startDate, Date endDate) {
        List<ProductStatsDTO> result = new ArrayList<>();
        String sql = "SELECT p.Product_ID, p.Name, p.Quantity as CurrentStock, p.Price, " +
                     "COALESCE(SUM(od.Quantity * od.Unit_Price), 0) as TotalRevenue, " +
                     "COALESCE(SUM(od.Quantity), 0) as QuantitySold " +
                     "FROM Products p " +
                     "LEFT JOIN OrderDetails od ON p.Product_ID = od.Product_ID " +
                     "LEFT JOIN Orders o ON od.Order_ID = o.Order_ID " +
                     "WHERE o.Order_Date BETWEEN ? AND ? OR o.Order_Date IS NULL " +
                     "GROUP BY p.Product_ID, p.Name, p.Quantity, p.Price " +
                     "ORDER BY TotalRevenue DESC";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ProductStatsDTO dto = new ProductStatsDTO(
                    rs.getInt("Product_ID"),
                    rs.getString("Name"),
                    rs.getDouble("TotalRevenue"),
                    rs.getInt("QuantitySold"),
                    rs.getInt("CurrentStock"),
                    rs.getDouble("Price")
                );
                result.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Lấy danh sách sản phẩm bán chạy nhất
     */
    public List<ProductStatsDTO> getBestSellingProducts(int limit, Date startDate, Date endDate) {
        List<ProductStatsDTO> result = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " p.Product_ID, p.Name, p.Quantity as CurrentStock, p.Price, " +
                     "SUM(od.Quantity * od.Unit_Price) as TotalRevenue, " +
                     "SUM(od.Quantity) as QuantitySold " +
                     "FROM Products p " +
                     "INNER JOIN OrderDetails od ON p.Product_ID = od.Product_ID " +
                     "INNER JOIN Orders o ON od.Order_ID = o.Order_ID " +
                     "WHERE o.Order_Date BETWEEN ? AND ? " +
                     "GROUP BY p.Product_ID, p.Name, p.Quantity, p.Price " +
                     "ORDER BY QuantitySold DESC";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ProductStatsDTO dto = new ProductStatsDTO(
                    rs.getInt("Product_ID"),
                    rs.getString("Name"),
                    rs.getDouble("TotalRevenue"),
                    rs.getInt("QuantitySold"),
                    rs.getInt("CurrentStock"),
                    rs.getDouble("Price")
                );
                result.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Lấy danh sách sản phẩm sắp hết hàng
     */
    public List<ProductStatsDTO> getLowStockProducts(int threshold) {
        List<ProductStatsDTO> result = new ArrayList<>();
        String sql = "SELECT Product_ID, Name, Price, Quantity as CurrentStock, 0 as TotalRevenue, 0 as QuantitySold " +
                     "FROM Products " +
                     "WHERE Quantity <= ? " +
                     "ORDER BY Quantity ASC";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, threshold);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ProductStatsDTO dto = new ProductStatsDTO(
                    rs.getInt("Product_ID"),
                    rs.getString("Name"),
                    rs.getDouble("TotalRevenue"),
                    rs.getInt("QuantitySold"),
                    rs.getInt("CurrentStock"),
                    rs.getDouble("Price")
                );
                result.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Lấy thống kê khách hàng chi tiêu nhiều nhất
     */
    public List<CustomerStatsDTO> getTopSpendingCustomers(int limit, Date startDate, Date endDate) {
        List<CustomerStatsDTO> result = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " u.Users_ID, u.User_Name, u.Email, u.PhoneNum, " +
                     "SUM(o.Total_Amount) as TotalSpent, " +
                     "COUNT(o.Order_ID) as OrderCount, " +
                     "MAX(o.Order_Date) as LastOrderDate " +
                     "FROM Users u " +
                     "INNER JOIN Orders o ON u.Users_ID = o.User_ID " +
                     "WHERE o.Order_Date BETWEEN ? AND ? " +
                     "GROUP BY u.Users_ID, u.User_Name, u.Email, u.PhoneNum " +
                     "ORDER BY TotalSpent DESC";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CustomerStatsDTO dto = new CustomerStatsDTO(
                    rs.getInt("Users_ID"),
                    rs.getString("User_Name").trim(),
                    rs.getString("Email").trim(),
                    rs.getString("PhoneNum").trim(),
                    rs.getDouble("TotalSpent"),
                    rs.getInt("OrderCount"),
                    rs.getTimestamp("LastOrderDate"),
                    null
                );
                result.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Lấy thống kê khách hàng thân thiết (mua nhiều lần nhất)
     */
    public List<CustomerStatsDTO> getLoyalCustomers(int limit, Date startDate, Date endDate) {
        List<CustomerStatsDTO> result = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " u.Users_ID, u.User_Name, u.Email, u.PhoneNum, " +
                     "SUM(o.Total_Amount) as TotalSpent, " +
                     "COUNT(o.Order_ID) as OrderCount, " +
                     "MAX(o.Order_Date) as LastOrderDate " +
                     "FROM Users u " +
                     "INNER JOIN Orders o ON u.Users_ID = o.User_ID " +
                     "WHERE o.Order_Date BETWEEN ? AND ? " +
                     "GROUP BY u.Users_ID, u.User_Name, u.Email, u.PhoneNum " +
                     "ORDER BY OrderCount DESC";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CustomerStatsDTO dto = new CustomerStatsDTO(
                    rs.getInt("Users_ID"),
                    rs.getString("User_Name").trim(),
                    rs.getString("Email").trim(),
                    rs.getString("PhoneNum").trim(),
                    rs.getDouble("TotalSpent"),
                    rs.getInt("OrderCount"),
                    rs.getTimestamp("LastOrderDate"),
                    null
                );
                result.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Lấy thống kê tổng quan
     */
    public OverallStatsDTO getOverallStats(Date startDate, Date endDate, int lowStockThreshold) {
        OverallStatsDTO result = new OverallStatsDTO();
        
        // Tổng doanh thu và số đơn hàng
        String sql1 = "SELECT SUM(Total_Amount) as TotalRevenue, COUNT(*) as TotalOrders " +
                      "FROM Orders WHERE Order_Date BETWEEN ? AND ?";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql1)) {
            
            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                result.setTotalRevenue(rs.getDouble("TotalRevenue"));
                result.setTotalOrders(rs.getInt("TotalOrders"));
                if (result.getTotalOrders() > 0) {
                    result.setAverageOrderValue(result.getTotalRevenue() / result.getTotalOrders());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Tổng số khách hàng
        String sql2 = "SELECT COUNT(DISTINCT User_ID) as TotalCustomers FROM Orders WHERE Order_Date BETWEEN ? AND ?";
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql2)) {
            
            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                result.setTotalCustomers(rs.getInt("TotalCustomers"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Tổng số sản phẩm và sản phẩm sắp hết
        String sql3 = "SELECT COUNT(*) as TotalProducts, " +
                      "SUM(CASE WHEN Quantity <= ? THEN 1 ELSE 0 END) as LowStockProducts " +
                      "FROM Products";
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql3)) {
            
            pstmt.setInt(1, lowStockThreshold);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                result.setTotalProducts(rs.getInt("TotalProducts"));
                result.setLowStockProducts(rs.getInt("LowStockProducts"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Lấy số khách hàng mới theo tháng
     */
   
public List<RevenueByTimeDTO> getNewCustomersByMonth(Date startDate, Date endDate) {
    List<RevenueByTimeDTO> result = new ArrayList<>();
    
    String sql = "SELECT FORMAT(FirstOrderDate, 'yyyy-MM') AS TimePeriod, " +
                 "COUNT(User_ID) AS OrderCount, " +
                 "0 AS TotalRevenue " +
                 "FROM ( " +
                 "    SELECT User_ID, MIN(Order_Date) AS FirstOrderDate " +
                 "    FROM Orders " +
                 "    GROUP BY User_ID " +
                 ") AS FirstOrders " +
                 "WHERE FirstOrderDate BETWEEN ? AND ? " +
                 "GROUP BY FORMAT(FirstOrderDate, 'yyyy-MM') " +
                 "ORDER BY TimePeriod";
    
    try (Connection conn = DataBaseConfig.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
        pstmt.setTimestamp(2, new Timestamp(endDate.getTime()));
        
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            RevenueByTimeDTO dto = new RevenueByTimeDTO(
                rs.getString("TimePeriod"),
                rs.getDouble("TotalRevenue"),
                rs.getInt("OrderCount")
            );
            result.add(dto);
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return result;
}
}