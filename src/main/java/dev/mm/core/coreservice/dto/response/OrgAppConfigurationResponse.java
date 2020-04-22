package dev.mm.core.coreservice.dto.response;

import dev.mm.core.coreservice.dto.organization.OrganizationDto;
import dev.mm.core.coreservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrgAppConfigurationResponse {

    private UserDto userDto;
    private OrganizationDto organizationDto;

}
