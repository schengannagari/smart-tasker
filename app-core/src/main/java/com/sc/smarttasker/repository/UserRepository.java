package com.sc.smarttasker.repository;

import com.sc.smarttasker.constants.Role;
import com.sc.smarttasker.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Account, Long> {

    public Optional<Account> findByEmail(String email);

    public Optional<Account> findByRolesContaining(Role role);
}
