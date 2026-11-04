
package com.example.Group_management_System;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final GroupRepository groupRepository;

    public HomeController(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("groups", groupRepository.findAll());
        model.addAttribute("newGroup", new Group());
        return "index";
    }

    @PostMapping("/groups")
    public String createGroup(
            @ModelAttribute("newGroup") Group group,
            RedirectAttributes redirectAttributes) {

        if (group.getGroupName() == null ||
                group.getGroupName().isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Group name is required.");
            return "redirect:/";
        }

        group.setGroupId(null);
        groupRepository.save(group);

        redirectAttributes.addFlashAttribute(
                "success", "Group created successfully!");

        return "redirect:/";
    }

    @PostMapping("/groups/update/{id}")
    public String updateGroup(
            @PathVariable Integer id,
            @RequestParam String groupName,
            RedirectAttributes redirectAttributes) {

        if (groupName == null || groupName.isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Group name cannot be empty.");
            return "redirect:/";
        }

        Group group = groupRepository.findById(id).orElse(null);

        if (group == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Group not found.");
            return "redirect:/";
        }

        group.setGroupName(groupName.trim());
        groupRepository.save(group);

        redirectAttributes.addFlashAttribute(
                "success", "Group updated successfully!");

        return "redirect:/";
    }

    @PostMapping("/groups/delete/{id}")
    public String deleteGroup(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        if (groupRepository.existsById(id)) {
            groupRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "success", "Group deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "Group not found.");
        }

        return "redirect:/";
    }


    @PostMapping("/groups/toggle/{id}")
    public String toggleGroupStatus(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        Group group = groupRepository.findById(id).orElse(null);

        if (group != null) {
            group.setIsActive(!Boolean.TRUE.equals(group.getIsActive()));
            groupRepository.save(group);

            redirectAttributes.addFlashAttribute(
                    "success", "Group status updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "Group not found.");
        }

        return "redirect:/";
    }

}
