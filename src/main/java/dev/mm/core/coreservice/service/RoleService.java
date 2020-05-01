package dev.mm.core.coreservice.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import dev.mm.core.coreservice.dto.response.PageResponseDto;
import dev.mm.core.coreservice.dto.role.RoleDto;
import dev.mm.core.coreservice.dto.role.RolePageRequestDto;
import dev.mm.core.coreservice.model.QRole;
import dev.mm.core.coreservice.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private PaginationService paginationService;

    public PageResponseDto getPage(RolePageRequestDto rolePageRequestDto) {

        Boolean organizationRole = rolePageRequestDto.getOrganizationRole();

        PageResponseDto pageResponseDto = paginationService.getPage(rolePageRequestDto, jpaQuery -> {

            jpaQuery.from(QRole.role);

            BooleanExpression expression = null;

            if (organizationRole != null) {
                expression = QRole.role.organizationRole.eq(organizationRole);
            }

            if (expression != null) {
                jpaQuery.where(expression);
            }

        });

        pageResponseDto.setContent(((List<Role>) pageResponseDto.getContent()).stream().map(RoleDto::new)
            .collect(Collectors.toList()));

        return pageResponseDto;
    }
}
