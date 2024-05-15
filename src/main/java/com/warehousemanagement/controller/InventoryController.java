package com.warehousemanagement.controller;

import com.warehousemanagement.model.Item;
import com.warehousemanagement.service.InventoryService;
import com.warehousemanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping(path = "/item")
public class InventoryController {

    public static Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/inventory")
    public String inventory(Principal principal, @ModelAttribute("item") Item item, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("In Warehouse Inventory");
        model.addAttribute("items", inventoryService.getAllItems());
        return "inventory";
    }

    @GetMapping
    public String addItem(Principal principal, @ModelAttribute("item") Item item) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("In Item input form");
        return "item";
    }

    @PostMapping
    public String addItem(Principal principal, @Valid @ModelAttribute("item") Item item, BindingResult result) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Add Item in Inventory");

        if (result.hasErrors()) {
            return "item";
        } else {
            inventoryService.addItem(item);
            return "redirect:/item/inventory";
        }
    }

    @GetMapping("/{id}")
    public String editItem(Principal principal, @PathVariable("id") Long itemId, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        model.addAttribute("item", inventoryService.getItem(itemId));
        logger.info("In Edit Item Details");
        return "edit_item_details";
    }

    @PutMapping("/{id}")
    public String editItem(Principal principal, @PathVariable("id") Long itemId,
                           @Valid @ModelAttribute("item") Item editedItem, BindingResult result) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Edit Item Details");

        if (result.hasErrors()) {
            return "edit_item_details";
        } else {
            Item currentItem = inventoryService.getItem(itemId);
            editedItem.setOrderItemsQuantities(currentItem.getOrderItemsQuantities());
            inventoryService.updateItem(editedItem);
            return "redirect:/item/inventory";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteItem(Principal principal, @PathVariable("id") Long itemId) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Delete Item from Inventory");
        Item item = inventoryService.getItem(itemId);
        inventoryService.deleteItem(item);
        return "redirect:/item/inventory";
    }
}
