package com.warehousemanagement.service.implementation;

import com.warehousemanagement.model.User;
import com.warehousemanagement.model.enums.RoleEnum;
import com.warehousemanagement.repository.UserRepository;
import com.warehousemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

import static com.warehousemanagement.model.constant.Constants.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

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
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
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
        String encodedNewPassword = passwordEncoder.encode(newPassword);

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
        return passwordEncoder.matches(newPassword, oldPassword);
    }
}
