package com.phatpl.learnvocabulary.services;

import com.phatpl.learnvocabulary.dtos.request.SaveWordRequest;
import com.phatpl.learnvocabulary.dtos.response.GroupResponse;
import com.phatpl.learnvocabulary.dtos.response.GroupWordResponse;
import com.phatpl.learnvocabulary.dtos.response.WordResponse;
import com.phatpl.learnvocabulary.exceptions.UnauthorizationException;
import com.phatpl.learnvocabulary.filters.BaseFilter;
import com.phatpl.learnvocabulary.mappers.GroupWordResponseMapper;
import com.phatpl.learnvocabulary.models.Group;
import com.phatpl.learnvocabulary.models.GroupWord;
import com.phatpl.learnvocabulary.models.UserGroup;
import com.phatpl.learnvocabulary.repositories.GroupWordRepository;
import com.phatpl.learnvocabulary.repositories.UserGroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class GroupWordService extends BaseService<GroupWord, GroupWordResponse, BaseFilter, Integer> {
    GroupWordRepository groupWordRepository;
    GroupWordResponseMapper groupWordResponseMapper;
    WordService wordService;
    GroupService groupService;
    UserService userService;
    UserGroupRepository userGroupRepository;

    @Autowired
    public GroupWordService(GroupWordRepository groupWordRepository, GroupWordResponseMapper groupWordResponseMapper, WordService wordService, UserGroupService userGroupService, GroupService groupService, UserService userService, UserGroupRepository userGroupRepository) {
        super(groupWordResponseMapper, groupWordRepository);
        this.groupWordRepository = groupWordRepository;
        this.groupWordResponseMapper = groupWordResponseMapper;
        this.wordService = wordService;
        this.userGroupRepository = userGroupRepository;
        this.groupService = groupService;
        this.userService = userService;
    }

    public Boolean saveIntoGroup(SaveWordRequest request, JwtAuthenticationToken auth) {
        var userId = extractUserId(auth);
        var word = wordService.findById(request.getWordId());
        var userGroup = userGroupRepository.findByUserIdAndGroupId(userId, request.getGroupId()).orElseThrow(
                EntityNotFoundException::new
        );

        var group = userGroup.getGroup();

        if (userGroup.getIsOwner()) {
            var groupWord = new GroupWord(word, group);
            persistEntity(groupWord);
            return true;
        } else {
            throw new UnauthorizationException();
        }
    }

    public List<WordResponse> getWordsOfGroup(Integer groupId, JwtAuthenticationToken auth) {
        Integer userId = extractUserId(auth);
        Group group = groupService.findById(groupId);
        if (!group.getIsPrivate() || groupService.isOwner(userId, groupId)) {
            var words = new ArrayList<WordResponse>();
            groupWordRepository.findByGroupId(groupId).forEach(
                    (e) -> words.add(wordService.getWordResponseMapper().toDTO(e.getWord()))
            );
            return words;
        } else {
            throw new UnauthorizationException();
        }
    }

    public GroupResponse clone(Integer groupId, JwtAuthenticationToken auth) {
        var user = userService.findById(extractUserId(auth));
        var oldGroup = groupService.findById(groupId);

        if (oldGroup.getIsPrivate() && !groupService.isOwner(user.getId(), groupId)) {
            throw new UnauthorizationException();
        }

        var words = oldGroup.getGroupWords();
        var newGroup = Group.builder()
                .name(oldGroup.getName())
                .isPrivate(true)
                .groupWords(new ArrayList<>())
                .build();
        var response = groupService.createDTO(newGroup);

        userGroupRepository.save(new UserGroup(true, user, newGroup));
        for (var word : words) {
            persistEntity(new GroupWord(
                    word.getWord(),
                    newGroup
            ));
        }

        return response;
    }
}
