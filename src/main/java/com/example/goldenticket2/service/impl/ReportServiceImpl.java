package com.example.goldenticket2.service.impl;


import com.example.goldenticket2.entity.Event;
import com.example.goldenticket2.entity.Payment;
import com.example.goldenticket2.entity.PaymentStatus;
import com.example.goldenticket2.entity.User;
import com.example.goldenticket2.repository.EventRepository;
import com.example.goldenticket2.repository.PaymentRepository;
import com.example.goldenticket2.repository.UserRepository;
import com.example.goldenticket2.service.ReportService;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public byte[] generateExcelReport() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            var summary = workbook.createSheet("Summary");
            Row title = summary.createRow(0);
            title.createCell(0).setCellValue("GoldenTicket Report");
            title.getCell(0).setCellStyle(headerStyle);
            long approvedPayments = paymentRepository.findAll().stream().filter(p -> p.getStatus() == PaymentStatus.APPROVED).count();
            BigDecimal revenue = paymentRepository.findAll().stream()
                    .filter(p -> p.getStatus() == PaymentStatus.APPROVED)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            summary.createRow(2).createCell(0).setCellValue("Generated at");
            summary.getRow(2).createCell(1).setCellValue(Instant.now().toString());
            summary.createRow(3).createCell(0).setCellValue("Approved payments");
            summary.getRow(3).createCell(1).setCellValue(approvedPayments);
            summary.createRow(4).createCell(0).setCellValue("Revenue COP");
            summary.getRow(4).createCell(1).setCellValue(revenue.doubleValue());

            var sales = workbook.createSheet("Sales");
            writeHeader(sales.createRow(0), headerStyle, "Payment ID", "Reference", "Event", "User", "Status", "Amount");
            int rowIndex = 1;
            for (Payment payment : paymentRepository.findAll()) {
                Row row = sales.createRow(rowIndex++);
                row.createCell(0).setCellValue(payment.getId());
                row.createCell(1).setCellValue(payment.getReference());
                row.createCell(2).setCellValue(payment.getPurchase().getEvent().getName());
                row.createCell(3).setCellValue(payment.getPurchase().getUser().getEmail());
                row.createCell(4).setCellValue(payment.getStatus().name());
                row.createCell(5).setCellValue(payment.getAmount().doubleValue());
            }

            var users = workbook.createSheet("Users");
            writeHeader(users.createRow(0), headerStyle, "ID", "Name", "Email", "Enabled");
            rowIndex = 1;
            for (User user : userRepository.findAll()) {
                Row row = users.createRow(rowIndex++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getFullName());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.isEnabled());
            }

            var events = workbook.createSheet("Events");
            writeHeader(events.createRow(0), headerStyle, "ID", "Name", "Date", "Location", "Capacity", "Available", "Status");
            rowIndex = 1;
            for (Event event : eventRepository.findAll()) {
                Row row = events.createRow(rowIndex++);
                row.createCell(0).setCellValue(event.getId());
                row.createCell(1).setCellValue(event.getName());
                row.createCell(2).setCellValue(event.getEventDate().toString());
                row.createCell(3).setCellValue(event.getLocation());
                row.createCell(4).setCellValue(event.getCapacity());
                row.createCell(5).setCellValue(event.getAvailableCapacity());
                row.createCell(6).setCellValue(event.getStatus().name());
            }

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                var sheet = workbook.getSheetAt(i);
                for (int c = 0; c < 8; c++) {
                    sheet.autoSizeColumn(c);
                }
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to generate Excel report", ex);
        }
    }

    private void writeHeader(Row row, CellStyle style, String... headers) {
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
            row.getCell(i).setCellStyle(style);
        }
    }
}

