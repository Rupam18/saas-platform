package com.rupam.saas.repository;

import com.rupam.saas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    // All users belonging to a company
    List<User> findByCompanyId(Long companyId);

    // For dashboard stats
    Long countByCompanyId(Long companyId);
}

