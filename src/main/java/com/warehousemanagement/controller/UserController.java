package com.warehousemanagement.controller;

import com.warehousemanagement.model.User;
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
import java.util.Objects;

import static com.warehousemanagement.model.constant.Constants.EMAIL_EXISTS;
import static com.warehousemanagement.model.constant.Constants.OLD_PASSWORD_REUSE;
import static com.warehousemanagement.model.constant.Constants.REUSED_OLD_PASSWORD;
import static com.warehousemanagement.model.constant.Constants.SUCCESSFUL_PASSWORD_CHANGE;

@Controller
@RequestMapping(path = "/account")
public class UserController {

    public static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/{id}")
    public String userDetails(Principal principal, @PathVariable("id") Long userId, @ModelAttribute("user") User user, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/logout";
        logger.info("In Account Details");
        model.addAttribute("user", userService.findUser(userId));
        return "user_details";
    }

    @PostMapping("/request/password/change/{id}")
    public String requestPasswordChange(Principal principal, @PathVariable("id") Long userId,
                                        @RequestParam("newPassword") String newPassword, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/logout";
        logger.info("User requested password change");
        User currentUser = userService.findUser(userId);

        if (userService.passwordMatches(newPassword, currentUser.getPassword())) {
            logger.error("The old password is used as new password!");
            model.addAttribute("user", currentUser);
            model.addAttribute("passwordMatches", OLD_PASSWORD_REUSE);
            return "user_details";
        } else {
            currentUser.setNewRequestedPassword(newPassword);
            userService.updateUser(currentUser);
        }
        return "redirect:/";
    }

    @PostMapping("/approve/password/change/{id}")
    public String changeAccountPassword(Principal principal, @PathVariable("id") Long userId,
                                        @RequestParam("newPassword") String newPassword, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/logout";
        logger.info("Approve password change");

        if (!newPassword.isEmpty()) {
            boolean passwordChanged = userService.changePassword(userId, newPassword);
            model.addAttribute("passwordChanged", passwordChanged);
            if (passwordChanged) {
                logger.info("Password successfully changed");
                model.addAttribute("successfulPasswordChange", SUCCESSFUL_PASSWORD_CHANGE);
            } else {
                logger.error("Reused old password by user!");
                model.addAttribute("reusedOldPassword", REUSED_OLD_PASSWORD);
            }
        }
        model.addAttribute("users", userService.getNonAdminUsers());

        return "admin_dashboard";
    }

    @GetMapping("/edit/{id}")
    public String editUser(Principal principal, @PathVariable("id") Long userId, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/logout";
        logger.info("In User Details Edit");
        model.addAttribute("user", userService.findUser(userId));
        return "edit_user_details";
    }

    @PutMapping("/{id}")
    public String editUser(Principal principal, @PathVariable("id") Long userId,
                           @Valid @ModelAttribute("user") User editedUser, BindingResult result, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/logout";
        logger.info("Edit User Details");
        User currentUser = userService.findUser(userId);
        boolean emailExists = Objects.nonNull(userService.findUser(editedUser.getEmail())) && !editedUser.getEmail().equals(currentUser.getEmail());

        if (result.hasErrors() || emailExists) {
            if (emailExists) {
                logger.warn("The Requested Email already exists");
                model.addAttribute("emailExists", EMAIL_EXISTS);
            }
            return "edit_user_details";
        } else {
            editedUser.setPassword(currentUser.getPassword());
            editedUser.setNewRequestedPassword(currentUser.getNewRequestedPassword());
            editedUser.setRoles(currentUser.getRoles());
            editedUser.setOrders(currentUser.getOrders());
            userService.updateUser(editedUser);
            return "redirect:/";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteUser(Principal principal, @PathVariable("id") Long userId) {
        if (userService.principalIsNull(principal)) return "redirect:/logout";
        logger.info("Delete User");
        User currentUser = userService.findUser(userId);
        currentUser.getOrders().forEach(inventoryService::returnItemsInInventory);
        userService.deleteUser(currentUser);
        return "redirect:/";
    }
}
