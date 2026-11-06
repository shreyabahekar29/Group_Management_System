package com.example.Group_management_System;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Objects;

@Controller
public class EstimateController {

    private final EstimateRepository estimateRepository;
    private final GroupRepository groupRepository;
    private final ChainRepository chainRepository;
    private final BrandRepository brandRepository;
    private final ZoneRepository zoneRepository;

    public EstimateController(
            EstimateRepository estimateRepository,
            GroupRepository groupRepository,
            ChainRepository chainRepository,
            BrandRepository brandRepository,
            ZoneRepository zoneRepository) {

        this.estimateRepository = estimateRepository;
        this.groupRepository = groupRepository;
        this.chainRepository = chainRepository;
        this.brandRepository = brandRepository;
        this.zoneRepository = zoneRepository;
    }

    // Display all estimates
    @GetMapping("/estimates")
    public String listEstimates(Model model) {
        model.addAttribute("estimates", estimateRepository.findAll());
        return "estimates";
    }

    // Open new estimate form
    @GetMapping("/estimates/new")
    public String newEstimate(Model model) {
        model.addAttribute("estimate", new Estimate());
        loadDropdowns(model);
        return "estimate-form";
    }

    // Save new estimate
    @PostMapping("/estimates/save")
    public String saveEstimate(
            @ModelAttribute("estimate") Estimate estimate,
            @RequestParam Integer groupId,
            @RequestParam Integer chainId,
            @RequestParam Integer brandId,
            @RequestParam Integer zoneId,
            RedirectAttributes redirectAttributes) {

        try {
            setEstimateRelationships(
                    estimate, groupId, chainId, brandId, zoneId);

            calculateTotalCost(estimate);

            estimateRepository.save(estimate);

            redirectAttributes.addFlashAttribute(
                    "success", "Estimate saved successfully!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/estimates/new";
        }

        return "redirect:/estimates";
    }

    // Open edit form
    @GetMapping("/estimates/edit/{id}")
    public String editEstimate(
            @PathVariable Integer id, Model model) {

        Estimate estimate = estimateRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid estimate ID"));

        model.addAttribute("estimate", estimate);

        model.addAttribute("selectedGroupId",
                estimate.getChain().getGroup().getGroupId());

        model.addAttribute("selectedChainId",
                estimate.getChain().getChainId());

        loadDropdowns(model);

        return "estimate-form";
    }

    // Update estimate
    @PostMapping("/estimates/update/{id}")
    public String updateEstimate(
            @PathVariable Integer id,
            @ModelAttribute("estimate") Estimate estimate,
            @RequestParam Integer groupId,
            @RequestParam Integer chainId,
            @RequestParam Integer brandId,
            @RequestParam Integer zoneId,
            RedirectAttributes redirectAttributes) {

        try {
            Estimate existing = estimateRepository.findById(id)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Estimate not found"));

            estimate.setEstimateId(existing.getEstimateId());

            setEstimateRelationships(
                    estimate, groupId, chainId, brandId, zoneId);

            calculateTotalCost(estimate);

            estimateRepository.save(estimate);

            redirectAttributes.addFlashAttribute(
                    "success", "Estimate updated successfully!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/estimates/edit/" + id;
        }

        return "redirect:/estimates";
    }

    // Delete estimate
    @PostMapping("/estimates/delete/{id}")
    public String deleteEstimate(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        estimateRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success", "Estimate deleted successfully!");

        return "redirect:/estimates";
    }

    // Load dropdown data
    private void loadDropdowns(Model model) {

        model.addAttribute("groups", groupRepository.findAll());
        model.addAttribute("chains", chainRepository.findByIsActiveTrue());
        model.addAttribute("brands", brandRepository.findByIsActiveTrue());
        model.addAttribute("zones", zoneRepository.findByIsActiveTrue());
    }

    // Connect Group, Chain, Brand and Zone
    private void setEstimateRelationships(
            Estimate estimate,
            Integer groupId,
            Integer chainId,
            Integer brandId,
            Integer zoneId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Group not found"));

        Chain chain = chainRepository.findById(chainId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Chain not found"));

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Brand not found"));

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Zone not found"));

        if (!Objects.equals(chain.getGroup().getGroupId(), group.getGroupId())) {
            throw new IllegalArgumentException(
                    "Selected chain does not belong to the selected group");
        }

        if (!Objects.equals(brand.getChain().getChainId(), chain.getChainId())) {
            throw new IllegalArgumentException(
                    "Selected brand does not belong to the selected chain");
        }

        if (!Objects.equals(zone.getBrand().getBrandId(), brand.getBrandId())) {
            throw new IllegalArgumentException(
                    "Selected zone does not belong to the selected brand");
        }

        estimate.setChain(chain);
        estimate.setGroupName(group.getGroupName());
        estimate.setBrandName(brand.getBrandName());
        estimate.setZoneName(zone.getZoneName());
    }

    // Calculate total cost
    private void calculateTotalCost(Estimate estimate) {

        if (estimate.getQty() == null ||
                estimate.getCostPerUnit() == null ||
                estimate.getQty() <= 0 ||
                estimate.getCostPerUnit().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Enter a valid quantity and cost per unit");
        }

        BigDecimal total = estimate.getCostPerUnit()
                .multiply(BigDecimal.valueOf(estimate.getQty()));

        estimate.setTotalCost(total);
    }
}
