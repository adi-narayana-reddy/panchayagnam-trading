package com.py.repo;

import com.py.dto.RoleCountProjection;
import com.py.entity.PyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PyUserRepository extends JpaRepository<PyUser, Long> {

    Optional<PyUser> findByEmail(String email);

    Optional<PyUser> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    List<PyUser> findByRoleAndIsActive(PyUser.Role role, Boolean isActive);

    Optional<PyUser> findById(long id);
    // Count by role

    long countByRole(PyUser.Role role);

// Count by role and active
    long countByRoleAndIsActive(PyUser.Role role, Boolean isActive);

    Optional<PyUser> findByIdAndRole(Long id, PyUser.Role role);

    @Query("SELECT CAST(u.role AS string) AS role, COUNT(u) AS count FROM PyUser u GROUP BY u.role")
    List<RoleCountProjection> countAllByRole();
}
