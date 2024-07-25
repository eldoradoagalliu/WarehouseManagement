package com.warehousemanagement.util;

import com.warehousemanagement.model.User;
import com.warehousemanagement.model.dto.RegisterRequest;
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
        RegisterRequest user = (RegisterRequest) object;
        if (!user.getConfirmedPassword().equals(user.getPassword())) {
            errors.rejectValue("confirmedPassword", "Match");
        }
    }
}
