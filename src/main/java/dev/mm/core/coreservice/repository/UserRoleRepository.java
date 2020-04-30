package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.UserRole;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface UserRoleRepository extends JpaQueryDslRepository<UserRole, Long> {

    int countByUserIdAndRoleIdIn(long userId, Set<Long> roleIds);

    int countByOrganizationIdAndUserIdAndRoleIdIn(long organizationId, long userId, Set<Long> roleIds);

    List<UserRole> findByUserIdInAndOrganizationId(Set<Long> userIds, long organizationId);

    List<UserRole> findByUserIdIn(Set<Long> userIds);

    @Transactional
    int deleteByUserIdAndOrganizationIdIsNull(long userId);

    @Transactional
    int deleteByUserIdAndOrganizationId(long userId, long organizationId);

}
