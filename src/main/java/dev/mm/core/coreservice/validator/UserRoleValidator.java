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

    public void validateRoleIdsAreNotForOrganization(Set<Long> roleIds) {
        if (roleRepository.countByIdInAndOrganizationRoleIsFalse(roleIds) != roleIds.size()) {
            throw new ValidationErrorException(
                "roleIds", translate("Some of roles do not exist or are organization level roles")
            );
        }
    }

    public void validateRoleIdsAreForOrganization(Set<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty() && roleRepository.countByIdInAndOrganizationRoleIsTrue(roleIds) != roleIds.size()) {
            throw new ValidationErrorException(
                "roleIds", translate("Some of roles do not exist or not organization level roles")
            );
        }
    }
}
