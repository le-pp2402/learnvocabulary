package com.phatpl.learnvocabulary.controllers;

import com.phatpl.learnvocabulary.dtos.request.CreateGroupRequest;
import com.phatpl.learnvocabulary.dtos.request.UpdateGroupRequest;
import com.phatpl.learnvocabulary.dtos.response.GroupResponse;
import com.phatpl.learnvocabulary.exceptions.UnauthorizationException;
import com.phatpl.learnvocabulary.filters.GroupFilter;
import com.phatpl.learnvocabulary.models.Group;
import com.phatpl.learnvocabulary.services.GroupService;
import com.phatpl.learnvocabulary.services.GroupWordService;
import com.phatpl.learnvocabulary.services.UserGroupService;
import com.phatpl.learnvocabulary.utils.BuildResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/groups")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupController extends BaseController<Group, GroupResponse, GroupFilter, Integer> {
    GroupService groupService;
    UserGroupService userGroupService;
    GroupWordService groupWordService;

    @Autowired
    public GroupController(GroupService groupService, UserGroupService userGroupService, GroupWordService groupWordService) {
        super(groupService);
        this.groupService = groupService;
        this.userGroupService = userGroupService;
        this.groupWordService = groupWordService;
    }

    @Override
    @GetMapping
    public ResponseEntity findAll(GroupFilter groupFilter) {
        var groups = groupService.findByFilter(groupFilter);
        if (groups == null || groups.isEmpty()) {
            return BuildResponse.notFound(groups);
        } else {
            return BuildResponse.ok(groups);
        }
    }

    @PostMapping("/me")
    public ResponseEntity createGroup(@RequestBody @Valid CreateGroupRequest createGroupRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            var error = bindingResult.getFieldErrors().get(0);
            return BuildResponse.badRequest(error.getDefaultMessage());
        }
        try {
            return BuildResponse.created(userGroupService.create(createGroupRequest));
        } catch (RuntimeException e) {
            return BuildResponse.badRequest(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity findGroupByUser() {
        try {
            return BuildResponse.ok(groupService.findGroupByUser());
        } catch (RuntimeException e) {
            return BuildResponse.badRequest(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateGroupInfo(@PathVariable("id") Integer groupId, @RequestBody UpdateGroupRequest updateGroupRequest) {
        try {
            return BuildResponse.ok(groupService.updateGroupInfo(groupId, updateGroupRequest));
        } catch (UnauthorizationException e) {
            return BuildResponse.unauthorized(e.getMessage());
        } catch (RuntimeException e) {
            return BuildResponse.badRequest(e.getMessage());
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") Integer groupId) {
        try {
            return BuildResponse.ok(groupWordService.getWordsOfGroup(groupId));
        } catch (UnauthorizationException e) {
            return BuildResponse.unauthorized(e.getMessage());
        } catch (RuntimeException e) {
            return BuildResponse.badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer groupId) {
        try {
            userGroupService.delete(groupId);
            return BuildResponse.ok("deleted group id = " + groupId);
        } catch (Exception e) {
            return BuildResponse.forbidden(e.getMessage());
        }
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity followGroup(@NotNull @PathVariable("id") Integer groupId) {
        try {
            return BuildResponse.ok(userGroupService.follow(groupId));
        } catch (UnauthorizationException e) {
            return BuildResponse.unauthorized(e.getMessage());
        } catch (RuntimeException e) {
            return BuildResponse.badRequest(e.getMessage());
        }
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity cloneGroup(@NotNull @PathVariable("id") Integer groupId) {
        try {
            return BuildResponse.ok(groupWordService.clone(groupId));
        } catch (UnauthorizationException e) {
            return BuildResponse.unauthorized(e.getMessage());
        } catch (RuntimeException e) {
            return BuildResponse.badRequest(e.getMessage());
        }
    }
}