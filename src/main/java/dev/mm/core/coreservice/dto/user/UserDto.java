package dev.mm.core.coreservice.dto.user;

import dev.mm.core.coreservice.dto.entity.IdAndTimestampsDto;
import dev.mm.core.coreservice.dto.userRole.UserRoleDto;
import dev.mm.core.coreservice.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Getter
@Setter
public class UserDto extends IdAndTimestampsDto {

    private String username;
    private boolean enabled;
    private boolean deleted;
    private List<UserRoleDto> userRoles;

    public UserDto(User user) {
        this(user, true);
    }

    public UserDto(User user, boolean includeRelations) {
        super(user);
        username = user.getUsername();
        enabled = user.isEnabled();
        deleted = user.isDeleted();
        if (includeRelations) {
            if (isNotEmpty(user.getUserRoles())) {
                userRoles = user.getUserRoles().stream().map(UserRoleDto::new).collect(Collectors.toList());
            }
        }
    }

}