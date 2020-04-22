package dev.mm.core.coreservice.controller.organization;

import dev.mm.core.coreservice.dto.response.OrgAppConfigurationResponse;
import dev.mm.core.coreservice.security.UserDetailsImpl;
import dev.mm.core.coreservice.service.OrganizationService;
import dev.mm.core.coreservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static dev.mm.core.coreservice.constants.Uris.API_ORGANIZATION_APP_CONFIGURATION;

@RestController
public class OrgAppConfigurationController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @GetMapping(API_ORGANIZATION_APP_CONFIGURATION)
    public ResponseEntity getAppConfiguration(
        @PathVariable long organizationId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(new OrgAppConfigurationResponse(
            userService.getUserFromOrganizationById(organizationId, userDetails.getId()),
            organizationService.getOrganizationDtoById(organizationId)
        ));
    }

}
