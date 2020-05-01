package dev.mm.core.coreservice.validator;

import dev.mm.core.coreservice.dto.user.CreateUpdateUserDto;
import dev.mm.core.coreservice.exception.ValidationErrorException;
import dev.mm.core.coreservice.repository.RoleRepository;
import dev.mm.core.coreservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static dev.mm.core.coreservice.util.TranslationUtil.translate;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.hibernate.internal.util.collections.CollectionHelper.isEmpty;
import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Service
public class UserValidator {

    @Autowired
    private UserRoleValidator userRoleValidator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public void validateCreateRequest(CreateUpdateUserDto createUpdateUserDto) {

        validateCreateRequestCommon(createUpdateUserDto);

        if (isNotEmpty(createUpdateUserDto.getRoleIds())) {
            userRoleValidator.validateRoleIdsAreNotForOrganization(createUpdateUserDto.getRoleIds());
        }

        validateUpdateRequest(createUpdateUserDto);
    }

    public void validateCreateRequestCommon(CreateUpdateUserDto createUpdateUserDto) {

        if (isBlank(createUpdateUserDto.getUsername())) {
            throw new ValidationErrorException("username", translate("Username must not be blank"));
        }

        if (userRepository.countByUsername(createUpdateUserDto.getUsername()) > 0) {
            throw new ValidationErrorException("username", translate("Username already taken"));
        }

        if (isBlank(createUpdateUserDto.getPassword())) {
            throw new ValidationErrorException("password", translate("Password must not be blank"));
        }

        if (createUpdateUserDto.getPassword().length() < 7) {
            throw new ValidationErrorException("password", translate("Password must have more than 6 chars"));
        }
    }

    public void validateUpdateRequest(CreateUpdateUserDto createUpdateUserDto) {
        if (createUpdateUserDto.getEnabled() == null) {
            throw new ValidationErrorException("enabled", translate("Enabled must be set true or false"));
        }
    }

    public void validateCreateForOrganizationRequest(CreateUpdateUserDto createUpdateUserDto) {
        validateCreateRequestCommon(createUpdateUserDto);
        validateRoleIdsArePresentAndForOrganization(createUpdateUserDto);
    }

    public void validateUpdateForOrganizationRequest(CreateUpdateUserDto createUpdateUserDto) {
        validateUpdateRequest(createUpdateUserDto);
        validateRoleIdsArePresentAndForOrganization(createUpdateUserDto);
    }

    public void validateRoleIdsArePresentAndForOrganization(CreateUpdateUserDto createUpdateUserDto) {
        if (isEmpty(createUpdateUserDto.getRoleIds())) {
            throw new ValidationErrorException("roleIds", translate("At least one role is required"));
        }
        userRoleValidator.validateRoleIdsAreForOrganization(createUpdateUserDto.getRoleIds());
    }

}
