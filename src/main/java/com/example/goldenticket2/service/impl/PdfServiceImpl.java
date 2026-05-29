package com.example.goldenticket2.service.impl;

import com.example.goldenticket2.entity.Ticket;
import com.example.goldenticket2.service.PdfService;
import com.example.goldenticket2.service.QrService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final QrService qrService;

    @Value("${app.ticket.storage-path}")
    private String storagePath;

    @Override
    public String generateTicketPdf(Ticket ticket) {
        try {
            Path directory = Path.of(storagePath).toAbsolutePath();
            Files.createDirectories(directory);
            Path file = directory.resolve(ticket.getTicketNumber() + ".pdf");

            PdfWriter writer = new PdfWriter(file.toString());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            byte[] qr = qrService.generatePng(ticket.getQrCode(), 240, 240);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            document.add(new Paragraph("GoldenTicket")
                    .setFontSize(24)
                    .setFont(boldFont)
                    .setFontColor(ColorConstants.ORANGE)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(ticket.getEvent().getName())
                    .setFontSize(18)
                    .setFont(boldFont)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Ticket: " + ticket.getTicketNumber()).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Attendee: " + ticket.getOwner().getFullName()));
            document.add(new Paragraph("Date: " + ticket.getEvent().getEventDate()));
            document.add(new Paragraph("Location: " + ticket.getEvent().getLocation()));
            document.add(new Image(ImageDataFactory.create(qr)).setAutoScale(true).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Present this QR at access control. Each ticket can be used once.")
                    .setFontSize(10)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            document.close();
            return file.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to generate ticket PDF", ex);
        }
    }
}
