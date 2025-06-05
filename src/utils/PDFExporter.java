///*
// * Click nbfs:nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs:nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package utils;
//
//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.*;
//import java.awt.Font;
//import java.io.FileOutputStream;
//import java.util.List;
//import javax.swing.text.Document;
//import model.InvoiceItem;
//
//public class PDFExporter {
//    public static void export(String orderId, String userName, String orderDate, List<InvoiceItem> items, double totalAmount) {
//        Document document = new Document() {};
//        try {
//            PdfWriter.getInstance(document, new FileOutputStream("Invoice_" + orderId + ".pdf"));
//            document.open();
//
//             Tiêu đề
//            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
//            Paragraph title = new Paragraph("HÓA ĐƠN MUA HÀNG", titleFont);
//            title.setAlignment(Element.ALIGN_CENTER);
//            document.add(title);
//            document.add(new Paragraph(" "));
//
//             Thông tin
//            document.add(new Paragraph("Mã đơn hàng: " + orderId));
//            document.add(new Paragraph("Khách hàng: " + userName));
//            document.add(new Paragraph("Ngày đặt: " + orderDate));
//            document.add(new Paragraph(" "));
//
//             Bảng sản phẩm
//            PdfPTable table = new PdfPTable(4);
//            table.addCell("Tên sản phẩm");
//            table.addCell("Số lượng");
//            table.addCell("Đơn giá");
//            table.addCell("Thành tiền");
//
//            for (InvoiceItem item : items) {
//                table.addCell(item.getProductName());
//                table.addCell(String.valueOf(item.getQuantity()));
//                table.addCell(String.valueOf(item.getUnitPrice()));
//                table.addCell(String.valueOf(item.getTotalItem()));
//            }
//            document.add(table);
//
//            document.add(new Paragraph(" "));
//            document.add(new Paragraph("Tổng tiền: " + totalAmount + " VND", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
//
//            document.close();
//            JOptionPane.showMessageDialog(null, "Đã xuất hóa đơn PDF thành công!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(null, "Lỗi khi xuất PDF!");
//        }
//    }
//}
