package com.zay.springsecurity.service;


import com.zay.springsecurity.model.User;
import com.zay.springsecurity.payload.request.LoginRequest;
import com.zay.springsecurity.payload.request.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    ResponseEntity<?> loginAndCreateToken(LoginRequest loginRequest) throws Exception;

    List<User> getAllUsers();

    ResponseEntity<?> registerUser(RegisterRequest registerRequest);

    void createVerificationToken(User user, String token);

    void confirmUser(String token);
}
