package com.warehousemanagement.service.implementation;

import com.warehousemanagement.model.User;
import com.warehousemanagement.model.dto.AuthenticationResponse;
import com.warehousemanagement.repository.RoleRepository;
import com.warehousemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.warehousemanagement.constant.Constants.INVALID_CREDENTIALS;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }
        return new UsernamePasswordAuthenticationToken(userOpt.get(), null, userOpt.get().getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void register(User user, String role) {
        var newUser = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .roles(roleRepository.findByName(role))
                .build();
        userRepository.save(newUser);
    }

    public AuthenticationResponse redirectAuthenticatedUser(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        String userRole = StringUtils.EMPTY;
        if (userOpt.isPresent()) {
            userRole = userOpt.get().getUserRole();
        }
        return AuthenticationResponse.builder()
                .role(userRole)
                .build();
    }
}
