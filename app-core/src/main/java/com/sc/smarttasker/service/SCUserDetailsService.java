package com.sc.smarttasker.service;

import com.sc.smarttasker.entity.Account;
import com.sc.smarttasker.repository.UserRepository;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@Data
public class SCUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        log.log(Level.DEBUG, "Inside the loadUserByUsername, username: {}", username);
        Account account = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        {
                            log.log(Level.ERROR, "User Not found");
                            return new UsernameNotFoundException("User not found: " + username);
                        }
                );

        log.log(Level.DEBUG, "Inside the loadUserByUsername, account: {}", account.getEmail());
        List<SimpleGrantedAuthority> authorities =
                account.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .toList();

        return new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPassword(),
                account.isActive(),
                true,
                true,
                true,
                authorities
        );
    }

}
