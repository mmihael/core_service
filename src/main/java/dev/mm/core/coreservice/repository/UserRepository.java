package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaQueryDslRepository<User, Long> {

    User findByUsernameAndDeletedIsFalse(String username);

    User findByUsername(String username);

    int countByUsername(String username);

    Optional<User> findByIdAndDeletedIsFalseAndEnabledIsTrue(long id);

    @Query("SELECT distinct u FROM User u left join fetch u.userRoles ur left join fetch ur.role r where u.id = ?1 and u.deleted = false")
    Optional<User> findAllWithRolesWhereUserId(long userId);

    @Query("SELECT distinct u FROM User u inner join fetch u.userRoles ur inner join fetch ur.role r where u.id = ?1 and u.deleted = false and ur.organizationId = ?2")
    Optional<User> findAllWithRolesWhereUserIdAndOrganizationId(long userId, long organizationId);

    @Query("SELECT distinct u FROM User u left join fetch u.userRoles ur left join fetch ur.role r where u.id in ?1")
    List<User> findAllWithRolesWhereUserIdIn(Set<Long> userIds);

    @Query("SELECT distinct u FROM User u left join fetch u.userRoles ur left join fetch ur.role r where u.id in ?1 and ur.organizationId = ?2")
    List<User> findAllWithOrgRolesWhereUserIdIn(Set<Long> userIds, long organizationId);

}
