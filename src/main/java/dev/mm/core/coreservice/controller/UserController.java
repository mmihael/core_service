package dev.mm.core.coreservice.controller;

import dev.mm.core.coreservice.annotations.RequiredRoles;
import dev.mm.core.coreservice.dto.user.CreateUpdateUserDto;
import dev.mm.core.coreservice.dto.user.UserPageRequestDto;
import dev.mm.core.coreservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static dev.mm.core.coreservice.constants.Roles.ADMIN_ID;
import static dev.mm.core.coreservice.constants.Uris.API_USER;
import static dev.mm.core.coreservice.constants.Uris.API_USER_ID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(API_USER)
    @RequiredRoles({ADMIN_ID})
    public ResponseEntity getUsersPage(UserPageRequestDto userPageRequestDto) {
        return ResponseEntity.ok(userService.usersPage(userPageRequestDto));
    }

    @GetMapping(API_USER_ID)
    @RequiredRoles({ADMIN_ID})
    public ResponseEntity getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUserDtoById(userId));
    }

    @PostMapping(API_USER)
    @RequiredRoles({ADMIN_ID})
    public ResponseEntity createUser(@RequestBody CreateUpdateUserDto createUpdateUserDto) {
        return ResponseEntity.ok(userService.validateAndCreateUser(createUpdateUserDto));
    }

    @PutMapping(API_USER_ID)
    @RequiredRoles({ADMIN_ID})
    public ResponseEntity updateUser(@PathVariable long userId, @RequestBody CreateUpdateUserDto createUpdateUserDto) {
        return ResponseEntity.ok(userService.validateAndUpdateUser(userId, createUpdateUserDto));
    }

    @DeleteMapping(API_USER_ID)
    @RequiredRoles({ADMIN_ID})
    public ResponseEntity deleteUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
