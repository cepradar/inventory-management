package com.inventory.repository;

import com.inventory.model.PermisosUsuarioId;
import com.inventory.model.Permisos_Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisosUsuarioRepository extends JpaRepository<Permisos_Usuario, PermisosUsuarioId> {

    @Query("SELECT pu FROM Permisos_Usuario pu WHERE pu.role.name = :roleName")
    List<Permisos_Usuario> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT pu FROM Permisos_Usuario pu WHERE pu.role.name = :roleName AND pu.permission.name = :permName")
    Optional<Permisos_Usuario> findByRoleNameAndPermissionName(@Param("roleName") String roleName, @Param("permName") String permName);
}
