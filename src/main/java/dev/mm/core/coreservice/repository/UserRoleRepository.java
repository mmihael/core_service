package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.UserRole;

import java.util.Set;

public interface UserRoleRepository extends JpaQueryDslRepository<UserRole, Long> {

    int countByUserIdAndRoleIdIn(long userId, Set<Long> roleIds);

}
