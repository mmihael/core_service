package dev.mm.core.coreservice.dto.userRole;

import dev.mm.core.coreservice.dto.entity.IdAndTimestampsDto;
import dev.mm.core.coreservice.dto.role.RoleDto;
import dev.mm.core.coreservice.model.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleDto extends IdAndTimestampsDto {

    private long userId;
    private long roleId;
    private RoleDto roleDto;

    public UserRoleDto(UserRole userRole) {
        super(userRole);
        this.userId = userRole.getUserId();
        this.roleId = userRole.getRoleId();
        this.roleDto = new RoleDto(userRole.getRole());
    }
}
