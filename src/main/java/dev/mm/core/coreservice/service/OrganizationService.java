package dev.mm.core.coreservice.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import dev.mm.core.coreservice.dto.organization.AssignUsersToOrganizationRequestDto;
import dev.mm.core.coreservice.dto.organization.CreateOrganizationDto;
import dev.mm.core.coreservice.dto.organization.OrganizationDto;
import dev.mm.core.coreservice.dto.organization.OrganizationPageRequestDto;
import dev.mm.core.coreservice.dto.response.PageResponseDto;
import dev.mm.core.coreservice.dto.user.UserDto;
import dev.mm.core.coreservice.exception.EntityNotFoundException;
import dev.mm.core.coreservice.model.Organization;
import dev.mm.core.coreservice.model.QOrganization;
import dev.mm.core.coreservice.model.Role;
import dev.mm.core.coreservice.model.User;
import dev.mm.core.coreservice.model.UserRole;
import dev.mm.core.coreservice.repository.OrganizationRepository;
import dev.mm.core.coreservice.repository.RoleRepository;
import dev.mm.core.coreservice.repository.UserRepository;
import dev.mm.core.coreservice.validator.OrganizationValidator;
import dev.mm.core.coreservice.validator.UserRoleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static dev.mm.core.coreservice.util.StringUtil.isNotBlank;
import static dev.mm.core.coreservice.util.TranslationUtil.translate;
import static java.util.Collections.singletonMap;

@Service
public class OrganizationService {

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private UserRoleValidator userRoleValidator;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationValidator organizationValidator;

    public PageResponseDto getPage(OrganizationPageRequestDto organizationPageRequestDto) {

        String name = organizationPageRequestDto.getName();

        PageResponseDto pageResponseDto = paginationService.getPage(organizationPageRequestDto, jpaQuery -> {

            JPAQuery queryBase = jpaQuery.from(QOrganization.organization).distinct();

            BooleanExpression booleanExpression = QOrganization.organization.deleted.eq(false);

            if (isNotBlank(name)) {
                booleanExpression = booleanExpression.and(QOrganization.organization.name.containsIgnoreCase(name));
            }

            queryBase.orderBy(QOrganization.organization.id.asc());

        });

        pageResponseDto.setContent(((List<Organization>) pageResponseDto.getContent()).stream()
            .map(OrganizationDto::new).collect(Collectors.toList()));

        return pageResponseDto;
    }

    public OrganizationDto validateAndCreateOrganization(CreateOrganizationDto createOrganizationDto) {
        organizationValidator.validateCreateOrganization(createOrganizationDto);
        Organization organization = new Organization();
        organization.setName(createOrganizationDto.getName());
        return new OrganizationDto(organizationRepository.save(organization));
    }

    public OrganizationDto validateAndUpdateOrganization(long organizationId, CreateOrganizationDto createOrganizationDto) {
        Organization organization = getOrganizationByIdOrThrow(organizationId);
        organizationValidator.validateUpdateOrganization(createOrganizationDto);
        organization.setName(createOrganizationDto.getName());
        return new OrganizationDto(organizationRepository.save(organization));
    }

    public UserDto validateAndAssignUserToOrganization(long organizationId, AssignUsersToOrganizationRequestDto dto) {
        userRoleValidator.validateRoleIdsAreForOrganization(dto.getRoleIds());
        return assignUserToOrganization(organizationId, dto);
    }

    public UserDto assignUserToOrganization(long organizationId, AssignUsersToOrganizationRequestDto dto) {
        Organization organization = getOrganizationByIdOrThrow(organizationId);
        User user = userService.getUserWithRolesOrThrow(dto.getUserId());
        List<Role> roles = roleRepository.findByIdInAndOrganizationRoleIsTrue(dto.getRoleIds());
        user.getUserRoles().addAll(roles.stream().map(role -> {
            UserRole userRole = new UserRole();
            userRole.setRole(role);
            userRole.setOrganization(organization);
            userRole.setUser(user);
            user.getUserRoles().add(userRole);
            return userRole;
        }).collect(Collectors.toSet()));
        return new UserDto(userRepository.save(user));
    }

    public OrganizationDto deleteOrganization(long organizationId) {
        Organization organization = getOrganizationByIdOrThrow(organizationId);
        organization.setDeleted(true);
        return new OrganizationDto(organizationRepository.save(organization));
    }

    public OrganizationDto getOrganizationDtoById(long organizationId) {
       return new OrganizationDto(getOrganizationByIdOrThrow(organizationId));
    }

    public Organization getOrganizationByIdOrThrow(long organizationId) {
        return organizationRepository
            .findByIdAndDeletedIsFalse(organizationId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    translate("Organization with id: {{ id }} not found", singletonMap("id", organizationId))
                )
            );
    }

}
