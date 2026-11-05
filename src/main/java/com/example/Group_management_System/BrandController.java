package com.example.Group_management_System;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class BrandController {

    private final BrandRepository brandRepository;
    private final ChainRepository chainRepository;
    private final GroupRepository groupRepository;

    public BrandController(
            BrandRepository brandRepository,
            ChainRepository chainRepository,
            GroupRepository groupRepository) {

        this.brandRepository = brandRepository;
        this.chainRepository = chainRepository;
        this.groupRepository = groupRepository;
    }

    // Display brands and filter by company or group
    @GetMapping("/brands")
    public String listBrands(
            @RequestParam(required = false) Integer chainId,
            @RequestParam(required = false) Integer groupId,
            Model model) {

        List<Brand> brands;

        if (chainId != null) {
            brands = brandRepository
                    .findByChain_ChainIdAndIsActiveTrue(chainId);

        } else if (groupId != null) {
            brands = brandRepository
                    .findByChain_Group_GroupIdAndIsActiveTrue(groupId);

        } else {
            brands = brandRepository.findByIsActiveTrue();
        }

        model.addAttribute("brands", brands);
        model.addAttribute("chains", chainRepository.findByIsActiveTrue());
        model.addAttribute("groups", groupRepository.findAll());
        model.addAttribute("selectedChainId", chainId);
        model.addAttribute("selectedGroupId", groupId);

        return "brands";
    }

    // Show add brand form
    @GetMapping("/brands/new")
    public String showAddForm(Model model) {

        model.addAttribute("brand", new Brand());
        model.addAttribute("chains", chainRepository.findByIsActiveTrue());

        return "brand-form";
    }

    // Save new brand
    @PostMapping("/brands")
    public String addBrand(
            @ModelAttribute("brand") Brand brand,
            @RequestParam Integer chainId,
            RedirectAttributes redirectAttributes) {

        if (brand.getBrandName() == null ||
                brand.getBrandName().isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Brand name is required.");

            return "redirect:/brands/new";
        }

        String brandName = brand.getBrandName().trim();

        if (brandName.length() > 50) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Brand name cannot exceed 50 characters.");

            return "redirect:/brands/new";
        }

        Chain chain = chainRepository.findById(chainId).orElse(null);

        if (chain == null ||
                !Boolean.TRUE.equals(chain.getIsActive())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please select a valid active company.");

            return "redirect:/brands/new";
        }

        brand.setBrandName(brandName);
        brand.setChain(chain);
        brand.setIsActive(true);

        brandRepository.save(brand);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Brand added successfully!");

        return "redirect:/brands";
    }

    // Show edit form
    @GetMapping("/brands/{id}/edit")
    public String showEditForm(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Brand brand = brandRepository.findById(id).orElse(null);

        if (brand == null ||
                !Boolean.TRUE.equals(brand.getIsActive())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Brand not found.");

            return "redirect:/brands";
        }

        model.addAttribute("brand", brand);
        model.addAttribute("chains", chainRepository.findByIsActiveTrue());

        return "brand-form";
    }

    // Update brand details
    @PostMapping("/brands/{id}")
    public String updateBrand(
            @PathVariable Integer id,
            @ModelAttribute("brand") Brand updatedBrand,
            @RequestParam Integer chainId,
            RedirectAttributes redirectAttributes) {

        Brand existingBrand = brandRepository.findById(id).orElse(null);

        if (existingBrand == null ||
                !Boolean.TRUE.equals(existingBrand.getIsActive())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Brand not found.");

            return "redirect:/brands";
        }

        String brandName = updatedBrand.getBrandName();

        if (brandName == null || brandName.isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Brand name is required.");

            return "redirect:/brands/" + id + "/edit";
        }

        brandName = brandName.trim();

        if (brandName.length() > 50) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Brand name cannot exceed 50 characters.");

            return "redirect:/brands/" + id + "/edit";
        }

        Chain chain = chainRepository.findById(chainId).orElse(null);

        if (chain == null ||
                !Boolean.TRUE.equals(chain.getIsActive())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please select a valid active company.");

            return "redirect:/brands/" + id + "/edit";
        }

        existingBrand.setBrandName(brandName);
        existingBrand.setChain(chain);

        brandRepository.save(existingBrand);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Brand updated successfully!");

        return "redirect:/brands";
    }

    // Soft delete brand
    @PostMapping("/brands/{id}/delete")
    public String deleteBrand(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        Brand brand = brandRepository.findById(id).orElse(null);

        if (brand == null ||
                !Boolean.TRUE.equals(brand.getIsActive())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Brand not found.");

            return "redirect:/brands";
        }

        brand.setIsActive(false);
        brandRepository.save(brand);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Brand deactivated successfully!");

        return "redirect:/brands";
    }
}
