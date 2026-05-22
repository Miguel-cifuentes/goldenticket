package com.example.goldenticket2.repository;

import com.example.goldenticket2.entity.Role;
import com.example.goldenticket2.entity.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
