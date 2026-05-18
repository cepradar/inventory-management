package com.inventory.repository;

import com.inventory.model.RolePermission;
import com.inventory.model.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {

    @Query("SELECT rp FROM RolePermission rp JOIN FETCH rp.permission WHERE rp.role.name = :roleName")
    List<RolePermission> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT rp FROM RolePermission rp JOIN FETCH rp.permission " +
           "WHERE rp.role.name = :roleName AND rp.permission.code = :code")
    Optional<RolePermission> findByRoleNameAndPermissionCode(
            @Param("roleName") String roleName, @Param("code") String code);

    /** Solo los permisos activamente otorgados para un rol */
    @Query("SELECT rp FROM RolePermission rp JOIN FETCH rp.permission " +
           "WHERE rp.role.name = :roleName AND rp.isActive = true")
    List<RolePermission> findActiveByRoleName(@Param("roleName") String roleName);

    /** Códigos de permisos activos para un rol (para autorización rápida) */
    @Query("SELECT rp.permission.code FROM RolePermission rp " +
           "WHERE rp.role.name = :roleName AND rp.isActive = true " +
           "AND rp.permission.active = true")
    List<String> findActivePermissionCodesByRoleName(@Param("roleName") String roleName);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.role.name = :roleName")
    void deleteByRoleName(@Param("roleName") String roleName);
}
