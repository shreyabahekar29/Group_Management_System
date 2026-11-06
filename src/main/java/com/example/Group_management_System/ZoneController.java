
package com.example.Group_management_System;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ZoneController {

    private final ZoneRepository zoneRepository;
    private final BrandRepository brandRepository;

    public ZoneController(ZoneRepository zoneRepository,
                          BrandRepository brandRepository) {
        this.zoneRepository = zoneRepository;
        this.brandRepository = brandRepository;
    }

    // Display Zone Dashboard
    @GetMapping("/zones")
    public String listZones(Model model) {
        model.addAttribute("zones",
                zoneRepository.findByIsActiveTrue());

        return "zones";
    }

    // Open Add Zone Form
    @GetMapping("/zones/new")
    public String showAddForm(Model model) {
        model.addAttribute("zone", new Zone());
        model.addAttribute("brands",
                brandRepository.findByIsActiveTrue());

        return "zone-form";
    }

    // Save New Zone
    @PostMapping("/zones")
    public String saveZone(@ModelAttribute("zone") Zone zone,
                           @RequestParam("brandId") Integer brandId,
                           RedirectAttributes redirectAttributes) {

        if (zone.getZoneName() == null ||
                zone.getZoneName().isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Zone name is required.");

            return "redirect:/zones/new";
        }

        Optional<Brand> optionalBrand =
                brandRepository.findById(brandId);

        if (optionalBrand.isEmpty() ||
                !Boolean.TRUE.equals(optionalBrand.get().getIsActive())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please select an active Brand.");

            return "redirect:/zones/new";
        }

        zone.setZoneId(null);
        zone.setBrand(optionalBrand.get());
        zone.setIsActive(true);

        zoneRepository.save(zone);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Zone created successfully!");

        return "redirect:/zones";
    }

    // Open Edit Zone Form
    @GetMapping("/zones/{id}/edit")
    public String editZone(@PathVariable Integer id,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        Optional<Zone> optionalZone = zoneRepository.findById(id);

        if (optionalZone.isEmpty() ||
                !Boolean.TRUE.equals(optionalZone.get().getIsActive())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Zone not found.");

            return "redirect:/zones";
        }

        model.addAttribute("zone", optionalZone.get());
        model.addAttribute("brands",
                brandRepository.findByIsActiveTrue());

        return "zone-form";
    }

    // Update Zone
    @PostMapping("/zones/{id}")
    public String updateZone(@PathVariable Integer id,
                             @ModelAttribute("zone") Zone updatedZone,
                             @RequestParam("brandId") Integer brandId,
                             RedirectAttributes redirectAttributes) {

        Optional<Zone> optionalZone = zoneRepository.findById(id);
        Optional<Brand> optionalBrand = brandRepository.findById(brandId);

        if (optionalZone.isEmpty() ||
                !Boolean.TRUE.equals(optionalZone.get().getIsActive())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Zone not found.");

            return "redirect:/zones";
        }

        if (updatedZone.getZoneName() == null ||
                updatedZone.getZoneName().isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Zone name is required.");

            return "redirect:/zones/" + id + "/edit";
        }

        if (optionalBrand.isEmpty() ||
                !Boolean.TRUE.equals(optionalBrand.get().getIsActive())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please select an active Brand.");

            return "redirect:/zones/" + id + "/edit";
        }

        Zone zone = optionalZone.get();

        zone.setZoneName(updatedZone.getZoneName());
        zone.setBrand(optionalBrand.get());

        zoneRepository.save(zone);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Zone updated successfully!");

        return "redirect:/zones";
    }

    // Soft Delete Zone
    @PostMapping("/zones/{id}/delete")
    public String deleteZone(@PathVariable Integer id,
                             RedirectAttributes redirectAttributes) {

        Optional<Zone> optionalZone = zoneRepository.findById(id);

        if (optionalZone.isPresent() &&
                Boolean.TRUE.equals(optionalZone.get().getIsActive())) {

            Zone zone = optionalZone.get();
            zone.setIsActive(false);

            zoneRepository.save(zone);

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Zone deleted successfully!");

        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Zone not found.");
        }

        return "redirect:/zones";
    }
}

