package dev.mm.core.coreservice.dto.role;

import dev.mm.core.coreservice.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto {

    private long id;
    private String name;

    public RoleDto(Role role) {
        id = role.getId();
        name = role.getName();
    }
}
