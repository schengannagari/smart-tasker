package com.sc.smarttasker.controller;

import com.sc.smarttasker.entity.Account;
import com.sc.smarttasker.service.UserService;
import com.sc.smarttasker.utils.JwtUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtUtils utils;

    @GetMapping("/csrf-token")
    public ResponseEntity<CsrfToken> csrfToken(CsrfToken csrfToken) {
        return ResponseEntity.ok(csrfToken);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        try {
            log.log(Level.DEBUG, "Inside the login method");
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword());
            Authentication authentication = manager.authenticate(token);
            log.log(Level.DEBUG, "Inside the login method authenticated: {}", authentication.isAuthenticated());
            if (authentication.isAuthenticated()) {
                Set<String> roles = authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());

                Map<String, Object> jwtToken = utils.generateTokenWithExpiry(authentication.getName(), roles);

                Map<String, Object> response = new HashMap<>();
                response.put("token", jwtToken.get("token"));
                response.put("expiresAt", jwtToken.get("expiresAt"));
                response.put("email", authentication.getName());
                response.put("roles", roles);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        log.log(Level.DEBUG, "Inside the register method");
        return ResponseEntity.ok(this.userService.save(account));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Account> findUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.userService.findUserById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Account> updateUser(@PathVariable("id") Long id, @RequestBody Account account) {
        log.log(Level.DEBUG, "Inside the updateUser method, id: {}", id);
        return ResponseEntity.ok(this.userService.updateUser(id, account));
    }

    @GetMapping("/list-users")
    public ResponseEntity<List<Account>> getUsers() {
        log.log(Level.DEBUG, "Inside the getUsers method");
        return ResponseEntity.ok(this.userService.getUsers());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(@RequestParam Long id) {
        log.log(Level.DEBUG, "Inside the deleteAccount method, id: {}", id);
        this.userService.deleteAccount(id);
        return ResponseEntity.ok("Account deleted successfully");
    }

}
