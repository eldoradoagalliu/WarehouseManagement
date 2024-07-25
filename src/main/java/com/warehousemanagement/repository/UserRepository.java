package com.warehousemanagement.repository;

import com.warehousemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByIdIs(Long id);

    Optional<User> findByEmail(String email);
}
