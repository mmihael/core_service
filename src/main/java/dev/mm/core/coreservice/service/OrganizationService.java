package dev.mm.core.coreservice.service;

import com.querydsl.core.types.dsl.BooleanExpression;
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
import dev.mm.core.coreservice.repository.UserRoleRepository;
import dev.mm.core.coreservice.validator.OrganizationValidator;
import dev.mm.core.coreservice.validator.UserRoleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private UserRoleRepository userRoleRepository;

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

            jpaQuery.from(QOrganization.organization).distinct();

            BooleanExpression booleanExpression = QOrganization.organization.deleted.eq(false);

            if (isNotBlank(name)) {
                booleanExpression = booleanExpression.and(QOrganization.organization.name.containsIgnoreCase(name));
            }

            jpaQuery.where(booleanExpression);

            jpaQuery.orderBy(QOrganization.organization.id.asc());

        });

        pageResponseDto.setContent(((List<Organization>) pageResponseDto.getContent()).stream()
            .map(OrganizationDto::new).collect(Collectors.toList()));

        return pageResponseDto;
    }

    @Transactional
    public OrganizationDto validateAndCreateOrganization(CreateOrganizationDto createOrganizationDto) {
        organizationValidator.validateCreateOrganization(createOrganizationDto);
        Organization organization = new Organization();
        organization.setName(createOrganizationDto.getName());
        return new OrganizationDto(organizationRepository.save(organization));
    }

    @Transactional
    public OrganizationDto validateAndUpdateOrganization(long organizationId, CreateOrganizationDto createOrganizationDto) {
        Organization organization = getOrganizationByIdOrThrow(organizationId);
        organizationValidator.validateUpdateOrganization(organization, createOrganizationDto);
        organization.setName(createOrganizationDto.getName());
        return new OrganizationDto(organizationRepository.save(organization));
    }

    @Transactional
    public UserDto validateAndAssignUserToOrganization(long organizationId, AssignUsersToOrganizationRequestDto dto) {
        userRoleValidator.validateRoleIdsAreForOrganization(dto.getRoleIds());
        return assignUserToOrganization(organizationId, dto);
    }

    @Transactional
    public UserDto assignUserToOrganization(long organizationId, AssignUsersToOrganizationRequestDto dto) {
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Organization organization = getOrganizationByIdOrThrow(organizationId);
            User user = userService.getUserWithRolesOrThrow(dto.getUserId());
            List<Role> roles = roleRepository.findByIdInAndOrganizationRoleIsTrue(dto.getRoleIds());

            List<UserRole> organizationUserRoles = user.getUserRoles().stream()
                    .filter(userRole -> userRole.getOrganizationId() != null && userRole.getOrganizationId() == organizationId)
                    .collect(Collectors.toList());

            List<UserRole> forRemove = new ArrayList<>();
            Set<Long> existingRoles = new HashSet<>();

            for (UserRole userRole : organizationUserRoles) {
                if (dto.getRoleIds().contains(userRole.getRoleId())) {
                    existingRoles.add(userRole.getRoleId());
                } else {
                    userRole.setUser(null);
                    userRole.setOrganization(null);
                    forRemove.add(userRole);
                }
            }

            user.getUserRoles().removeAll(forRemove);

            user.getUserRoles().addAll(roles.stream().filter(role -> !existingRoles.contains(role.getId())).map(role -> {
                UserRole userRole = new UserRole();
                userRole.setRole(role);
                userRole.setOrganization(organization);
                userRole.setUser(user);
                user.getUserRoles().add(userRole);
                return userRole;
            }).collect(Collectors.toSet()));
            userRoleRepository.deleteAll(forRemove);
            return new UserDto(userRepository.save(user));
        } else {
            userRoleRepository.deleteByUserIdAndOrganizationId(dto.getUserId(), organizationId);
            return new UserDto(userService.getUserWithRolesOrThrow(dto.getUserId()));
        }
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
