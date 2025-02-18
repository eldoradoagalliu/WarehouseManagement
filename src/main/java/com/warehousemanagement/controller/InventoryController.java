package com.warehousemanagement.controller;

import com.warehousemanagement.model.Item;
import com.warehousemanagement.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.warehousemanagement.constant.Constants.API_PATH;
import static com.warehousemanagement.constant.Constants.ID;
import static com.warehousemanagement.constant.Constants.INVENTORY_API_PATH;
import static com.warehousemanagement.constant.Constants.ITEM;
import static com.warehousemanagement.constant.Constants.ITEMS;

@Controller
@RequestMapping(API_PATH + "/item")
@RequiredArgsConstructor
public class InventoryController {

    public static Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    @GetMapping("/inventory")
    public String inventory(@ModelAttribute(ITEM) Item item, Model model) {
        logger.info("In Warehouse Inventory");
        model.addAttribute(ITEMS, inventoryService.getAllItems());
        return "inventory";
    }

    @GetMapping
    public String addItem(@ModelAttribute(ITEM) Item item) {
        logger.info("In Item input form");
        return "item";
    }

    @PostMapping
    public String addItem(@Valid @ModelAttribute(ITEM) Item item, BindingResult result) {
        logger.info("Add Item in Inventory");
        if (result.hasErrors()) {
            return "item";
        } else {
            inventoryService.addItem(item);
            return "redirect:" + INVENTORY_API_PATH;
        }
    }

    @GetMapping("/{id}")
    public String editItem(@PathVariable(ID) Long itemId, Model model) {
        logger.info("In Edit Item Details");
        model.addAttribute(ITEM, inventoryService.getItem(itemId));
        return "edit_item_details";
    }

    @PutMapping("/{id}")
    public String editItem(@PathVariable(ID) Long itemId, @Valid @ModelAttribute(ITEM) Item editedItem, BindingResult result) {
        logger.info("Edit Item Details");
        if (result.hasErrors()) {
            return "edit_item_details";
        } else {
            Item currentItem = inventoryService.getItem(itemId);
            editedItem.setOrderItemsQuantities(currentItem.getOrderItemsQuantities());
            inventoryService.updateItem(editedItem);
            return "redirect:" + INVENTORY_API_PATH;
        }
    }

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable(ID) Long itemId) {
        logger.info("Delete Item from Inventory");
        Item item = inventoryService.getItem(itemId);
        inventoryService.deleteItem(item);
        return "redirect:" + INVENTORY_API_PATH;
    }
}
