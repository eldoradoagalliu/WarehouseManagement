package com.warehousemanagement.controllers;

import com.warehousemanagement.models.User;
import com.warehousemanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Objects;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    static final String SUCCESSFUL_PASSWORD_CHANGE = "User Password changed successfully!";
    static final String REUSED_OLD_PASSWORD = "The user is using the old account password! User needs to try with another password.";

    @GetMapping("/account/{id}")
    public String userDetails(Principal principal, @PathVariable("id") Long userId, @ModelAttribute("user") User user, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/logout";
        model.addAttribute("user", userService.findUser(userId));
        return "user_details";
    }

    @GetMapping("/account/edit/{id}")
    public String editUser(Principal principal, @PathVariable("id") Long userId, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/logout";
        model.addAttribute("user", userService.findUser(userId));
        return "edit_user_details";
    }

    @PutMapping("/account/edit/{id}")
    public String editUser(Principal principal, @PathVariable("id") Long userId,
                           @Valid @ModelAttribute("user") User editedUser, BindingResult result, Model model) {
        User currentUser = userService.findUser(userId);
        boolean emailExists = Objects.nonNull(userService.findUser(editedUser.getEmail())) && !editedUser.getEmail().equals(currentUser.getEmail());

        if (result.hasErrors() || emailExists) {
            if (emailExists) {
                model.addAttribute("emailExistsErrorMessage", "This email has been used by another user!");
            }
            return "edit_user_details";
        } else {
            editedUser.setPassword(currentUser.getPassword());
            editedUser.setNewRequestedPassword(currentUser.getNewRequestedPassword());
            editedUser.setRoles(currentUser.getRoles());
            userService.updateUser(editedUser);
            return "redirect:/";
        }
    }

    @PostMapping("/request/password/change/{id}")
    public String requestPasswordChange(Principal principal, @PathVariable("id") Long userId,
                                        @RequestParam("newPassword") String newPassword, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/logout";
        User currentUser = userService.findUser(userId);

        if (userService.passwordMatches(newPassword, currentUser.getPassword())) {
            model.addAttribute("user", currentUser);
            model.addAttribute("passwordMatches", "You are using the old password! Please, try a new one.");
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

        if (!newPassword.isEmpty()) {
            boolean passwordChanged = userService.changePassword(userId, newPassword);
            model.addAttribute("passwordChanged", passwordChanged);
            if (passwordChanged) {
                model.addAttribute("successfulChangeMessage", SUCCESSFUL_PASSWORD_CHANGE);
            } else {
                model.addAttribute("reusedOldPasswordMessage", REUSED_OLD_PASSWORD);
            }
        }
        model.addAttribute("users", userService.getNonAdminUsers());

        return "admin_dashboard";
    }

    @DeleteMapping("/account/delete/{id}")
    public String deleteUser(Principal principal, @PathVariable("id") Long userId) {
        if (userService.principalIsNull(principal)) return "redirect:/logout";
        User currentUser = userService.findUser(userId);
        userService.deleteUser(currentUser);
        return "redirect:/";
    }
}
