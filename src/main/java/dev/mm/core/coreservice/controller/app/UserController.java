package dev.mm.core.coreservice.controller.app;

import dev.mm.core.coreservice.dto.user.CreateUpdateUserDto;
import dev.mm.core.coreservice.dto.user.UserPageRequestDto;
import dev.mm.core.coreservice.security.UserDetailsImpl;
import dev.mm.core.coreservice.service.AuthorizationService;
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

import static dev.mm.core.coreservice.constants.Uris.API_USER;
import static dev.mm.core.coreservice.constants.Uris.API_USER_ID;

@RestController
public class UserController {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UserService userService;

    @GetMapping(API_USER)
    public ResponseEntity getUsersPage(
        UserPageRequestDto userPageRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(userService.usersPage(userPageRequestDto));
    }

    @GetMapping(API_USER_ID)
    public ResponseEntity getUser(@PathVariable long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(userService.getUserDtoById(userId));
    }

    @PostMapping(API_USER)
    public ResponseEntity createUser(
        @RequestBody CreateUpdateUserDto createUpdateUserDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(userService.validateAndCreateUser(createUpdateUserDto));
    }

    @PutMapping(API_USER_ID)
    public ResponseEntity updateUser(
        @PathVariable long userId,
        @RequestBody CreateUpdateUserDto createUpdateUserDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(userService.validateAndUpdateUser(userId, createUpdateUserDto));
    }

    @DeleteMapping(API_USER_ID)
    public ResponseEntity deleteUser(@PathVariable long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
