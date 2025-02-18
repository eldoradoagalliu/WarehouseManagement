package com.warehousemanagement.controller;

import com.warehousemanagement.model.User;
import com.warehousemanagement.model.dto.AuthenticationResponse;
import com.warehousemanagement.service.UserService;
import com.warehousemanagement.service.implementation.AuthenticationService;
import com.warehousemanagement.util.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Objects;

import static com.warehousemanagement.constant.Constants.*;

@Controller
@RequestMapping(API_PATH)
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final UserValidator userValidator;

    @GetMapping("/register")
    public String registerUser(@ModelAttribute(USER) User user, Model model) {
        logger.info("Register attempt");
        model.addAttribute("adminDoesntExist", userService.getAllUsers().isEmpty());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(Principal principal, @Valid @ModelAttribute(USER) User user, BindingResult result,
                               Model model, @RequestParam(value = ROLE, required = false) String role) {
        userValidator.validate(user, result);
        if (result.hasErrors() || Objects.nonNull(userService.findUserByEmail(user.getEmail()))) {
            if (Objects.nonNull(userService.findUserByEmail(user.getEmail()))) {
                logger.error("The Requested Email already exists");
                model.addAttribute("emailExists", EMAIL_EXISTS);
            }
            model.addAttribute("adminDoesntExist", userService.getAllUsers().isEmpty());
            return "register";
        }
        authenticationService.register(user, role);

        AuthenticationResponse response = authenticationService.getAuthenticatedUserRole(principal.getName());
        return redirectAuthenticatedUserToView(response.getRole());
    }

    @GetMapping("/login")
    public String login(@ModelAttribute(USER) User user, @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout, Model model) {
        logger.info("Login attempt");
        if (error != null) {
            logger.warn("Invalid username or password!");
            model.addAttribute("errorMessage", INVALID_CREDENTIALS);
        }
        if (logout != null) {
            logger.info("Successfully logged out!");
            model.addAttribute("logoutMessage", SUCCESSFUL_LOGOUT);
        }
        model.addAttribute("adminDoesntExist", userService.getAllUsers().isEmpty());
        return "login";
    }

    @PostMapping("/redirect")
    public String redirect(Principal principal) {
        if (principal == null) {
            return "redirect:/api/v1/logout";
        }
        AuthenticationResponse response = authenticationService.getAuthenticatedUserRole(principal.getName());
        return redirectAuthenticatedUserToView(response.getRole());
    }

    private String redirectAuthenticatedUserToView(String userRole) {
        logger.info("Redirect authenticated user to view");
        return switch (userRole) {
            case CLIENT -> "redirect:/api/v1/account/dashboard";
            case WAREHOUSE_MANAGER -> "redirect:/api/v1/account/manage/warehouse";
            case SYSTEM_ADMIN -> "redirect:/api/v1/account/system/admin";
            default -> "redirect:/api/v1/logout";
        };
    }
}
