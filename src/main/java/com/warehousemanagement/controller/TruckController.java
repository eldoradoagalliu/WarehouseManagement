package com.warehousemanagement.controller;

import com.warehousemanagement.model.Order;
import com.warehousemanagement.model.Truck;
import com.warehousemanagement.service.OrderService;
import com.warehousemanagement.service.TruckService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.warehousemanagement.model.constant.Constants.*;
import static com.warehousemanagement.model.enums.OrderStatusEnum.UNDER_DELIVERY;
import static java.time.DayOfWeek.SUNDAY;

@Controller
@RequestMapping(path = "/truck")
public class TruckController {

    public static Logger logger = LoggerFactory.getLogger(TruckController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TruckService truckService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String addItem(Principal principal, @ModelAttribute("truck") Truck truck, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("In Truck input form");
        model.addAttribute("trucks", truckService.getAllTrucks());
        return "truck";
    }

    @PostMapping
    public String addTruck(Principal principal, @Valid @ModelAttribute("truck") Truck truck, BindingResult result, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Add new Truck");

        if (result.hasErrors() || Objects.nonNull(truckService.findTruck(truck.getChassisNumber()))) {
            if (Objects.nonNull(truckService.findTruck(truck.getChassisNumber()))) {
                logger.error("Truck already exists");
                model.addAttribute("chassisNumberExists", CHASSIS_NUMBER_EXISTS);
            }
            model.addAttribute("trucks", truckService.getAllTrucks());
            return "truck";
        } else {
            truckService.addTruck(truck);
            return "redirect:/truck";
        }
    }

    @GetMapping("/{id}")
    public String editTruck(Principal principal, @PathVariable("id") Long truckId, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("In Edit Truck Details");
        model.addAttribute("truck", truckService.findTruck(truckId));
        return "edit_truck_details";
    }

    @PutMapping("/{id}")
    public String editTruck(Principal principal, @PathVariable("id") Long truckId,
                            @Valid @ModelAttribute("truck") Truck editedTruck, BindingResult result, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Edit Truck Details");
        Truck currentTruck = truckService.findTruck(truckId);
        boolean chassisNumberExists = Objects.nonNull(truckService.findTruck(editedTruck.getChassisNumber()))
                && !editedTruck.getChassisNumber().equals(currentTruck.getChassisNumber());

        if (result.hasErrors() || chassisNumberExists) {
            if (chassisNumberExists) {
                logger.warn("Truck already exists");
                model.addAttribute("chassisNumberExists", CHASSIS_NUMBER_EXISTS);
            }
            return "edit_truck_details";
        } else {
            editedTruck.setOrderToDeliver(currentTruck.getOrderToDeliver());
            truckService.updateTruck(editedTruck);
            return "redirect:/truck";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteTruck(Principal principal, @PathVariable("id") Long truckId) {
        if (userService.principalIsNull(principal)) return "redirect:/login";
        logger.info("Delete Truck");
        Truck truck = truckService.findTruck(truckId);
        truckService.deleteTruck(truck);
        return "redirect:/";
    }

    @PostMapping("/schedule/delivery/{orderNumber}")
    public String scheduleOrderDelivery(Principal principal, @RequestParam("date") String date,
                                        @RequestParam("licensePlate") String licensePlate,
                                        @PathVariable("orderNumber") Long orderNumber, Model model) {
        if (userService.principalIsNull(principal) || !userService.isManager(principal)) return "redirect:/logout";
        logger.info("Schedule order truck delivery");

        Truck truck = truckService.findTruckByPlate(licensePlate);
        Order orderToDeliver = orderService.findOrder(orderNumber);
        // One day to deliver the order
        if (truck.getOrderToDeliver() != null) {
            logger.warn("The requested truck is busy!");
            model.addAttribute("user", userService.findUser(principal.getName()));
            model.addAttribute("orders", orderService.getSortedOrders());
            model.addAttribute("now", LocalDate.now().plusDays(DEFAULT_VALUE));
            model.addAttribute("busyTruck", BUSY_TRUCK);
            return "manager_dashboard";
        } else {
            LocalDate deliveryDate = parseDate(date);
            if (deliveryDate.getDayOfWeek().equals(SUNDAY)) {
                logger.warn("The selected day is a off day for truck drivers!");
                model.addAttribute("user", userService.findUser(principal.getName()));
                model.addAttribute("orders", orderService.getSortedOrders());
                model.addAttribute("now", LocalDate.now().plusDays(DEFAULT_VALUE));
                model.addAttribute("offDay", TRUCK_DRIVER_OFF_DAY);
                return "manager_dashboard";
            } else if (orderToDeliver.getItemQuantities().size() > 10) {
                logger.warn("The Order item number surpasses max truck amount!");
                model.addAttribute("user", userService.findUser(principal.getName()));
                model.addAttribute("orders", orderService.getSortedOrders());
                model.addAttribute("now", LocalDate.now().plusDays(DEFAULT_VALUE));
                model.addAttribute("maxTruckItemAmount", MAX_TRUCK_ITEM_AMOUNT);
                return "manager_dashboard";
            }
            orderToDeliver.setDeadline(deliveryDate);
            orderToDeliver.setStatus(UNDER_DELIVERY.getStatus());
            orderService.updateOrder(orderToDeliver);

            truck.setOrderToDeliver(orderToDeliver);
            truckService.updateTruck(truck);
            return "redirect:/";
        }
    }

    private LocalDate parseDate(String date) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, dateFormatter);
    }
}
