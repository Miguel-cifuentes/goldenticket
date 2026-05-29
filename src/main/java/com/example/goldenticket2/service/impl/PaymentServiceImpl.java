package com.example.goldenticket2.service.impl;

import com.example.goldenticket2.dto.PaymentRequest;
import com.example.goldenticket2.dto.PaymentResponse;
import com.example.goldenticket2.entity.Event;
import com.example.goldenticket2.entity.EventStatus;
import com.example.goldenticket2.entity.Payment;
import com.example.goldenticket2.entity.PaymentStatus;
import com.example.goldenticket2.entity.Purchase;
import com.example.goldenticket2.entity.PurchaseStatus;
import com.example.goldenticket2.entity.Ticket;
import com.example.goldenticket2.entity.TicketStatus;
import com.example.goldenticket2.entity.TicketType;
import com.example.goldenticket2.entity.User;
import com.example.goldenticket2.exception.BadRequestException;
import com.example.goldenticket2.exception.ResourceNotFoundException;
import com.example.goldenticket2.repository.EventRepository;
import com.example.goldenticket2.repository.PaymentRepository;
import com.example.goldenticket2.repository.PurchaseRepository;
import com.example.goldenticket2.repository.TicketRepository;
import com.example.goldenticket2.repository.TicketTypeRepository;
import com.example.goldenticket2.service.PaymentService;
import com.example.goldenticket2.service.PdfService;
import com.example.goldenticket2.service.WompiService;
import com.example.goldenticket2.util.SecurityUtils;
import com.example.goldenticket2.util.TicketNumberGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final PurchaseRepository purchaseRepository;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final WompiService wompiService;
    private final PdfService pdfService;

    @Override
    @Transactional
    public PaymentResponse createPurchase(PaymentRequest request) {
        User user = SecurityUtils.currentUser();
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + request.eventId()));
        TicketType ticketType = ticketTypeRepository.findById(request.ticketTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket type not found with id " + request.ticketTypeId()));

        if (!ticketType.getEvent().getId().equals(event.getId())) {
            throw new BadRequestException("Ticket type does not belong to this event");
        }
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new BadRequestException("Event is not available for purchases");
        }
        if (ticketType.getAvailableStock() < request.quantity() || event.getAvailableCapacity() < request.quantity()) {
            throw new BadRequestException("Not enough tickets available");
        }

        ticketType.setAvailableStock(ticketType.getAvailableStock() - request.quantity());
        event.setAvailableCapacity(event.getAvailableCapacity() - request.quantity());
        if (event.getAvailableCapacity() == 0) {
            event.setStatus(EventStatus.SOLD_OUT);
        }

        BigDecimal total = ticketType.getPrice().multiply(BigDecimal.valueOf(request.quantity()));
        Purchase purchase = Purchase.builder()
                .reference("GT-" + UUID.randomUUID())
                .user(user)
                .event(event)
                .ticketType(ticketType)
                .quantity(request.quantity())
                .totalAmount(total)
                .status(PurchaseStatus.PAYMENT_PENDING)
                .build();

        Payment payment = Payment.builder()
                .purchase(purchase)
                .reference(purchase.getReference())
                .status(PaymentStatus.PENDING)
                .amount(total)
                .amountInCents(total.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValueExact())
                .build();
        purchase.setPayment(payment);
        payment.setCheckoutUrl(wompiService.buildCheckoutUrl(payment));
        Purchase saved = purchaseRepository.save(purchase);
        return toResponse(saved.getPayment());
    }

    @Override
    @Transactional
    public PaymentResponse processWebhook(JsonNode payload) {
        //if (!wompiService.isValidEventSignature(payload)) {
            //throw new BadRequestException("Invalid Wompi webhook signature");
        //}
        JsonNode transaction = payload.path("data").path("transaction");
        String reference = transaction.path("reference").asText();
        String transactionId = transaction.path("id").asText();
        String status = transaction.path("status").asText("PENDING");
        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for reference " + reference));
        payment.setWompiTransactionId(transactionId);
        payment.setRawGatewayResponse(payload.toString());
        applyGatewayStatus(payment, status);
        return toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(String transactionId) {
        JsonNode response = wompiService.verifyTransaction(transactionId);
        JsonNode transaction = response.path("data");
        String reference = transaction.path("reference").asText();
        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for Wompi transaction " + transactionId));
        payment.setWompiTransactionId(transactionId);
        payment.setRawGatewayResponse(response.toString());
        applyGatewayStatus(payment, transaction.path("status").asText("PENDING"));
        return toResponse(payment);
    }

    private void applyGatewayStatus(Payment payment, String wompiStatus) {
        PaymentStatus status = switch (wompiStatus) {
            case "APPROVED" -> PaymentStatus.APPROVED;
            case "DECLINED", "VOIDED" -> PaymentStatus.DECLINED;
            case "ERROR" -> PaymentStatus.ERROR;
            default -> PaymentStatus.PENDING;
        };
        payment.setStatus(status);
        Purchase purchase = payment.getPurchase();
        if (status == PaymentStatus.APPROVED) {
            purchase.setStatus(PurchaseStatus.APPROVED);
            issueTickets(purchase);
        } else if (status == PaymentStatus.DECLINED || status == PaymentStatus.ERROR) {
            releaseStock(purchase);
            purchase.setStatus(PurchaseStatus.DECLINED);
        }
        paymentRepository.save(payment);
    }

    private void issueTickets(Purchase purchase) {
        if (!purchase.getTickets().isEmpty()) {
            return;
        }
        for (int i = 0; i < purchase.getQuantity(); i++) {
            Ticket ticket = Ticket.builder()
                    .ticketNumber(TicketNumberGenerator.next())
                    .qrCode(UUID.randomUUID().toString())
                    .status(TicketStatus.ISSUED)
                    .purchase(purchase)
                    .event(purchase.getEvent())
                    .owner(purchase.getUser())
                    .build();
            ticket.setPdfPath(pdfService.generateTicketPdf(ticket));
            purchase.getTickets().add(ticket);
            ticketRepository.save(ticket);
        }
    }

    private void releaseStock(Purchase purchase) {
        if (purchase.getStatus() == PurchaseStatus.DECLINED || purchase.getStatus() == PurchaseStatus.CANCELLED) {
            return;
        }
        TicketType ticketType = purchase.getTicketType();
        Event event = purchase.getEvent();
        ticketType.setAvailableStock(ticketType.getAvailableStock() + purchase.getQuantity());
        event.setAvailableCapacity(event.getAvailableCapacity() + purchase.getQuantity());
        if (event.getStatus() == EventStatus.SOLD_OUT) {
            event.setStatus(EventStatus.PUBLISHED);
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        Purchase purchase = payment.getPurchase();
        return new PaymentResponse(
                purchase.getId(),
                payment.getId(),
                payment.getReference(),
                payment.getWompiTransactionId(),
                payment.getStatus(),
                purchase.getStatus(),
                payment.getAmount(),
                payment.getAmountInCents(),
                payment.getCheckoutUrl()
        );
    }
}