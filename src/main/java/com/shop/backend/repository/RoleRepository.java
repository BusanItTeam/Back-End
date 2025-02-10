package com.shop.backend.repository;

import com.shop.backend.models.AppRole;
import com.shop.backend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRole appRole);
}
