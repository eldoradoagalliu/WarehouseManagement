package com.warehousemanagement.service;

import com.warehousemanagement.model.dto.AuthenticationRequest;
import com.warehousemanagement.model.dto.AuthenticationResponse;
import com.warehousemanagement.model.dto.RegisterRequest;

public interface AuthenticationService {

    AuthenticationResponse register(RegisterRequest request, String role);

    AuthenticationResponse authenticate(AuthenticationRequest request);
}
