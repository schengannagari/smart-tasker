package com.sc.smarttasker;

import com.sc.smarttasker.constants.Role;
import com.sc.smarttasker.entity.Account;
import com.sc.smarttasker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

@SpringBootApplication
public class SmartTaskerApplication {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SmartTaskerApplication.class, args);
	}

	@Bean
	public CommandLineRunner run () {
		return (args) -> {
			Optional<Account> savedUser = this.userService.getUserByRoles(Role.ROLE_SUPER_ADMIN);
			if (savedUser.isEmpty()) {
				Account admin = new Account();
				admin.setEmail("admin@example.com");
				admin.setPassword(passwordEncoder.encode("supersecret"));
				admin.setActive(true);
				admin.setRoles(Set.of(Role.ROLE_SUPER_ADMIN));
				this.userService.save(admin);
				System.out.println("Super admin user created!");
			}
		};
	}

}
