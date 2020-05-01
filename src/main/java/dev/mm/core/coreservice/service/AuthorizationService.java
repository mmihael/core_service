package dev.mm.core.coreservice.service;

import dev.mm.core.coreservice.exception.EntityNotFoundException;
import dev.mm.core.coreservice.exception.ForbiddenException;
import dev.mm.core.coreservice.repository.OrganizationRepository;
import dev.mm.core.coreservice.repository.UserRoleRepository;
import dev.mm.core.coreservice.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

import static dev.mm.core.coreservice.constants.Role.ORGANIZATION_MEMBER;
import static dev.mm.core.coreservice.constants.Role.ORGANIZATION_OWNER;
import static dev.mm.core.coreservice.constants.Role.SUPER_ADMIN;
import static dev.mm.core.coreservice.util.TranslationUtil.translate;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;

@Service
public class AuthorizationService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    public void ensureUserIsSuperAdmin(UserDetailsImpl userDetails) {
        if (userRoleRepository.countByUserIdAndRoleIdIn(userDetails.getId(), singleton(SUPER_ADMIN.id)) != 1) {
            throw new ForbiddenException(translate("Required super admin role"));
        }
    }

    public void ensureUserIsOrganizationOwner(long organizationId, long userId) {
        ensureOrganizationExists(organizationId);
        int rolesCount = userRoleRepository.countByOrganizationIdAndUserIdAndRoleIdIn(
            organizationId, userId, singleton(ORGANIZATION_OWNER.id)
        );
        if (rolesCount != 1) {
            throw new ForbiddenException(translate("Required organization owner role"));
        }
    }

    public void ensureUserIsOrganizationOwnerOrMember(long organizationId, long userId) {
        ensureOrganizationExists(organizationId);
        int rolesCount = userRoleRepository.countByOrganizationIdAndUserIdAndRoleIdIn(
            organizationId, userId, new HashSet<>(Arrays.asList(ORGANIZATION_OWNER.id, ORGANIZATION_MEMBER.id))
        );
        if (rolesCount < 1) {
            throw new ForbiddenException(translate("Required organization owner or member role"));
        }
    }

    public void ensureOrganizationExists(long organizationId) {
        if (organizationRepository.countByIdAndDeletedIsFalse(organizationId) == 0) {
            throw new EntityNotFoundException(
                translate("Organization with id: {{ id }} not found", singletonMap("id", organizationId))
            );
        }
    }

}
