package utils;

import model.StatisticsDTO.RevenueByTimeDTO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Helper class để tạo và quản lý biểu đồ thống kê
 */
public class ChartHelper {
    
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,###");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM");
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MM/yyyy");
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    
    /**
     * Tạo biểu đồ cột doanh thu
     */
    public static ChartPanel createRevenueBarChart(List<RevenueByTimeDTO> data, String timeType) {
        CategoryDataset dataset = createRevenueDataset(data);
        
        String title = "Biểu đồ doanh thu theo " + getTimeTypeLabel(timeType);
        String xAxisLabel = getTimeTypeLabel(timeType);
        String yAxisLabel = "Doanh thu (VNĐ)";
        
        JFreeChart chart = ChartFactory.createBarChart(
            title,
            xAxisLabel,
            yAxisLabel,
            dataset,
            PlotOrientation.VERTICAL,
            true,  // include legend
            true,  // tooltips
            false  // urls
        );
        
        // Tùy chỉnh biểu đồ
        customizeBarChart(chart);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        chartPanel.setMouseWheelEnabled(true);
        
        return chartPanel;
    }
    
    /**
     * Tạo biểu đồ đường doanh thu
     */
    public static ChartPanel createRevenueLineChart(List<RevenueByTimeDTO> data, String timeType) {
        CategoryDataset dataset = createRevenueDataset(data);
        
        String title = "Xu hướng doanh thu theo " + getTimeTypeLabel(timeType);
        String xAxisLabel = getTimeTypeLabel(timeType);
        String yAxisLabel = "Doanh thu (VNĐ)";
        
        JFreeChart chart = ChartFactory.createLineChart(
            title,
            xAxisLabel,
            yAxisLabel,
            dataset,
            PlotOrientation.VERTICAL,
            true,  // include legend
            true,  // tooltips
            false  // urls
        );
        
        // Tùy chỉnh biểu đồ
        customizeLineChart(chart);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        chartPanel.setMouseWheelEnabled(true);
        
        return chartPanel;
    }
    
    /**
     * Tạo biểu đồ kết hợp (cột + đường)
     */
    public static ChartPanel createCombinedChart(List<RevenueByTimeDTO> data, String timeType) {
        CategoryDataset revenueDataset = createRevenueDataset(data);
        CategoryDataset orderDataset = createOrderCountDataset(data);
        
        String title = "Doanh thu và số đơn hàng theo " + getTimeTypeLabel(timeType);
        String xAxisLabel = getTimeTypeLabel(timeType);
        
        JFreeChart chart = ChartFactory.createBarChart(
            title,
            xAxisLabel,
            "Doanh thu (VNĐ)",
            revenueDataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Tùy chỉnh biểu đồ kết hợp
        customizeCombinedChart(chart, orderDataset);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        chartPanel.setMouseWheelEnabled(true);
        
        return chartPanel;
    }
    
    /**
     * Tạo dataset cho doanh thu
     */
    private static CategoryDataset createRevenueDataset(List<RevenueByTimeDTO> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (RevenueByTimeDTO item : data) {
            dataset.addValue(item.getTotalRevenue(), "Doanh thu", item.getTimePeriod());
        }
        
        return dataset;
    }
    
    /**
     * Tạo dataset cho số đơn hàng
     */
    private static CategoryDataset createOrderCountDataset(List<RevenueByTimeDTO> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (RevenueByTimeDTO item : data) {
            dataset.addValue(item.getOrderCount(), "Số đơn hàng", item.getTimePeriod());
        }
        
        return dataset;
    }
    
    /**
     * Tùy chỉnh biểu đồ cột
     */
    private static void customizeBarChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        
        // Màu nền
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Tùy chỉnh renderer
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189)); // Màu xanh dương
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        
        // Tùy chỉnh trục Y
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#,###"));
        
        // Tùy chỉnh trục X
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
            org.jfree.chart.axis.CategoryLabelPositions.UP_45);
        
        // Font chữ
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 12));
        rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    /**
     * Tùy chỉnh biểu đồ đường
     */
    private static void customizeLineChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        
        // Màu nền
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Tùy chỉnh renderer
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesPaint(0, new Color(255, 127, 80)); // Màu cam
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesFilled(0, true);
        
        // Tùy chỉnh trục
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#,###"));
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
            org.jfree.chart.axis.CategoryLabelPositions.UP_45);
        
        // Font chữ
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 12));
        rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    /**
     * Tùy chỉnh biểu đồ kết hợp
     */
    private static void customizeCombinedChart(JFreeChart chart, CategoryDataset orderDataset) {
        CategoryPlot plot = chart.getCategoryPlot();
        
        // Tạo trục Y thứ 2 cho số đơn hàng
        NumberAxis secondaryAxis = new NumberAxis("Số đơn hàng");
        secondaryAxis.setNumberFormatOverride(new DecimalFormat("#,###"));
        plot.setRangeAxis(1, secondaryAxis);
        
        // Tạo renderer cho đường số đơn hàng
        LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
        lineRenderer.setSeriesPaint(0, Color.RED);
        lineRenderer.setSeriesStroke(0, new BasicStroke(2.0f));
        lineRenderer.setSeriesShapesVisible(0, true);
        
        plot.setRenderer(1, lineRenderer);
        plot.setDataset(1, orderDataset);
        plot.mapDatasetToRangeAxis(1, 1);
        
        // Tùy chỉnh màu sắc
        BarRenderer barRenderer = (BarRenderer) plot.getRenderer(0);
        barRenderer.setSeriesPaint(0, new Color(79, 129, 189));
        
        // Màu nền
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Font chữ
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
    }
    
    /**
     * Lấy nhãn loại thời gian
     */
    private static String getTimeTypeLabel(String timeType) {
        switch (timeType.toLowerCase()) {
            case "day": return "Ngày";
            case "month": return "Tháng";
            case "year": return "Năm";
            default: return "Thời gian";
        }
    }
    
    /**
     * Format tooltip cho biểu đồ
     */
    public static String formatTooltip(String category, double value, String timeType) {
        return String.format("%s: %s VNĐ", category, CURRENCY_FORMAT.format(value));
    }
}