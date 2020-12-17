package com.zay.springsecurity.controller;

import com.zay.springsecurity.model.User;
import com.zay.springsecurity.payload.request.LoginRequest;
import com.zay.springsecurity.payload.request.RegisterRequest;
import com.zay.springsecurity.repository.UserRepository;
import com.zay.springsecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
        return userService.loginAndCreateToken(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) throws Exception {
        return userService.registerUser(registerRequest);
    }

    @GetMapping("/account")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUser() {
        return userService.getAllUsers();
    }

    @GetMapping("/zay/accountConfirm")
    public String confirmAccount (@RequestParam("token") String token) {
        userService.confirmUser(token);
        return "accountConfirmed";
    }

}
