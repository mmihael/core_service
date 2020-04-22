package dev.mm.core.coreservice.dto.organization;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AssignUsersToOrganizationRequestDto {
    private Long userId;
    private Set<Long> roleIds;
}
