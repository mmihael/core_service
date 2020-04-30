package dev.mm.core.coreservice.controller.app;

import dev.mm.core.coreservice.dto.response.AppConfigurationResponse;
import dev.mm.core.coreservice.security.UserDetailsImpl;
import dev.mm.core.coreservice.service.AppConfigurationService;
import dev.mm.core.coreservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.mm.core.coreservice.constants.Uris.API_APP_CONFIGURATION;

@RestController
public class AppConfigurationController {

    @Autowired
    private AppConfigurationService appConfigurationService;

    @GetMapping(API_APP_CONFIGURATION)
    public ResponseEntity getAppConfiguration(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(appConfigurationService.getAppConfigurationFor(userDetails));
    }

}
