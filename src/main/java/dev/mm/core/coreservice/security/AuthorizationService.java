package dev.mm.core.coreservice.security;

import dev.mm.core.coreservice.exception.ForbiddenException;
import dev.mm.core.coreservice.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static dev.mm.core.coreservice.constants.Role.SUPER_ADMIN;
import static dev.mm.core.coreservice.util.TranslationUtil.translate;
import static java.util.Collections.singleton;

@Service
public class AuthorizationService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    public void ensureUserIsSuperAdmin(UserDetailsImpl userDetails) {
        if (userRoleRepository.countByUserIdAndRoleIdIn(userDetails.getId(), singleton(SUPER_ADMIN.id)) != 1) {
            throw new ForbiddenException(translate("Required super admin role"));
        }
    }

}
