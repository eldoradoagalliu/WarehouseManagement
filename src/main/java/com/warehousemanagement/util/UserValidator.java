package com.warehousemanagement.util;

import com.warehousemanagement.model.User;
import com.warehousemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        User user = (User) object;
        if (!user.getConfirmedPassword().equals(user.getPassword())) {
            errors.rejectValue("confirmedPassword", "PasswordMatch");
        }
    }

    public void validateData(User user, Errors errors) {
        Optional<User> userOpt = userRepository.findByEmail(user.getEmail());
        if (userOpt.isPresent()) {
            errors.rejectValue("email", "EmailExists");
            if (userOpt.get().getConfirmedPassword() != null) {
                validate(user, errors);
            }
        }
    }

    public void validateEmail(User currentUser, User user, Errors errors) {
        Optional<User> userOpt = userRepository.findByEmail(user.getEmail());
        if (userOpt.isPresent() && !currentUser.getEmail().equals(user.getEmail())) {
            errors.rejectValue("email", "EmailExists");
        }
    }
}
