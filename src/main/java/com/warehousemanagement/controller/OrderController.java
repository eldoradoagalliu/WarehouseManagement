package com.warehousemanagement.controller;

import com.warehousemanagement.model.Item;
import com.warehousemanagement.model.Order;
import com.warehousemanagement.model.OrderItemQuantity;
import com.warehousemanagement.model.User;
import com.warehousemanagement.model.enums.OrderStatus;
import com.warehousemanagement.service.InventoryService;
import com.warehousemanagement.service.OrderService;
import com.warehousemanagement.service.TruckService;
import com.warehousemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static com.warehousemanagement.constant.Constants.*;

@Controller
@RequestMapping(API_PATH + "/order")
@RequiredArgsConstructor
public class OrderController {

    public static Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final UserService userService;
    private final OrderService orderService;
    private final InventoryService inventoryService;
    private final TruckService truckService;

    @GetMapping("/{orderNumber}")
    public String orderDetails(@PathVariable(ORDER_NUMBER) Long orderNumber, Model model) {
        logger.info("In Order Details");
        Order currentOrder = orderService.findOrder(orderNumber);
        model.addAttribute(ORDER, currentOrder);
        model.addAttribute(ORDER_ITEMS, inventoryService.getOrderItems(currentOrder));
        model.addAttribute(ITEMS, inventoryService.getAllItems());
        return "order_details";
    }

    @PostMapping
    public String createOrder(Principal principal) {
        logger.info("Create an order");
        User client = userService.findUser(principal.getName());
        Order newOrder = Order.builder()
                .status(OrderStatus.CREATED.getStatus())
                .client(client)
                .build();
        orderService.createOrder(newOrder);
        return "redirect:" + REDIRECT_USER_API_PATH;
    }

    @PostMapping("/add/item/{id}")
    public String addItemToOrder(@RequestParam(ORDER_NUMBER) Long orderNumber, @PathVariable(ID) Long itemId, Model model) {
        logger.info("Add item to order - No.{}", orderNumber);
        Order currentOrder = orderService.findOrder(orderNumber);
        Item item = inventoryService.getItem(itemId);
        if (item.getQuantity() < 1L) {
            logger.error("Item is not in enough quantity in Inventory!");
            model.addAttribute(ORDER, currentOrder);
            model.addAttribute(ORDER_ITEMS, inventoryService.getOrderItems(currentOrder));
            model.addAttribute(ITEMS, inventoryService.getAllItems());
            model.addAttribute(LOW_QUANTITY, LOW_QUANTITY_ITEM);
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
                OrderItemQuantity orderItem = OrderItemQuantity.builder()
                        .quantity(1L)
                        .order(currentOrder)
                        .item(item)
                        .build();
                inventoryService.addRequestedItemQuantity(orderItem);
            }

            return "redirect:" + ORDER_API_PATH + orderNumber;
        }
    }

    @DeleteMapping("/remove/item/{id}")
    public String removeItemFromOrder(@RequestParam(ORDER_NUMBER) Long orderNumber, @PathVariable(ID) Long itemId) {
        logger.info("Remove item from order - No.{}", orderNumber);
        Order currentOrder = orderService.findOrder(orderNumber);
        Item itemToRemove = inventoryService.getItem(itemId);
        inventoryService.removeItemFromOrder(currentOrder, itemToRemove);
        orderService.updateOrder(currentOrder);
        return "redirect:" + ORDER_API_PATH + orderNumber;
    }

    @PostMapping("/modify/item/quantity/{id}")
    public String modifyItemQuantity(@RequestParam(QUANTITY) Long quantity, @RequestParam(ORDER_NUMBER) Long orderNumber,
                                     @PathVariable(ID) Long itemId, Model model) {
        logger.info("Add {} items to order - No.{}", quantity, orderNumber);
        Order currentOrder = orderService.findOrder(orderNumber);
        Item item = inventoryService.getItem(itemId);
        if (item.getQuantity() < quantity) {
            logger.error("Item is not in enough quantity in Inventory!");
            model.addAttribute(ORDER, currentOrder);
            model.addAttribute(ORDER_ITEMS, inventoryService.getOrderItems(currentOrder));
            model.addAttribute(ITEMS, inventoryService.getAllItems());
            model.addAttribute(LOW_QUANTITY, LOW_QUANTITY_ITEM);
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
                OrderItemQuantity orderItem = OrderItemQuantity.builder()
                        .quantity(quantity)
                        .order(currentOrder)
                        .item(item)
                        .build();
                inventoryService.addRequestedItemQuantity(orderItem);
            }

            return "redirect:" + ORDER_API_PATH + orderNumber;
        }
    }

    @PostMapping("/submit/{orderNumber}")
    public String submitOrder(Principal principal, @PathVariable(ORDER_NUMBER) Long orderNumber, Model model) {
        logger.info("Submit order - No.{}", orderNumber);
        Order currentOrder = orderService.findOrder(orderNumber);
        if (!(currentOrder.getStatus().equals(OrderStatus.CREATED.getStatus()) ||
                currentOrder.getStatus().equals(OrderStatus.DECLINED.getStatus()))) {
            logger.error("Order is not in correct submission status!");
            model.addAttribute(CURRENT_USER, userService.findUser(principal.getName()));
            model.addAttribute("notCorrectSubmissionStatus", NOT_CORRECT_SUBMISSION_STATUS);
            return "dashboard";
        } else {
            currentOrder.setStatus(OrderStatus.AWAITING_APPROVAL.getStatus());
            currentOrder.setSubmittedDate(Date.from(Instant.now()));
            currentOrder.setDeclineReason(null);
            orderService.updateOrder(currentOrder);
            return "redirect:" + REDIRECT_USER_API_PATH;
        }
    }

    @DeleteMapping("/cancel/{orderNumber}")
    public String cancelOrder(Principal principal, @PathVariable(ORDER_NUMBER) Long orderNumber, Model model) {
        logger.info("Cancel order - No.{}", orderNumber);
        Order currentOrder = orderService.findOrder(orderNumber);
        if (currentOrder.getStatus().equals(OrderStatus.FULFILLED.getStatus()) ||
                currentOrder.getStatus().equals(OrderStatus.UNDER_DELIVERY.getStatus()) ||
                currentOrder.getStatus().equals(OrderStatus.CANCELLED.getStatus())) {
            logger.error("Order is not in correct status!");
            model.addAttribute(CURRENT_USER, userService.findUser(principal.getName()));
            model.addAttribute("notCorrectStatus", NOT_CORRECT_STATUS);
            return "dashboard";
        } else {
            inventoryService.returnItemsInInventory(currentOrder);
            orderService.deleteOrder(currentOrder);
            return "redirect:" + REDIRECT_USER_API_PATH;
        }
    }

    @PostMapping("/approve/{orderNumber}")
    public String approveOrder(@PathVariable(ORDER_NUMBER) Long orderNumber) {
        logger.info("Approve order - No.{}", orderNumber);
        Order currentOrder = orderService.findOrder(orderNumber);
        currentOrder.setStatus(OrderStatus.APPROVED.getStatus());
        orderService.updateOrder(currentOrder);
        return "redirect:" + REDIRECT_USER_API_PATH;
    }

    @PostMapping("/decline/{orderNumber}")
    public String declineOrder(@RequestParam(REASON) String reason, @PathVariable(ORDER_NUMBER) Long orderNumber) {
        logger.info("Decline order - No.{}", orderNumber);
        Order currentOrder = orderService.findOrder(orderNumber);
        currentOrder.setSubmittedDate(new Date(DEFAULT_VALUE));
        currentOrder.setStatus(OrderStatus.DECLINED.getStatus());
        currentOrder.setDeclineReason(reason);
        orderService.updateOrder(currentOrder);
        return "redirect:" + REDIRECT_USER_API_PATH;
    }

    @PostMapping("/fulfill/{orderNumber}")
    public String fulfillOrder(@PathVariable(ORDER_NUMBER) Long orderNumber) {
        logger.info("Fulfill order - No.{}", orderNumber);
        Order currentOrder = orderService.findOrder(orderNumber);
        currentOrder.setStatus(OrderStatus.FULFILLED.getStatus());
        currentOrder.getTruckDelivers().stream()
                .findFirst()
                .ifPresent(truck -> truck.setOrderToDeliver(null));
        orderService.updateOrder(currentOrder);
        return "redirect:" + REDIRECT_USER_API_PATH;
    }

    @GetMapping("/filter")
    public String filterOrders(Principal principal, @RequestParam(STATUS) String status, Model model) {
        logger.info("Filter orders by status: {}", status);
        List<Order> orders = orderService.filterOrders(status);
        model.addAttribute(CURRENT_USER, userService.findUser(principal.getName()));
        model.addAttribute(ORDERS, orders);
        model.addAttribute(TRUCKS, truckService.getAllTrucks());
        model.addAttribute(TODAY_DATE, LocalDate.now().plusDays(DEFAULT_VALUE));
        return "manager_dashboard";
    }

    @GetMapping("/client/filter")
    public String filterClientOrders(Principal principal, @RequestParam(STATUS) String status, Model model) {
        logger.info("Filter client orders by status: {}", status);
        User currentUser = userService.findUser(principal.getName());
        List<Order> filteredOrders = orderService.filterClientOrder(currentUser.getOrders(), status);
        model.addAttribute(CURRENT_USER, currentUser);
        model.addAttribute(ORDERS, filteredOrders);
        return "dashboard";
    }
}
