package dev.mm.core.coreservice.dto.organization;

import dev.mm.core.coreservice.dto.entity.IdAndTimestampsDto;
import dev.mm.core.coreservice.model.Organization;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationDto extends IdAndTimestampsDto {

    private String name;

    private boolean deleted;

    public OrganizationDto(Organization organization) {
        super(organization);
        name = organization.getName();
        deleted = organization.isDeleted();
    }

}
