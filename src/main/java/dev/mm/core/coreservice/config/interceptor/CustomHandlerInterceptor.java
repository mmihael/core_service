package dev.mm.core.coreservice.config.interceptor;

import dev.mm.core.coreservice.annotations.RequiredRoles;
import dev.mm.core.coreservice.constants.Uris;
import dev.mm.core.coreservice.exception.ForbiddenException;
import dev.mm.core.coreservice.model.User;
import dev.mm.core.coreservice.repository.UserRepository;
import dev.mm.core.coreservice.repository.UserRoleRepository;
import dev.mm.core.coreservice.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CustomHandlerInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod = null;

        log.debug("Handler class: {}", handler.getClass());

        if (handler instanceof HandlerMethod) {
            handlerMethod = (HandlerMethod) handler;
        }

        if (handlerMethod != null && request.getRequestURI().startsWith(Uris.API)) {

            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            User user = userRepository
                    .findById(userDetails.getId())
                    .orElseThrow(() -> new ForbiddenException("Non existing user"));

            RequiredRoles requiredRoles = handlerMethod.getMethodAnnotation(RequiredRoles.class);

            if (requiredRoles != null && requiredRoles.value().length > 0) {

                Set<String> requiredRolesSet = new HashSet<>(Arrays.asList(requiredRoles.value()));

                if (userRoleRepository.countHowManyRolesUserHasFromSet(user.getId(), requiredRolesSet) != requiredRolesSet.size()) {
                    throw new ForbiddenException("Missing some of the required roles: " + requiredRolesSet);
                }
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }
}