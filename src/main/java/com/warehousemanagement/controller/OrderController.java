package com.warehousemanagement.controller;

import com.warehousemanagement.model.Item;
import com.warehousemanagement.model.Order;
import com.warehousemanagement.model.OrderItemQuantity;
import com.warehousemanagement.model.User;
import com.warehousemanagement.service.InventoryService;
import com.warehousemanagement.service.OrderService;
import com.warehousemanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.warehousemanagement.model.constant.Constants.DEFAULT_VALUE;
import static com.warehousemanagement.model.constant.Constants.LOW_QUANTITY_ITEM;
import static com.warehousemanagement.model.constant.Constants.NOT_CORRECT_STATUS;
import static com.warehousemanagement.model.constant.Constants.NOT_CORRECT_SUBMISSION_STATUS;
import static com.warehousemanagement.model.enums.OrderStatusEnum.*;

@Controller
@RequestMapping(path = "/order")
public class OrderController {

    public static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/{orderNumber}")
    public String orderDetails(Principal principal, @PathVariable("orderNumber") Long orderNumber, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("In Order Details");
        Order currentOrder = orderService.findOrder(orderNumber);
        model.addAttribute("order", currentOrder);
        model.addAttribute("orderItems", inventoryService.getOrderItems(currentOrder));
        model.addAttribute("items", inventoryService.getAllItems());
        return "order_details";
    }

    @PostMapping
    public String createOrder(Principal principal) {
        logger.info("Create an Order");
        if (userService.principalIsNull(principal)) return "redirect:/login";
        User client = userService.findUser(principal.getName());
        Order newOrder = new Order(CREATED.getStatus(), client);
        orderService.createOrder(newOrder);
        return "redirect:/dashboard";
    }

    @PostMapping("/add/item/{id}")
    public String addItemToOrder(Principal principal, @RequestParam("orderNumber") Long orderNumber, @PathVariable("id") Long itemId,
                                 Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Add Item To Order");
        Order currentOrder = orderService.findOrder(orderNumber);
        Item item = inventoryService.getItem(itemId);
        if (item.getQuantity() < 1L) {
            logger.error("Item is not in enough quantity in Inventory!");
            model.addAttribute("order", currentOrder);
            model.addAttribute("orderItems", inventoryService.getOrderItems(currentOrder));
            model.addAttribute("items", inventoryService.getAllItems());
            model.addAttribute("lowQuantity", LOW_QUANTITY_ITEM);
            return "order_details";
        } else {
            item.setQuantity(item.getQuantity() - 1L);
            inventoryService.updateItem(item);

            Optional<OrderItemQuantity> existingOrderItem = inventoryService.getOrderItem(currentOrder, item);
            if (existingOrderItem.isPresent()) {
                OrderItemQuantity orderItem = existingOrderItem.get();
                orderItem.setQuantity(orderItem.getQuantity() + 1L);
                inventoryService.updateItemQuantity(orderItem);
            } else {
                OrderItemQuantity orderItem = new OrderItemQuantity(1L, currentOrder, item);
                inventoryService.addRequestedItemQuantity(orderItem);
            }

            return "redirect:/order/" + orderNumber;
        }
    }

    @DeleteMapping("/remove/item/{id}")
    public String removeItemFromOrder(Principal principal, @RequestParam("orderNumber") Long orderNumber, @PathVariable("id") Long itemId) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Remove Item From Order");
        Order currentOrder = orderService.findOrder(orderNumber);
        Item itemToRemove = inventoryService.getItem(itemId);
        inventoryService.removeItemFromOrder(currentOrder, itemToRemove);
        orderService.updateOrder(currentOrder);
        return "redirect:/order/" + orderNumber;
    }

    @PostMapping("/modify/item/quantity/{id}")
    public String modifyItemQuantity(Principal principal, @RequestParam("quantity") Long quantity,
                                     @RequestParam("orderNumber") Long orderNumber, @PathVariable("id") Long itemId, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Add {} Items To Order", quantity);

        Order currentOrder = orderService.findOrder(orderNumber);
        Item item = inventoryService.getItem(itemId);
        if (item.getQuantity() < quantity) {
            logger.error("Item is not in enough quantity in Inventory!");
            model.addAttribute("order", currentOrder);
            model.addAttribute("orderItems", inventoryService.getOrderItems(currentOrder));
            model.addAttribute("items", inventoryService.getAllItems());
            model.addAttribute("lowQuantity", LOW_QUANTITY_ITEM);
            return "order_details";
        } else {
            item.setQuantity(item.getQuantity() - quantity);
            inventoryService.updateItem(item);

            Optional<OrderItemQuantity> existingOrderItem = inventoryService.getOrderItem(currentOrder, item);
            if (existingOrderItem.isPresent()) {
                OrderItemQuantity orderItem = existingOrderItem.get();
                orderItem.setQuantity(orderItem.getQuantity() + quantity);
                inventoryService.updateItemQuantity(orderItem);
            } else {
                OrderItemQuantity orderItem = new OrderItemQuantity(quantity, currentOrder, item);
                inventoryService.addRequestedItemQuantity(orderItem);
            }

            return "redirect:/order/" + orderNumber;
        }
    }

    @PostMapping("/submit/{orderNumber}")
    public String submitOrder(Principal principal, @PathVariable("orderNumber") Long orderNumber, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Submit Order");
        Order currentOrder = orderService.findOrder(orderNumber);

        if (!(currentOrder.getStatus().equals(CREATED.getStatus()) || currentOrder.getStatus().equals(DECLINED.getStatus()))) {
            logger.error("Order is not in correct submission status!");
            model.addAttribute("user", userService.findUser(principal.getName()));
            model.addAttribute("notCorrectSubmissionStatus", NOT_CORRECT_SUBMISSION_STATUS);
            return "dashboard";
        } else {
            currentOrder.setStatus(AWAITING_APPROVAL.getStatus());
            currentOrder.setSubmittedDate(Date.from(Instant.now()));
            currentOrder.setDeclineReason(null);
            orderService.updateOrder(currentOrder);
            return "redirect:/";
        }
    }

    @DeleteMapping("/cancel/{orderNumber}")
    public String cancelOrder(Principal principal, @PathVariable("orderNumber") Long orderNumber, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Cancel Order");
        Order currentOrder = orderService.findOrder(orderNumber);

        if (currentOrder.getStatus().equals(FULFILLED.getStatus()) || currentOrder.getStatus().equals(UNDER_DELIVERY.getStatus()) ||
                currentOrder.getStatus().equals(CANCELLED.getStatus())) {
            logger.error("Order is not in correct status!");
            model.addAttribute("user", userService.findUser(principal.getName()));
            model.addAttribute("notCorrectStatus", NOT_CORRECT_STATUS);
            return "dashboard";
        } else {
            inventoryService.returnItemsInInventory(currentOrder);
            orderService.deleteOrder(currentOrder);
            return "redirect:/";
        }
    }

    @GetMapping("/filter")
    public String filterOrders(Principal principal, @RequestParam("status") String status, Model model) {
        if (userService.principalIsNull(principal) || !userService.isManager(principal)) return "redirect:/logout";
        logger.info("Filter orders by: {}", status);
        List<Order> orders = orderService.filterOrders(status);
        model.addAttribute("user", userService.findUser(principal.getName()));
        model.addAttribute("orders", orders);
        model.addAttribute("now", LocalDate.now().plusDays(DEFAULT_VALUE));
        return "manager_dashboard";
    }

    @GetMapping("/client/filter")
    public String filterClientOrders(Principal principal, @RequestParam("status") String status, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Filter client orders by: {}", status);
        User currentUser = userService.findUser(principal.getName());
        List<Order> filteredOrders = orderService.filterClientOrder(currentUser.getOrders(), status);
        model.addAttribute("user", currentUser);
        model.addAttribute("orders", filteredOrders);
        return "dashboard";
    }

    @PostMapping("/approve/{orderNumber}")
    public String approveOrder(Principal principal, @PathVariable("orderNumber") Long orderNumber) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Approve Order");
        Order currentOrder = orderService.findOrder(orderNumber);
        currentOrder.setStatus(APPROVED.getStatus());
        orderService.updateOrder(currentOrder);
        return "redirect:/";
    }

    @PostMapping("/decline/{orderNumber}")
    public String declineOrder(Principal principal, @RequestParam("reason") String reason, @PathVariable("orderNumber") Long orderNumber) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Decline Order");
        Order currentOrder = orderService.findOrder(orderNumber);
        currentOrder.setSubmittedDate(new Date(DEFAULT_VALUE));
        currentOrder.setStatus(DECLINED.getStatus());
        currentOrder.setDeclineReason(reason);
        orderService.updateOrder(currentOrder);
        return "redirect:/";
    }

    @PostMapping("/fulfill/{orderNumber}")
    public String fulfillOrder(Principal principal, @PathVariable("orderNumber") Long orderNumber) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Fulfill Order");
        Order currentOrder = orderService.findOrder(orderNumber);
        currentOrder.setStatus(FULFILLED.getStatus());
        orderService.updateOrder(currentOrder);
        return "redirect:/";
    }
}
