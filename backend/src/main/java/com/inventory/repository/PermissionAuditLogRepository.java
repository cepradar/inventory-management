package com.inventory.repository;

import com.inventory.model.PermissionAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionAuditLogRepository extends JpaRepository<PermissionAuditLog, Long> {

    List<PermissionAuditLog> findByRoleNameOrderByChangedAtDesc(String roleName);

    List<PermissionAuditLog> findByPermissionCodeOrderByChangedAtDesc(String permissionCode);
}
