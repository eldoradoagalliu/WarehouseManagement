package com.warehousemanagement.controller;

import com.warehousemanagement.model.User;
import com.warehousemanagement.service.InventoryService;
import com.warehousemanagement.service.OrderService;
import com.warehousemanagement.service.TruckService;
import com.warehousemanagement.service.UserService;
import com.warehousemanagement.util.UserValidator;
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

import static com.warehousemanagement.constant.Constants.*;

@Controller
@RequestMapping(API_PATH + "/account")
@RequiredArgsConstructor
public class UserController {

    public static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserValidator userValidator;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final TruckService truckService;

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        logger.info("In Client Dashboard");
        User currentUser = userService.findUser(principal.getName());
        model.addAttribute(CURRENT_USER, currentUser);
        return "dashboard";
    }

    @GetMapping("/manage/warehouse")
    public String managerDashboard(Principal principal, Model model) {
        logger.info("In Warehouse Manager Dashboard");
        model.addAttribute(CURRENT_USER, userService.findUser(principal.getName()));
        model.addAttribute(ORDERS, orderService.getSortedOrders());
        model.addAttribute(TRUCKS, truckService.getAllTrucks());
        model.addAttribute(TODAY_DATE, LocalDate.now().plusDays(DEFAULT_VALUE));
        return "manager_dashboard";
    }

    @GetMapping("/system/admin")
    public String adminDashboard(@ModelAttribute(USER) User user, Model model) {
        logger.info("In System Administrator Dashboard");
        model.addAttribute(USERS, userService.getNonAdminUsers());
        return "admin_dashboard";
    }

    @GetMapping("/{id}")
    public String accountDetails(@PathVariable(ID) Long userId, @ModelAttribute(USER) User user, Model model) {
        logger.info("In Account Details");
        model.addAttribute(CURRENT_USER, userService.findUser(userId));
        return "account_details";
    }

    @PostMapping("/request/password/change/{id}")
    public String requestPasswordChange(@PathVariable(ID) Long userId, @RequestParam(NEW_PASSWORD) String newPassword,
                                        Model model) {
        logger.info("User requested password change");
        User currentUser = userService.findUser(userId);
        if (userService.passwordMatches(newPassword, currentUser.getPassword())) {
            logger.warn("The new password is the same as the old password!");
            model.addAttribute(CURRENT_USER, currentUser);
            model.addAttribute("passwordMatches", OLD_PASSWORD_REUSE);
            return "account_details";
        } else {
            currentUser.setNewRequestedPassword(newPassword);
            userService.updateUser(currentUser);
        }
        return "redirect:" + REDIRECT_USER_API_PATH;
    }

    @PostMapping("/approve/password/change/{id}")
    public String changeAccountPassword(@PathVariable(ID) Long userId, @RequestParam(NEW_PASSWORD) String newPassword,
                                        Model model) {
        logger.info("Approve password change");
        if (!newPassword.isEmpty()) {
            boolean passwordChanged = userService.changePassword(userId, newPassword);
            model.addAttribute("passwordChanged", passwordChanged);
            if (passwordChanged) {
                logger.info("Password changed successfully");
                model.addAttribute("successfulPasswordChange", SUCCESSFUL_PASSWORD_CHANGE);
            } else {
                logger.warn("Reused old password by user!");
                model.addAttribute("reusedOldPassword", REUSED_OLD_PASSWORD);
            }
        }
        model.addAttribute(USERS, userService.getNonAdminUsers());
        return "admin_dashboard";
    }

    @GetMapping("/{id}/edit")
    public String editAccount(@PathVariable(ID) Long userId, @ModelAttribute(USER) User user, Model model) {
        logger.info("In Edit Account Details");
        model.addAttribute(USER, userService.findUser(userId));
        return "edit_account_details";
    }

    @PutMapping("/{id}")
    public String editAccount(@PathVariable(ID) Long userId, @Valid @ModelAttribute(USER) User editedUser, BindingResult result) {
        logger.info("Edit Account Details");
        User currentUser = userService.findUser(userId);
        userValidator.validateEmail(currentUser, editedUser, result);

        if (result.hasErrors()) {
            return "edit_account_details";
        } else {
            editedUser.setPassword(currentUser.getPassword());
            editedUser.setNewRequestedPassword(currentUser.getNewRequestedPassword());
            editedUser.setRoles(currentUser.getRoles());
            editedUser.setOrders(currentUser.getOrders());
            userService.updateUser(editedUser);
            return "redirect:" + REDIRECT_USER_API_PATH;
        }
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable(ID) Long userId) {
        logger.info("Delete User");
        User currentUser = userService.findUser(userId);
        currentUser.getOrders().forEach(inventoryService::returnItemsInInventory);
        userService.deleteUser(currentUser);
        return "redirect:" + REDIRECT_USER_API_PATH;
    }
}
