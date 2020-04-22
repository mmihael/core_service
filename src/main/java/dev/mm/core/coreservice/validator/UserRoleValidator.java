package dev.mm.core.coreservice.validator;

import dev.mm.core.coreservice.exception.ValidationErrorException;
import dev.mm.core.coreservice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static dev.mm.core.coreservice.util.TranslationUtil.translate;

@Service
public class UserRoleValidator {

    @Autowired
    private RoleRepository roleRepository;

    public void validateRoleIdsAreForOrganization(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new ValidationErrorException(
                "roleIds", translate("At least one role is required for user in organization")
            );
        }
        if (roleRepository.countByIdInAndOrganizationRoleIsTrue(roleIds) != roleIds.size()) {
            throw new ValidationErrorException(
                "roleIds", translate("Some of roles do not exist or not organization level roles")
            );
        }
    }
}
