package com.phatpl.learnvocabulary.controllers;

import com.phatpl.learnvocabulary.repositories.graph.FriendshipRepo;
import com.phatpl.learnvocabulary.repositories.graph.UserRepo;
import com.phatpl.learnvocabulary.utils.BuildResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    //    private final UserRepo repository;
    private final UserRepo userRepo;
    private final FriendshipRepo friendshipRepo;

    @Autowired
    public TestController(UserRepo userRepo, FriendshipRepo friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }

    @GetMapping
    public ResponseEntity test() {
        try {
            return BuildResponse.ok(userRepo.getFriends(10L));
        } catch (Exception e) {
            return BuildResponse.badRequest(e.getMessage());
        }
    }

}
