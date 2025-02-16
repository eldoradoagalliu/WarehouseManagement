package com.warehousemanagement.service.implementation;

import com.warehousemanagement.model.User;
import com.warehousemanagement.model.enums.UserRole;
import com.warehousemanagement.repository.UserRepository;
import com.warehousemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

import static com.warehousemanagement.constant.Constants.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getNonAdminUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getRoles().get(0).getName().equals(UserRole.SYSTEM_ADMIN.getRole()))
                .toList();
    }

    @Override
    public User findUser(Long id) {
        return userRepository.findByIdIs(id);
    }

    @Override
    public User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmailIs(email);
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public boolean isPrincipalNull(Principal principal) {
        return Objects.isNull(findUser(principal.getName()));
    }

    @Override
    public boolean isAdmin(Principal principal) {
        User currentUser = findUser(principal.getName());
        return currentUser.getRoles().get(0).getName().equals(UserRole.SYSTEM_ADMIN.getRole());
    }

    @Override
    public boolean isManager(Principal principal) {
        User currentUser = findUser(principal.getName());
        return currentUser.getRoles().get(0).getName().equals(UserRole.WAREHOUSE_MANAGER.getRole());
    }

    @Override
    public boolean changePassword(Long userId, String newPassword) {
        User currentUser = findUser(userId);
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        if (!passwordMatches(newPassword, currentUser.getPassword())) {
            currentUser.setPassword(encodedNewPassword);
            currentUser.setNewRequestedPassword(null);
            userRepository.save(currentUser);
            return true;
        }
        return false;
    }

    @Override
    public boolean passwordMatches(String newPassword, String oldPassword) {
        return passwordEncoder.matches(newPassword, oldPassword);
    }
}
