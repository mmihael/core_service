package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.User;

public interface UserRepository extends JpaQueryDslRepository<User, Long> {

    User findByUsername(String username);

}
