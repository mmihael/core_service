package dev.mm.core.coreservice.controller;

import dev.mm.core.coreservice.annotations.RequiredRoles;
import dev.mm.core.coreservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.mm.core.coreservice.constants.Roles.ADMIN;
import static dev.mm.core.coreservice.constants.Uris.API_GET_USERS_PAGE;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(API_GET_USERS_PAGE)
    @RequiredRoles({ADMIN})
    public ResponseEntity getUsersPage() {
        return ResponseEntity.ok(userService.usersPage());
    }
}
