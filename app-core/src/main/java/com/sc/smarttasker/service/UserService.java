package com.sc.smarttasker.service;

import com.sc.smarttasker.constants.Role;
import com.sc.smarttasker.entity.Account;
import com.sc.smarttasker.exceptions.EmailAlreadyExistsException;
import com.sc.smarttasker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(final UserRepository userRepository, final PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public Account findUserById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public Account getUser(Account account) {
        return this.userRepository.findByEmail(account.getEmail())
                .filter(storedUser -> this.encoder.matches(account.getPassword(), storedUser.getPassword()))
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public Account updateUser(Long id, Account account) {
        Account existingAccount = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        existingAccount.setName(account.getName());
        existingAccount.setEmail(account.getEmail());

        Set<Role> updatedRoles = Optional.ofNullable(existingAccount.getRoles())
                .orElseGet(HashSet::new);
        if (account.getRoles() != null) {
            updatedRoles.addAll(account.getRoles());
        }
        existingAccount.setRoles(updatedRoles);

        return userRepository.save(existingAccount);
    }


    public Account save(Account account) {
        Optional<Account> existingUser = this.userRepository.findByEmail(account.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException("Email already Exists");
        }
        account.setPassword(this.encoder.encode(account.getPassword()));
        account.setActive(true);
        Set<Role> updatedRoles = Optional.ofNullable(account.getRoles())
                .orElseGet(() -> Set.of(Role.ROLE_USER));
        account.setRoles(updatedRoles);
        return this.userRepository.save(account);
    }

    public Optional<Account> getUserByRoles(Role role) {
        return this.userRepository.findByRolesContaining(role);
    }

    public List<Account> getUsers() {
        return this.userRepository.findAll();
    }

    public void deleteAccount(long id) {
        this.userRepository.deleteById(id);
    }
}
