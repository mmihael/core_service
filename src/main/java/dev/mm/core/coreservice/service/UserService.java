package dev.mm.core.coreservice.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import dev.mm.core.coreservice.dto.response.PageResponseDto;
import dev.mm.core.coreservice.dto.role.RoleDto;
import dev.mm.core.coreservice.dto.user.CreateUpdateUserDto;
import dev.mm.core.coreservice.dto.user.UserDto;
import dev.mm.core.coreservice.dto.user.UserPageRequestDto;
import dev.mm.core.coreservice.dto.userRole.UserRoleDto;
import dev.mm.core.coreservice.exception.EntityNotFoundException;
import dev.mm.core.coreservice.model.Organization;
import dev.mm.core.coreservice.model.QUser;
import dev.mm.core.coreservice.model.QUserRole;
import dev.mm.core.coreservice.model.Role;
import dev.mm.core.coreservice.model.User;
import dev.mm.core.coreservice.model.UserRole;
import dev.mm.core.coreservice.repository.RoleRepository;
import dev.mm.core.coreservice.repository.UserRepository;
import dev.mm.core.coreservice.repository.UserRoleRepository;
import dev.mm.core.coreservice.util.MapUtil;
import dev.mm.core.coreservice.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static dev.mm.core.coreservice.util.StringUtil.isNotBlank;
import static dev.mm.core.coreservice.util.TranslationUtil.translate;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Service
public class UserService {

    private static final String USER_WITH_ID_NOT_FOUND_TEMPLATE = "User with id: {{ id }} not found";

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private OrganizationService organizationService;

    public PageResponseDto usersPage(UserPageRequestDto userPageRequestDto) {
        return usersPage(null, userPageRequestDto);
    }

    public PageResponseDto usersPage(Long organizationId, UserPageRequestDto userPageRequestDto) {

        String username = userPageRequestDto.getUsername();
        Set<Long> hasAnyRoleId = userPageRequestDto.getHasAnyRoleId();
        Long notInOrganizationId = userPageRequestDto.getNotInOrganizationId();
        Long inOrganizationId = userPageRequestDto.getInOrganizationId();

        PageResponseDto pageResponseDto = paginationService.getPage(userPageRequestDto, jpaQuery -> {

            JPAQuery queryBase = jpaQuery.from(QUser.user).distinct();

            BooleanExpression booleanExpression = QUser.user.deleted.eq(false);

            if (isNotEmpty(hasAnyRoleId)) {
                booleanExpression = booleanExpression.and(QUser.user.userRoles.any().userId.in(hasAnyRoleId));
            }

            if (organizationId != null) {
                queryBase.innerJoin(QUserRole.userRole).on(QUser.user.id.eq(QUserRole.userRole.userId));
                booleanExpression = booleanExpression.and(QUserRole.userRole.organizationId.eq(organizationId));
            } else if (notInOrganizationId != null) {
                queryBase.leftJoin(QUserRole.userRole).on(QUser.user.id.eq(QUserRole.userRole.userId));
                booleanExpression = booleanExpression.and(QUser.user.id.notIn(
                    JPAExpressions
                        .select(QUserRole.userRole.userId)
                        .from(QUserRole.userRole)
                        .where(QUserRole.userRole.organizationId.eq(notInOrganizationId))
                ));
            }

            if (isNotBlank(username)) {
                booleanExpression = booleanExpression.and(QUser.user.username.containsIgnoreCase(username));
            }

            if (booleanExpression != null) { queryBase.where(booleanExpression); }

            queryBase.orderBy(QUser.user.id.asc());

        });

        boolean isOrgSearch = organizationId != null;

        List<User> content = (List<User>) pageResponseDto.getContent();
        List<UserDto> dtoContent = Collections.emptyList();

        if (!content.isEmpty()) {

            dtoContent = content.stream().map(user -> new UserDto(user, false)).collect(toList());

            Set<Long> userIds = dtoContent.stream().map(UserDto::getId).collect(toSet());

            List<UserRoleDto> userRoleDtos = (isOrgSearch ?
                userRoleRepository.findByUserIdInAndOrganizationId(userIds, organizationId) :
                userRoleRepository.findByUserIdIn(userIds)
            ).stream().map(userRole -> new UserRoleDto(userRole, false)).collect(toList());

            Set<Long> roleIds = userRoleDtos.stream().map(UserRoleDto::getRoleId).collect(toSet());

            Map<Long, RoleDto> roleDtoMap = (isOrgSearch ?
                roleRepository.findByIdInAndOrganizationRoleIsTrue(roleIds) :
                roleRepository.findAllById(roleIds)
            ).stream().collect(toMap(Role::getId, RoleDto::new));

            for (UserRoleDto userRoleDto : userRoleDtos) {
                userRoleDto.setRole(roleDtoMap.get(userRoleDto.getRoleId()));
            }

            Map<Long, List<UserRoleDto>> userRolesMap = userRoleDtos.stream()
                .collect(groupingBy(UserRoleDto::getUserId));

            for (UserDto userDto : dtoContent) {
                userDto.setUserRoles(userRolesMap.get(userDto.getId()));
            }
        }

        pageResponseDto.setContent(dtoContent);

        return pageResponseDto;
    }

    public UserDto getUserDtoById(long userId) {
        return new UserDto(getUserWithRolesOrThrow(userId));
    }

    public UserDto getUserFromOrganizationById(long organizationId, long userId) {
        return new UserDto(getUserFromOrganizationWithRolesOrThrow(organizationId, userId));
    }

    @Transactional
    public UserDto validateAndCreateUser(CreateUpdateUserDto createUpdateUserDto) {
        userValidator.validateCreateRequest(createUpdateUserDto);
        return createUser(createUpdateUserDto);
    }

    @Transactional
    public UserDto validateAndCreateUserInOrganization(CreateUpdateUserDto createUpdateUserDto, long organizationId) {
        userValidator.validateCreateForOrganizationRequest(createUpdateUserDto);
        return createUser(createUpdateUserDto, organizationId);
    }

    public UserDto createUser(CreateUpdateUserDto createUpdateUserDto) {
        return createUser(createUpdateUserDto, null);
    }

    public UserDto createUser(CreateUpdateUserDto createUpdateUserDto, Long organizationId) {

        User user = new User();
        user.setUsername(createUpdateUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(createUpdateUserDto.getPassword()));
        user.setEnabled(createUpdateUserDto.getEnabled());

        if (isNotEmpty(createUpdateUserDto.getRoleIds())) {
            setGroupsToUser(user, createUpdateUserDto.getRoleIds(), organizationId);
        }

        return new UserDto(userRepository.save(user));
    }

    @Transactional
    public UserDto validateAndUpdateUser(long userId, CreateUpdateUserDto createUpdateUserDto) {
        userValidator.validateUpdateRequest(createUpdateUserDto);
        return updateUser(userId, createUpdateUserDto);
    }

    @Transactional
    public UserDto validateAndUpdateUserInOrganization(
        long userId,
        CreateUpdateUserDto createUpdateUserDto,
        long organizationId
    ) {
        userValidator.validateUpdateForOrganizationRequest(createUpdateUserDto);
        return updateUser(userId, createUpdateUserDto, organizationId);
    }

    public UserDto updateUser(long userId, CreateUpdateUserDto createUpdateUserDto) {
        return updateUser(userId, createUpdateUserDto, null);
    }

    public UserDto updateUser(long userId, CreateUpdateUserDto createUpdateUserDto, Long organizationId) {

        User user = null;

        Set<Long> roleIdsForUpdate;

        if (organizationId != null) {
            user = getUserFromOrganizationWithRolesOrThrow(organizationId, userId);
            Set<Long> roleIds = createUpdateUserDto.getRoleIds();
            List<UserRole> organizationRolesForDelete = new ArrayList<>();
            List<UserRole> organizationRolesToKeep = new ArrayList<>();
            user.getUserRoles().stream()
                .filter(userRole -> organizationId.equals(userRole.getOrganizationId()))
                .forEach(userRole -> {
                    if (roleIds.contains(userRole.getRoleId())) {
                        organizationRolesToKeep.add(userRole);
                    } else {
                        organizationRolesForDelete.add(userRole);
                    }
                });

            Set<Long> userRoleIdsForOrganization = new HashSet<>();
            organizationRolesForDelete.forEach(userRole -> {
                userRoleIdsForOrganization.add(userRole.getId());
                userRole.setOrganization(null);
                userRole.setRole(null);
                userRole.setUser(null);
            });
            user.getUserRoles().removeIf(userRole -> userRoleIdsForOrganization.contains(userRole.getId()));
            userRoleRepository.deleteAll(organizationRolesForDelete);
            Set<Long> presentRoleIds = organizationRolesToKeep.stream().map(UserRole::getRoleId).collect(toSet());
            roleIdsForUpdate = roleIds.stream().filter(id -> !presentRoleIds.contains(id)).collect(toSet());
        } else {
            userRoleRepository.deleteByUserIdAndOrganizationIdIsNull(userId);
            user = getUserWithRolesOrThrow(userId);
            roleIdsForUpdate = createUpdateUserDto.getRoleIds();
        }

        user.setEnabled(createUpdateUserDto.getEnabled());

        if (isNotEmpty(roleIdsForUpdate)) {
            setGroupsToUser(user, roleIdsForUpdate, organizationId);
        }

        return new UserDto(userRepository.save(user));
    }

    public UserDto deleteUser(long userId) {
        return deleteUserInOrganization(userId, null);
    }

    public UserDto deleteUserInOrganization(long userId, Long organizationId) {
        User user = organizationId != null ?
            getUserFromOrganizationWithRolesOrThrow(organizationId, userId) :
            getUserWithRolesOrThrow(userId);
        user.setDeleted(true);
        return new UserDto(userRepository.save(user));
    }

    public User getUserOrThrow(long userId) {
        return userRepository
            .findById(userId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    translate(USER_WITH_ID_NOT_FOUND_TEMPLATE, singletonMap("id", userId))
                )
            );
    }

    public List<User> getAllUsersInOrThrow(Set<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new EntityNotFoundException("Not able to find all users");
        }
        return users;
    }

    public User getUserWithRolesOrThrow(long userId) {
        return userRepository
            .findAllWithRolesWhereUserId(userId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    translate(USER_WITH_ID_NOT_FOUND_TEMPLATE, singletonMap("id", userId))
                )
            );
    }

    public User getUserFromOrganizationWithRolesOrThrow(long organizationId, long userId) {
        return userRepository
            .findAllWithRolesWhereUserIdAndOrganizationId(userId, organizationId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    translate(
                        "User with id: {{ userId }} not found in organization with id: {{ organizationId }}",
                        MapUtil.mapFromKeyVal("userId", userId, "organizationId", organizationId)
                    )
                )
            );
    }

    private void setGroupsToUser(User user, Set<Long> roleIds, Long organizationId) {
        Organization organization = null;
        if (organizationId != null) {
            organization = organizationService.getOrganizationByIdOrThrow(organizationId);
        }
        for (Role role : roleRepository.findAllById(roleIds)) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            if (organization != null) { userRole.setOrganization(organization); }
            user.getUserRoles().add(userRole);
        }
    }

}
