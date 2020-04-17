package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.UserRole;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface UserRoleRepository extends JpaQueryDslRepository<UserRole, Long> {

    @Query("SELECT count(ur.id) FROM UserRole ur inner join Role r on r.id = ur.roleId where ur.userId = ?1 and r.name in ?2")
    int countHowManyRolesUserHasFromSet(long userId, Set<String> roleNames);

}
