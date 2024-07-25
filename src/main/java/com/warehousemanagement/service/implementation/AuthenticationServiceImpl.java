package com.warehousemanagement.service.implementation;

import com.warehousemanagement.model.User;
import com.warehousemanagement.model.dto.AuthenticationRequest;
import com.warehousemanagement.model.dto.AuthenticationResponse;
import com.warehousemanagement.model.dto.RegisterRequest;
import com.warehousemanagement.repository.RoleRepository;
import com.warehousemanagement.repository.UserRepository;
import com.warehousemanagement.service.AuthenticationService;
import com.warehousemanagement.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.warehousemanagement.model.constant.Constants.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepo;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request, String role) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roleRepo.findByName(role))
                .build();
        userRepository.save(user);

        var jwt = jwtUtil.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwt)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent()) {
            var jwt = jwtUtil.generateToken(user.get());
            return AuthenticationResponse.builder()
                    .token(jwt)
                    .build();
        } else {
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        }
    }
}
