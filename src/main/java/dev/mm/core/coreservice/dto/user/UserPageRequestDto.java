package dev.mm.core.coreservice.dto.user;

import dev.mm.core.coreservice.dto.request.PageRequestDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString(callSuper = true)
public class UserPageRequestDto extends PageRequestDto {

    private String username;
    private Set<Long> hasAnyRoleId;

}
