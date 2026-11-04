
package com.example.Group_management_System;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ChainController {

    private final ChainRepository chainRepository;
    private final GroupRepository groupRepository;

    public ChainController(ChainRepository chainRepository,
                           GroupRepository groupRepository) {
        this.chainRepository = chainRepository;
        this.groupRepository = groupRepository;
    }

    // Display all active companies or filter by group
    @GetMapping("/chains")
    public String listChains(
            @RequestParam(required = false) Integer groupId,
            Model model) {

        List<Chain> chains;

        if (groupId != null) {
            chains = chainRepository
                    .findByGroup_GroupIdAndIsActiveTrue(groupId);
        } else {
            chains = chainRepository.findByIsActiveTrue();
        }

        model.addAttribute("chains", chains);
        model.addAttribute("groups", groupRepository.findAll());
        model.addAttribute("selectedGroupId", groupId);

        return "chains";
    }

    // Show add company form
    @GetMapping("/chains/new")
    public String showAddForm(Model model) {

        model.addAttribute("chain", new Chain());
        model.addAttribute("groups", groupRepository.findAll());

        return "chain-form";
    }

    // Save new company
    @PostMapping("/chains")
    public String addChain(
            @ModelAttribute("chain") Chain chain,
            @RequestParam Integer groupId,
            RedirectAttributes redirectAttributes) {

        String gstn = chain.getGstnNo();

        if (chain.getCompanyName() == null ||
                chain.getCompanyName().isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Company name is required.");

            return "redirect:/chains/new";
        }

        if (!isValidGstn(gstn)) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Please enter a valid 15-character GST number.");

            return "redirect:/chains/new";
        }

        if (chainRepository.existsByGstnNo(gstn)) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "This GST number already exists.");

            return "redirect:/chains/new";
        }

        Group group = groupRepository.findById(groupId).orElse(null);

        if (group == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please select a valid group.");

            return "redirect:/chains/new";
        }

        chain.setGroup(group);
        chain.setIsActive(true);

        chainRepository.save(chain);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Company added successfully!");

        return "redirect:/chains";
    }

    // Show edit form
    @GetMapping("/chains/{id}/edit")
    public String showEditForm(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Chain chain = chainRepository.findById(id).orElse(null);

        if (chain == null ||
                !Boolean.TRUE.equals(chain.getIsActive())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Company not found.");

            return "redirect:/chains";
        }

        model.addAttribute("chain", chain);
        model.addAttribute("groups", groupRepository.findAll());

        return "chain-form";
    }

    // Update company details
    @PostMapping("/chains/{id}")
    public String updateChain(
            @PathVariable Integer id,
            @ModelAttribute("chain") Chain updatedChain,
            @RequestParam Integer groupId,
            RedirectAttributes redirectAttributes) {

        Chain existingChain = chainRepository.findById(id).orElse(null);

        if (existingChain == null ||
                !Boolean.TRUE.equals(existingChain.getIsActive())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Company not found.");

            return "redirect:/chains";
        }

        String gstn = updatedChain.getGstnNo();

        if (updatedChain.getCompanyName() == null ||
                updatedChain.getCompanyName().isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Company name is required.");

            return "redirect:/chains/" + id + "/edit";
        }

        if (!isValidGstn(gstn)) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Please enter a valid 15-character GST number.");

            return "redirect:/chains/" + id + "/edit";
        }

        if (chainRepository.existsByGstnNoAndChainIdNot(gstn, id)) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This GST number is already used by another company.");

            return "redirect:/chains/" + id + "/edit";
        }

        Group group = groupRepository.findById(groupId).orElse(null);

        if (group == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please select a valid group.");

            return "redirect:/chains/" + id + "/edit";
        }

        existingChain.setCompanyName(updatedChain.getCompanyName());
        existingChain.setGstnNo(gstn);
        existingChain.setGroup(group);

        chainRepository.save(existingChain);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Company updated successfully!");

        return "redirect:/chains";
    }

    // Soft delete company
    @PostMapping("/chains/{id}/delete")
    public String deleteChain(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        Chain chain = chainRepository.findById(id).orElse(null);

        if (chain == null ||
                !Boolean.TRUE.equals(chain.getIsActive())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Company not found.");

            return "redirect:/chains";
        }

        chain.setIsActive(false);
        chainRepository.save(chain);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Company deactivated successfully!");

        return "redirect:/chains";
    }

    // GST number validation
    private boolean isValidGstn(String gstn) {

        return gstn != null &&
                gstn.matches(
                        "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$"
                );
    }
}
