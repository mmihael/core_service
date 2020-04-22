package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.Role;

import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaQueryDslRepository<Role, Long> {

    int  countByIdInAndOrganizationRoleIsTrue(Set<Long> roleIds);

    List<Role> findByIdInAndOrganizationRoleIsTrue(Set<Long> roleIds);

}
