package com.warehousemanagement.validation;

import com.warehousemanagement.models.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        User user = (User) object;
        if (!user.getConfirmedPassword().equals(user.getPassword())) {
            errors.rejectValue("confirmedPassword", "Match");
        }
    }
}
