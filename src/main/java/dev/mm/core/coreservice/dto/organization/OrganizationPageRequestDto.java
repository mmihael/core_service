package dev.mm.core.coreservice.dto.organization;

import dev.mm.core.coreservice.dto.request.PageRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationPageRequestDto extends PageRequestDto {

    private String name;

}
