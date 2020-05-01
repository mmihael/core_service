package dev.mm.core.coreservice.controller.app;

import dev.mm.core.coreservice.dto.organization.AssignUsersToOrganizationRequestDto;
import dev.mm.core.coreservice.dto.organization.CreateOrganizationDto;
import dev.mm.core.coreservice.dto.organization.OrganizationPageRequestDto;
import dev.mm.core.coreservice.security.UserDetailsImpl;
import dev.mm.core.coreservice.service.AuthorizationService;
import dev.mm.core.coreservice.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static dev.mm.core.coreservice.constants.Uris.API_ORGANIZATION;
import static dev.mm.core.coreservice.constants.Uris.API_ORGANIZATION_ID;
import static dev.mm.core.coreservice.constants.Uris.API_ORGANIZATION_ID_ASSIGN_USER;

@RestController
public class OrganizationController {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private OrganizationService organizationService;

    @GetMapping(API_ORGANIZATION)
    public ResponseEntity getPage(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        OrganizationPageRequestDto organizationPageRequestDto
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(organizationService.getPage(organizationPageRequestDto));
    }

    @GetMapping(API_ORGANIZATION_ID)
    public ResponseEntity getOrganization(
        @PathVariable long organizationId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(organizationService.getOrganizationDtoById(organizationId));
    }

    @PostMapping(API_ORGANIZATION)
    public ResponseEntity createOrganization(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody CreateOrganizationDto createOrganizationDto
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(organizationService.validateAndCreateOrganization(createOrganizationDto));
    }

    @PutMapping(API_ORGANIZATION_ID)
    public ResponseEntity updateOrganization(
        @PathVariable long organizationId,
        @RequestBody CreateOrganizationDto createOrganizationDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(organizationService.validateAndUpdateOrganization(organizationId, createOrganizationDto));
    }

    @DeleteMapping(API_ORGANIZATION_ID)
    public ResponseEntity deleteOrganization(
        @PathVariable long organizationId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(organizationService.deleteOrganization(organizationId));
    }

    @PostMapping(API_ORGANIZATION_ID_ASSIGN_USER)
    public ResponseEntity assignUserToOrganization(
        @PathVariable long organizationId,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody AssignUsersToOrganizationRequestDto assignUsersToOrganizationRequestDto
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(
            organizationService.validateAndAssignUserToOrganization(organizationId, assignUsersToOrganizationRequestDto)
        );
    }

}
