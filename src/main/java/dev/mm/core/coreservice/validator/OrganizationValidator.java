package dev.mm.core.coreservice.validator;

import dev.mm.core.coreservice.dto.organization.CreateOrganizationDto;
import dev.mm.core.coreservice.exception.ValidationErrorException;
import dev.mm.core.coreservice.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static dev.mm.core.coreservice.util.StringUtil.isBlank;
import static dev.mm.core.coreservice.util.TranslationUtil.translate;

@Service
public class OrganizationValidator {

    @Autowired
    private OrganizationRepository organizationRepository;

    public void validateUpdateOrganization(CreateOrganizationDto createOrganizationDto) {
        validateCreateOrganization(createOrganizationDto);
    }

    public void validateCreateOrganization(CreateOrganizationDto createOrganizationDto) {

        if (isBlank(createOrganizationDto.getName())) {
            throw new ValidationErrorException("name", translate("Name must not be blank"));
        }

        if (organizationRepository.countByName(createOrganizationDto.getName()) > 0) {
            throw new ValidationErrorException("username", translate("Name already taken"));
        }
    }
}
