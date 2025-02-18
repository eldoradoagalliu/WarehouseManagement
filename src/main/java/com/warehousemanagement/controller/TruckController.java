package com.warehousemanagement.controller;

import com.warehousemanagement.model.Order;
import com.warehousemanagement.model.Truck;
import com.warehousemanagement.service.OrderService;
import com.warehousemanagement.service.TruckService;
import com.warehousemanagement.service.UserService;
import com.warehousemanagement.util.DateFormatter;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Objects;

import static com.warehousemanagement.constant.Constants.*;
import static com.warehousemanagement.model.enums.OrderStatus.UNDER_DELIVERY;
import static java.time.DayOfWeek.SUNDAY;

@Controller
@RequestMapping(API_PATH + "/truck")
@RequiredArgsConstructor
public class TruckController {

    public static Logger logger = LoggerFactory.getLogger(TruckController.class);

    private final UserService userService;
    private final TruckService truckService;
    private final OrderService orderService;

    @GetMapping
    public String addItem(@ModelAttribute(TRUCK) Truck truck, Model model) {
        logger.info("In Truck input form");
        model.addAttribute(TRUCKS, truckService.getAllTrucks());
        return "truck";
    }

    @PostMapping
    public String addTruck(@Valid @ModelAttribute(TRUCK) Truck truck, BindingResult result, Model model) {
        logger.info("Add new Truck");
        if (result.hasErrors() || Objects.nonNull(truckService.findTruck(truck.getChassisNumber()))) {
            if (Objects.nonNull(truckService.findTruck(truck.getChassisNumber()))) {
                logger.error("Truck already exists");
                model.addAttribute("chassisNumberExists", CHASSIS_NUMBER_EXISTS);
            }
            model.addAttribute(TRUCKS, truckService.getAllTrucks());
            return "truck";
        } else {
            truckService.addTruck(truck);
            return "redirect:/truck";
        }
    }

    @GetMapping("/{id}")
    public String editTruck(@PathVariable(ID) Long truckId, Model model) {
        logger.info("In Edit Truck Details");
        model.addAttribute(TRUCK, truckService.findTruck(truckId));
        return "edit_truck_details";
    }

    @PutMapping("/{id}")
    public String editTruck(@PathVariable(ID) Long truckId, @Valid @ModelAttribute(TRUCK) Truck editedTruck,
                            BindingResult result, Model model) {
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
    public String deleteTruck(@PathVariable(ID) Long truckId) {
        logger.info("Delete Truck");
        Truck truck = truckService.findTruck(truckId);
        truckService.deleteTruck(truck);
        return "redirect:" + REDIRECT_USER_API_PATH;
    }

    @PostMapping("/schedule/delivery/{orderNumber}")
    public String scheduleOrderDelivery(Principal principal, @RequestParam(DATE) String date,
                                        @RequestParam(LICENSE_PLATE) String licensePlate,
                                        @PathVariable(ORDER_NUMBER) Long orderNumber, Model model) {
        logger.info("Schedule order truck delivery");
        Truck truck = truckService.findTruckByPlate(licensePlate);
        Order orderToDeliver = orderService.findOrder(orderNumber);

        model.addAttribute(USER, userService.findUser(principal.getName()));
        model.addAttribute(ORDERS, orderService.getSortedOrders());
        model.addAttribute(TODAY_DATE, LocalDate.now().plusDays(DEFAULT_VALUE));

        // One day to deliver the order
        if (truck.getOrderToDeliver() != null) {
            logger.warn("The requested truck is busy!");
            model.addAttribute("busyTruck", BUSY_TRUCK);
            return "manager_dashboard";
        } else {
            LocalDate deliveryDate = DateFormatter.parseDate(date);
            if (deliveryDate.getDayOfWeek().equals(SUNDAY)) {
                logger.warn("The selected day is a off day for truck drivers!");
                model.addAttribute("offDay", TRUCK_DRIVER_OFF_DAY);
                return "manager_dashboard";
            } else if (orderToDeliver.getItemQuantities().size() > 10) {
                logger.warn("The Order item number surpasses max truck amount!");
                model.addAttribute("maxTruckItemAmount", MAX_TRUCK_ITEM_AMOUNT);
                return "manager_dashboard";
            }
            orderToDeliver.setDeadline(deliveryDate);
            orderToDeliver.setStatus(UNDER_DELIVERY.getStatus());
            orderService.updateOrder(orderToDeliver);

            truck.setOrderToDeliver(orderToDeliver);
            truckService.updateTruck(truck);
            return "redirect:" + REDIRECT_USER_API_PATH;
        }
    }
}
