package com.warehousemanagement.controller;

import com.warehousemanagement.model.User;
import com.warehousemanagement.service.OrderService;
import com.warehousemanagement.service.UserService;
import com.warehousemanagement.validation.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Objects;

import static com.warehousemanagement.model.constant.Constants.DEFAULT_VALUE;
import static com.warehousemanagement.model.constant.Constants.EMAIL_EXISTS;
import static com.warehousemanagement.model.constant.Constants.INVALID_CREDENTIALS;
import static com.warehousemanagement.model.constant.Constants.SUCCESSFUL_LOGOUT;

@Controller
public class MainController {

    public static Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private OrderService orderService;

    @GetMapping("/")
    public String home(Principal principal, @ModelAttribute("user") User user, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/logout";

        if (userService.isAdmin(principal)) {
            model.addAttribute("users", userService.getNonAdminUsers());
//            return "redirect:/system/admin";
            return "admin_dashboard";
        } else if (userService.isManager(principal)) {
            return "redirect:/manage/warehouse";
        } else {
            return "redirect:/dashboard";
        }
    }

    @GetMapping("/login")
    public String login(@ModelAttribute("user") User user, @RequestParam(value = "error", required = false) String error,
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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        logger.info("Register user");
        model.addAttribute("adminDoesntExist", userService.getAllUsers().isEmpty());
        return "register";
    }

    @PostMapping("/register")
    public String registration(@Valid @ModelAttribute("user") User user, BindingResult result, HttpServletRequest request,
                               Model model, @RequestParam(value = "role", required = false) String role) {
        userValidator.validate(user, result);
        String password = user.getPassword();

        if (result.hasErrors() || Objects.nonNull(userService.findUser(user.getEmail()))) {
            if (Objects.nonNull(userService.findUser(user.getEmail())))
                logger.error("The Requested Email already exists");
            {
                model.addAttribute("emailExists", EMAIL_EXISTS);
            }
            model.addAttribute("adminDoesntExist", userService.getAllUsers().isEmpty());
            return "register";
        }
        userService.createUser(user, role);
//        authWithHttpServletRequest(request, user.getEmail(), password);

        return "redirect:/";
    }

    private void authWithHttpServletRequest(HttpServletRequest request, String email, String password) {
        try {
            logger.info("Authenticate User");
            request.login(email, password);
        } catch (ServletException e) {
            logger.error("Error while authenticating user", e);
        }
    }

    @GetMapping("/system/admin")
    public String adminDashboard(Principal principal, @ModelAttribute("user") User user, Model model) {
        logger.info("In System Administrator Dashboard");
        model.addAttribute("users", userService.getNonAdminUsers());
        return "admin_dashboard";
    }

    @GetMapping("/manage/warehouse")
    public String managerDashboard(Principal principal, Model model) {
        if (userService.principalIsNull(principal) || !userService.isManager(principal)) return "redirect:/logout";
        logger.info("In Warehouse Manager Dashboard");
        model.addAttribute("user", userService.findUser(principal.getName()));
        model.addAttribute("orders", orderService.getSortedOrders());
        model.addAttribute("now", LocalDate.now().plusDays(DEFAULT_VALUE));
        return "manager_dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        if (userService.principalIsNull(principal)) return "redirect:/logout";
        logger.info("In Client Dashboard");
        User currentUser = userService.findUser(principal.getName());
        model.addAttribute("user", currentUser);
        return "dashboard";
    }
}
