package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.Organization;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaQueryDslRepository<Organization, Long> {

    Optional<Organization> findByIdAndDeletedIsFalse(long id);

    List<Organization> findByIdInAndDeletedIsFalse(Collection<Long> organizationIds);

    int countByIdAndDeletedIsFalse(long id);

    int countByName(String name);

}
