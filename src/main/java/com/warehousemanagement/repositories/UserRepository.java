package com.warehousemanagement.repositories;

import com.warehousemanagement.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByIdIs(Long id);

    User findByEmail(String email);
}
