package dev.mm.core.coreservice.validator;

import dev.mm.core.coreservice.dto.organization.CreateOrganizationDto;
import dev.mm.core.coreservice.exception.ValidationErrorException;
import dev.mm.core.coreservice.model.Organization;
import dev.mm.core.coreservice.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static dev.mm.core.coreservice.util.StringUtil.isBlank;
import static dev.mm.core.coreservice.util.TranslationUtil.translate;

@Service
public class OrganizationValidator {

    @Autowired
    private OrganizationRepository organizationRepository;

    public void validateUpdateOrganization(Organization organization, CreateOrganizationDto createOrganizationDto) {

        validateNameIsPresent(createOrganizationDto);

        if (!createOrganizationDto.getName().equals(organization.getName())) {
            validateNameIsUnique(createOrganizationDto);
        }
    }

    public void validateCreateOrganization(CreateOrganizationDto createOrganizationDto) {
        validateNameIsPresent(createOrganizationDto);
        validateNameIsUnique(createOrganizationDto);
    }

    public void validateNameIsUnique(CreateOrganizationDto createOrganizationDto) {
        if (organizationRepository.countByName(createOrganizationDto.getName()) > 0) {
            throw new ValidationErrorException("name", translate("Name already taken"));
        }
    }

    public void validateNameIsPresent(CreateOrganizationDto createOrganizationDto) {
        if (isBlank(createOrganizationDto.getName())) {
            throw new ValidationErrorException("name", translate("Name must not be blank"));
        }
    }
}
