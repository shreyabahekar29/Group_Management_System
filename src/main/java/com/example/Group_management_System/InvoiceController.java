package com.example.Group_management_System;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;
    private final EstimateRepository estimateRepository;
    private final ChainRepository chainRepository;

    public InvoiceController(
            InvoiceRepository invoiceRepository,
            EstimateRepository estimateRepository,
            ChainRepository chainRepository) {

        this.invoiceRepository = invoiceRepository;
        this.estimateRepository = estimateRepository;
        this.chainRepository = chainRepository;
    }

    @GetMapping("/invoices")
    public String showInvoices(Model model) {

        model.addAttribute("invoices",
                invoiceRepository.findAll());

        return "invoices";
    }

    @GetMapping("/invoices/new")
    public String newInvoice(Model model) {

        model.addAttribute("invoice", new Invoice());

        model.addAttribute("estimates",
                estimateRepository.findAll());

        return "invoice-form";
    }

    @PostMapping("/invoices/save")
    public String saveInvoice(
            @RequestParam Integer estimateId,
            @RequestParam BigDecimal amountPaid,
            @RequestParam String emailId,
            RedirectAttributes redirectAttributes) {

        Estimate estimate = estimateRepository
                .findById(estimateId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid Estimate ID"));

        Invoice invoice = new Invoice();

        invoice.setEstimate(estimate);
        invoice.setChain(estimate.getChain());

        invoice.setServiceDetails(estimate.getService());
        invoice.setQty(estimate.getQty());
        invoice.setCostPerQty(estimate.getCostPerUnit());

        BigDecimal payable = estimate.getTotalCost();

        if (payable == null || amountPaid == null
                || amountPaid.compareTo(BigDecimal.ZERO) < 0
                || amountPaid.compareTo(payable) > 0) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Invalid payment amount.");

            return "redirect:/invoices/new";
        }

        invoice.setAmountPayable(payable);
        invoice.setAmountPaid(amountPaid);
        invoice.setBalance(payable.subtract(amountPaid));

        invoice.setEmailId(emailId);
        invoice.setDateOfService(estimate.getDeliveryDate());
        invoice.setDeliveryDetails(estimate.getDeliveryDetails());

        int nextNumber = invoiceRepository.findAll()
                .stream()
                .map(Invoice::getInvoiceNo)
                .filter(number -> number != null)
                .max(Integer::compareTo)
                .orElse(1000) + 1;

        invoice.setInvoiceNo(nextNumber);

        invoiceRepository.save(invoice);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Invoice generated successfully!");

        return "redirect:/invoices";
    }
}
