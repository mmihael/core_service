package dev.mm.core.coreservice.service;

import dev.mm.core.coreservice.dto.organization.OrganizationDto;
import dev.mm.core.coreservice.dto.response.AppConfigurationResponse;
import dev.mm.core.coreservice.dto.response.OrgAppConfigurationResponse;
import dev.mm.core.coreservice.dto.user.UserDto;
import dev.mm.core.coreservice.dto.userRole.UserRoleDto;
import dev.mm.core.coreservice.repository.OrganizationRepository;
import dev.mm.core.coreservice.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Service
public class AppConfigurationService {

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationRepository organizationRepository;

    public AppConfigurationResponse getAppConfigurationFor(UserDetailsImpl userDetails) {
        UserDto userDto = userService.getUserDtoById(userDetails.getId());
        List<OrganizationDto> organizationDtos = Collections.emptyList();
        if (isNotEmpty(userDto.getUserRoles())) {
            Set<Long> organizationIds = userDto.getUserRoles().stream().map(UserRoleDto::getOrganizationId)
                .filter(Objects::nonNull).collect(toSet());
            if (!organizationIds.isEmpty()) {
                organizationDtos = organizationRepository.findByIdInAndDeletedIsFalse(organizationIds).stream()
                    .map(OrganizationDto::new).collect(toList());
            }
        }
        return new AppConfigurationResponse(userDto, organizationDtos);
    }

    public OrgAppConfigurationResponse getOrganizationAppConfigurationFor(long organizationId, UserDetailsImpl userDetails) {
        OrganizationDto organizationDto = organizationService.getOrganizationDtoById(organizationId);
        UserDto userDto = userService.getUserFromOrganizationById(organizationId, userDetails.getId());
        return new OrgAppConfigurationResponse(userDto, organizationDto);
    }
}
