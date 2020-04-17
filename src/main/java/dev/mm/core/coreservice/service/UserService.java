package dev.mm.core.coreservice.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import dev.mm.core.coreservice.dto.response.PageResponseDto;
import dev.mm.core.coreservice.dto.user.UserDto;
import dev.mm.core.coreservice.dto.user.UserPageRequestDto;
import dev.mm.core.coreservice.model.QUser;
import dev.mm.core.coreservice.model.User;
import dev.mm.core.coreservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Service
public class UserService {

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private UserRepository userRepository;

    public PageResponseDto usersPage(UserPageRequestDto userPageRequestDto) {

        String username = userPageRequestDto.getUsername();
        Set<Long> hasAnyRoleId = userPageRequestDto.getHasAnyRoleId();

        PageResponseDto pageResponseDto = paginationService.getPage(userPageRequestDto, jpaQuery -> {

            JPAQuery queryBase = jpaQuery.from(QUser.user);

            BooleanExpression booleanExpression = null;

            if (isNotEmpty(hasAnyRoleId)) {
                booleanExpression = QUser.user.userRoles.any().userId.in(hasAnyRoleId);
            }

            if (isNotBlank(username)) {
                BooleanExpression subExpression = QUser.user.username.likeIgnoreCase(username);
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
}
