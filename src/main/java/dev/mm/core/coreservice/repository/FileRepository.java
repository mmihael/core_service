package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.constants.FileAccessType;
import dev.mm.core.coreservice.model.File;

import java.util.Optional;

public interface FileRepository extends JpaQueryDslRepository<File, Long> {

    Optional<File> findOneByIdAndOrganizationId(long id, long organizationId);

    Optional<File> findOneByIdAndOrganizationIdAndAccessType(long id, long organizationId, FileAccessType accessType);

    Optional<File> findOneByIdAndAndAccessTypeAndOrganizationIdIsNull(long id, FileAccessType accessType);

}
