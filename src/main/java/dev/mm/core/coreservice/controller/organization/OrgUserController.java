package dev.mm.core.coreservice.controller.organization;

import dev.mm.core.coreservice.dto.user.CreateUpdateUserDto;
import dev.mm.core.coreservice.dto.user.UserPageRequestDto;
import dev.mm.core.coreservice.security.AuthorizationService;
import dev.mm.core.coreservice.security.UserDetailsImpl;
import dev.mm.core.coreservice.service.UserService;
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

import static dev.mm.core.coreservice.constants.Uris.API_ORGANIZATION_USER;
import static dev.mm.core.coreservice.constants.Uris.API_ORGANIZATION_USER_ID;

@RestController
public class OrgUserController {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UserService userService;

    @GetMapping(API_ORGANIZATION_USER)
    public ResponseEntity getOrganizationUsersPage(
        @PathVariable long organizationId,
        UserPageRequestDto userPageRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        authorizationService.ensureUserIsOrganizationOwner(organizationId, userDetails);
        return ResponseEntity.ok(userService.usersPage(organizationId, userPageRequestDto));
    }

    @GetMapping(API_ORGANIZATION_USER_ID)
    public ResponseEntity getUserFromOrganization(
        @PathVariable long organizationId,
        @PathVariable long userId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        authorizationService.ensureUserIsOrganizationOwner(organizationId, userDetails);
        return ResponseEntity.ok(userService.getUserFromOrganizationById(organizationId, userId));
    }

    @PostMapping(API_ORGANIZATION_USER)
    public ResponseEntity createUserInOrganization(
        @PathVariable long organizationId,
        @RequestBody CreateUpdateUserDto createUpdateUserDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        authorizationService.ensureUserIsOrganizationOwner(organizationId, userDetails);
        return ResponseEntity.ok(userService.validateAndCreateUserInOrganization(createUpdateUserDto, organizationId));
    }

    @PutMapping(API_ORGANIZATION_USER_ID)
    public ResponseEntity updateUserInOrganization(
        @PathVariable long organizationId,
        @PathVariable long userId,
        @RequestBody CreateUpdateUserDto createUpdateUserDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        authorizationService.ensureUserIsOrganizationOwner(organizationId, userDetails);
        return ResponseEntity
            .ok(userService.validateAndUpdateUserInOrganization(userId, createUpdateUserDto, organizationId));
    }

    @DeleteMapping(API_ORGANIZATION_USER_ID)
    public ResponseEntity deleteUserInOrganization(
        @PathVariable long organizationId,
        @PathVariable long userId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        authorizationService.ensureUserIsOrganizationOwner(organizationId, userDetails);
        return ResponseEntity.ok(userService.deleteUserInOrganization(userId, organizationId));
    }

}
