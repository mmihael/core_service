package dev.mm.core.coreservice.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import dev.mm.core.coreservice.dto.response.PageResponseDto;
import dev.mm.core.coreservice.dto.user.CreateUpdateUserDto;
import dev.mm.core.coreservice.dto.user.UserDto;
import dev.mm.core.coreservice.dto.user.UserPageRequestDto;
import dev.mm.core.coreservice.exception.EntityNotFoundException;
import dev.mm.core.coreservice.model.QUser;
import dev.mm.core.coreservice.model.Role;
import dev.mm.core.coreservice.model.User;
import dev.mm.core.coreservice.model.UserRole;
import dev.mm.core.coreservice.repository.RoleRepository;
import dev.mm.core.coreservice.repository.UserRepository;
import dev.mm.core.coreservice.repository.UserRoleRepository;
import dev.mm.core.coreservice.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;
import static dev.mm.core.coreservice.util.TranslationUtil.translate;
import static java.util.Collections.singletonMap;

@Service
public class UserService {

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

    public PageResponseDto usersPage(UserPageRequestDto userPageRequestDto) {

        String username = userPageRequestDto.getUsername();
        Set<Long> hasAnyRoleId = userPageRequestDto.getHasAnyRoleId();

        PageResponseDto pageResponseDto = paginationService.getPage(userPageRequestDto, jpaQuery -> {

            JPAQuery queryBase = jpaQuery.from(QUser.user);

            BooleanExpression booleanExpression = QUser.user.deleted.eq(false);

            if (isNotEmpty(hasAnyRoleId)) {
                booleanExpression = QUser.user.userRoles.any().userId.in(hasAnyRoleId);
            }

            if (isNotBlank(username)) {
                BooleanExpression subExpression = QUser.user.username.containsIgnoreCase(username);
                booleanExpression = booleanExpression != null ? booleanExpression.and(subExpression) : subExpression;
            }

            if (booleanExpression != null) { queryBase.where(booleanExpression); }

            queryBase.orderBy(QUser.user.id.asc());

        });

        Set<Long> userIds = ((List<User>) pageResponseDto.getContent()).stream().map(User::getId)
            .collect(Collectors.toSet());

        pageResponseDto.setContent(
            userRepository.findAllWithRolesWhereUserIdIn(userIds).stream().map(UserDto::new).collect(toList())
        );

        return pageResponseDto;
    }

    public UserDto getUserDtoById(long userId) {
        return new UserDto(getUserOrThrow(userId));
    }

    public UserDto validateAndCreateUser(CreateUpdateUserDto createUpdateUserDto) {
        userValidator.validateCreateRequest(createUpdateUserDto);
        return createUser(createUpdateUserDto);
    }

    public UserDto createUser(CreateUpdateUserDto createUpdateUserDto) {

        User user = new User();
        user.setUsername(createUpdateUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(createUpdateUserDto.getPassword()));
        user.setEnabled(createUpdateUserDto.getEnabled());

        if (isNotEmpty(createUpdateUserDto.getRoleIds())) {
            setGroupsToUser(user, createUpdateUserDto.getRoleIds());
        }

        return new UserDto(userRepository.save(user));
    }

    public UserDto validateAndUpdateUser(long userId, CreateUpdateUserDto createUpdateUserDto) {
        userValidator.validateUpdateRequest(createUpdateUserDto);
        return updateUser(userId, createUpdateUserDto);
    }

    public UserDto updateUser(long userId, CreateUpdateUserDto createUpdateUserDto) {

        User user = getUserOrThrow(userId);

        user.setEnabled(createUpdateUserDto.getEnabled());
        user.setUserRoles(new HashSet<>());

        if (isNotEmpty(createUpdateUserDto.getRoleIds())) {
            setGroupsToUser(user, createUpdateUserDto.getRoleIds());
        }

        return new UserDto(userRepository.save(user));
    }

    public UserDto deleteUser(long userId) {
        User user = getUserOrThrow(userId);
        user.setDeleted(true);
        return new UserDto(userRepository.save(user));
    }

    public User getUserOrThrow(long userId) {
        return userRepository
            .findAllWithRolesWhereUserId(userId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    translate("User with {{ id }} not found", singletonMap("id", userId))
                )
            );
    }

    private void setGroupsToUser(User user, Set<Long> roleIds) {
        for (Role role : roleRepository.findAllById(roleIds)) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            user.getUserRoles().add(userRole);
        }
    }

}
