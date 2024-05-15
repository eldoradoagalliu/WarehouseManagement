package com.warehousemanagement.repository;

import com.warehousemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByIdIs(Long id);

    User findByEmail(String email);
}
