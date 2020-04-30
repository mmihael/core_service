package dev.mm.core.coreservice.dto.role;

import dev.mm.core.coreservice.dto.request.PageRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePageRequestDto extends PageRequestDto {
    private Boolean organizationRole;
}
