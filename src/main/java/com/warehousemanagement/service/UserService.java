package com.warehousemanagement.service;

import com.warehousemanagement.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User findUser(Long id);

    User findUser(String email);

    User findUserByEmail(String email);

    void updateUser(User user);

    void deleteUser(User user);

    List<User> getNonAdminUsers();

    boolean changePassword(Long userId, String newPassword);

    boolean passwordMatches(String newPassword, String oldPassword);
}
