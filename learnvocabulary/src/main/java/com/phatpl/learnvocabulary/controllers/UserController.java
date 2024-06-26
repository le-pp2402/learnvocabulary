package com.phatpl.learnvocabulary.controllers;

import com.phatpl.learnvocabulary.dtos.request.UpdatePasswordRequest;
import com.phatpl.learnvocabulary.dtos.response.UserResponse;
import com.phatpl.learnvocabulary.filters.UserFilter;
import com.phatpl.learnvocabulary.models.User;
import com.phatpl.learnvocabulary.services.UserService;
import com.phatpl.learnvocabulary.utils.BuildResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@Transactional
public class UserController extends BaseController<User, UserResponse, UserFilter, Integer> {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        super(userService);
        this.userService = userService;
    }

    @PutMapping("/me")
    public ResponseEntity updateUserInfo(@Valid @RequestBody UpdatePasswordRequest request) {
        try {
            JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return BuildResponse.ok(userService.updateUserInfo(
                    request.getOldPassword(), request.getNewPassword(), auth
            ));
        } catch (Exception e) {
            return BuildResponse.badRequest(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity getUserInfo() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            return BuildResponse.ok(userService.me(auth));
        } catch (Exception e) {
            return BuildResponse.unauthorized(e.getMessage());
        }
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAuthority(SCOPE_ADMIN)")
    public ResponseEntity findAll() {
        var users = userService.findAllDTO();
        return BuildResponse.ok(users);
    }

}
