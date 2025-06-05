/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author PHU
 */
public class OrderUtils {
    public static String generateOrderID(String customerName, int customerId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
        String dateStr = LocalDateTime.now().format(formatter);
        String normalized = customerName.trim().toLowerCase().replaceAll("\\s+", "");
        return normalized + "_" + dateStr + "_" + customerId;
    }
}


