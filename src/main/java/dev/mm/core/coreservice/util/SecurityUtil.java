package dev.mm.core.coreservice.util;

import dev.mm.core.coreservice.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import static dev.mm.core.coreservice.constants.Common.DEFAULT_LANGUAGE;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

public class SecurityUtil {

    public static String getUserLangIfPresentOrDefault() {
        UserDetailsImpl userDetails = getUserIfPresent();
        if (userDetails != null && isNotBlank(userDetails.getLanguage())) {
            return userDetails.getLanguage();
        }
        return DEFAULT_LANGUAGE;
    }

    public static UserDetailsImpl getUserIfPresent() {
        if (
            SecurityContextHolder.getContext() != null &&
            SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null &&
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsImpl
        ) {
            return  (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }
}
