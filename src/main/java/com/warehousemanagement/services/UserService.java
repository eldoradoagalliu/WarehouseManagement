package com.warehousemanagement.services;

import com.warehousemanagement.models.User;
import com.warehousemanagement.models.enums.RoleEnum;
import com.warehousemanagement.repositories.RoleRepository;
import com.warehousemanagement.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder bCryptPwEncoder;

    public UserService(UserRepository userRepo, RoleRepository roleRepo, BCryptPasswordEncoder bCryptPwEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.bCryptPwEncoder = bCryptPwEncoder;
    }

    public void createUser(User user, String role) {
        user.setPassword(bCryptPwEncoder.encode(user.getPassword()));
        user.setRoles(roleRepo.findByName(role));
        userRepo.save(user);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public List<User> getNonAdminUsers() {
        return userRepo.findAll().stream()
                .filter(user -> !user.getRoles().get(0).getName().equals(RoleEnum.SYSTEM_ADMIN.getRole()))
                .toList();
    }

    public User findUser(Long id) {
        return userRepo.findByIdIs(id);
    }

    public User findUser(String email) {
        return userRepo.findByEmail(email);
    }

    public void updateUser(User user) {
        userRepo.save(user);
    }

    public void deleteUser(User user) {
        userRepo.delete(user);
    }

    public boolean principalIsNull(Principal principal) {
        return Objects.isNull(findUser(principal.getName()));
    }

    public boolean isAdmin(Principal principal) {
        User currentUser = findUser(principal.getName());
        return currentUser.getRoles().get(0).getName().equals(RoleEnum.SYSTEM_ADMIN.getRole());
    }

    public boolean isManager(Principal principal) {
        User currentUser = findUser(principal.getName());
        return currentUser.getRoles().get(0).getName().equals(RoleEnum.WAREHOUSE_MANAGER.getRole());
    }

    public boolean changePassword(Long userId, String newPassword) {
        User currentUser = findUser(userId);
        String encodedNewPassword = bCryptPwEncoder.encode(newPassword);

        if (!passwordMatches(newPassword, currentUser.getPassword())) {
            currentUser.setPassword(encodedNewPassword);
            currentUser.setNewRequestedPassword(null);
            userRepo.save(currentUser);
            return true;
        }
        return false;
    }

    public boolean passwordMatches(String newPassword, String oldPassword) {
        return bCryptPwEncoder.matches(newPassword, oldPassword);
    }
}
