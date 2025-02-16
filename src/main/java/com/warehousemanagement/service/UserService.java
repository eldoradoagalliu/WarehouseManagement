package com.warehousemanagement.service;

import com.warehousemanagement.model.User;

import java.security.Principal;
import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    List<User> getNonAdminUsers();

    User findUser(Long id);

    User findUser(String email);

    User findUserByEmail(String email);

    void updateUser(User user);

    void deleteUser(User user);

    boolean isPrincipalNull(Principal principal);

    boolean isAdmin(Principal principal);

    boolean isManager(Principal principal);

    boolean changePassword(Long userId, String newPassword);

    boolean passwordMatches(String newPassword, String oldPassword);
}
