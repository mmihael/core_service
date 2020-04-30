package dev.mm.core.coreservice.controller.app;

import dev.mm.core.coreservice.dto.role.RolePageRequestDto;
import dev.mm.core.coreservice.security.UserDetailsImpl;
import dev.mm.core.coreservice.service.AuthorizationService;
import dev.mm.core.coreservice.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.mm.core.coreservice.constants.Uris.API_ROLE;

@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping(API_ROLE)
    public ResponseEntity rolePage(RolePageRequestDto rolePageRequestDto) {
        return ResponseEntity.ok(roleService.getPage(rolePageRequestDto));
    }
}
