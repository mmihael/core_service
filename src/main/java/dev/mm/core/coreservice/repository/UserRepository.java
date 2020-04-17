package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaQueryDslRepository<User, Long> {

    User findByUsername(String username);

    @Query("SELECT u FROM User u inner join fetch u.userRoles ur inner join fetch ur.role r where u.id in ?1")
    List<User> findAllWithRolesWhereUserIdIn(Set<Long> userIds);

}
