package com.zay.springsecurity.service;

import com.zay.springsecurity.jwt.JwtTokenUtil;
import com.zay.springsecurity.model.Role;
import com.zay.springsecurity.model.User;
import com.zay.springsecurity.model.VerificationToken;
import com.zay.springsecurity.payload.request.LoginRequest;
import com.zay.springsecurity.payload.request.RegisterRequest;
import com.zay.springsecurity.payload.response.LoginResponse;
import com.zay.springsecurity.repository.RoleRepository;
import com.zay.springsecurity.repository.UserRepository;
import com.zay.springsecurity.repository.VerificationTokenRepository;
import com.zay.springsecurity.util.OnCreateAccountEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PasswordEncoder encoder;

    //the login controller
    @Override
    public ResponseEntity<?> loginAndCreateToken (LoginRequest loginRequest) throws Exception{
        authenticate(loginRequest.getUsername(),loginRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        Set<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Not found user with username: " + loginRequest.getUsername()));

        return ResponseEntity.ok(new LoginResponse(user.getFirstName()+" "+user.getLastName(),userDetails.getUsername(),token,roles));
    }

    //Find all users from the database
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //register user and save into database
    @Override
    public ResponseEntity<?> registerUser(RegisterRequest registerRequest) {
        //transform registerRequest model to user model
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        user.setVerified(false);
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName("ROLE_USER");
        roles.add(role);
        user.setRoles(roles);

        //save in the database
        userRepository.save(user);
        //send email verification
        eventPublisher.publishEvent(new OnCreateAccountEvent(user,"zay"));

        return ResponseEntity.ok("Your account has been registered with the username "+user.getUsername()+
                "\n The verification email has been sent to "+user.getEmail());
    }

    @Override
    public void confirmUser(String token) {
        //retrieve token
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        //verify date
        if (verificationToken.getExpiryDate().after(new Date())) {
            //change verified=false to true and give him role admin
            User oldUser = userRepository.findByUsername(verificationToken.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Not found user with username: " + verificationToken.getUsername()));

            User newUser = userRepository.findByUsername(verificationToken.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Not found user with username: " + verificationToken.getUsername()));

            newUser.setVerified(true);
            Set<Role> roles = newUser.getRoles();
            Role role = roleRepository.findByName("ROLE_ADMIN");
            roles.add(role);
            newUser.setRoles(roles);
            BeanUtils.copyProperties(newUser, oldUser);
            userRepository.save(oldUser);

            //delete from token
            verificationTokenRepository.delete(verificationToken);
        }
    }

    //create verification token for email verification
    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUsername(user.getUsername());
        verificationToken.setExpiryDate(verificationToken.calculateExpiryDate(verificationToken.EXPIRATION));

        verificationTokenRepository.save(verificationToken);
    }


    //Authentication method used to authenticate the Login Request
    private void authenticate(String username, String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }


}
