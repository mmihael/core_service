package dev.mm.core.coreservice.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateUpdateUserDto {

    private String username;
    private String password;
    private Boolean enabled;
    private Set<Long> roleIds;

}
