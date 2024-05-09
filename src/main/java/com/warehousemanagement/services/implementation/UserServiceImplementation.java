package com.warehousemanagement.services.implementation;

import com.warehousemanagement.models.User;
import com.warehousemanagement.models.enums.RoleEnum;
import com.warehousemanagement.repositories.RoleRepository;
import com.warehousemanagement.repositories.UserRepository;
import com.warehousemanagement.services.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder bCryptPwEncoder;

    public UserServiceImplementation(UserRepository userRepo, RoleRepository roleRepo, BCryptPasswordEncoder bCryptPwEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.bCryptPwEncoder = bCryptPwEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public List<User> getNonAdminUsers() {
        return userRepo.findAll().stream()
                .filter(user -> !user.getRoles().get(0).getName().equals(RoleEnum.SYSTEM_ADMIN.getRole()))
                .toList();
    }

    @Override
    public User findUser(Long id) {
        return userRepo.findByIdIs(id);
    }

    @Override
    public User findUser(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public void createUser(User user, String role) {
        user.setPassword(bCryptPwEncoder.encode(user.getPassword()));
        user.setRoles(roleRepo.findByName(role));
        userRepo.save(user);
    }

    @Override
    public void updateUser(User user) {
        userRepo.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepo.delete(user);
    }

    @Override
    public boolean principalIsNull(Principal principal) {
        return Objects.isNull(findUser(principal.getName()));
    }

    @Override
    public boolean isAdmin(Principal principal) {
        User currentUser = findUser(principal.getName());
        return currentUser.getRoles().get(0).getName().equals(RoleEnum.SYSTEM_ADMIN.getRole());
    }

    @Override
    public boolean isManager(Principal principal) {
        User currentUser = findUser(principal.getName());
        return currentUser.getRoles().get(0).getName().equals(RoleEnum.WAREHOUSE_MANAGER.getRole());
    }

    @Override
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

    @Override
    public boolean passwordMatches(String newPassword, String oldPassword) {
        return bCryptPwEncoder.matches(newPassword, oldPassword);
    }
}
