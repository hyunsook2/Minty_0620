package com.Reboot.Minty.community.controller;

import com.Reboot.Minty.community.entity.Community;
import com.Reboot.Minty.community.entity.CommunityLike;
import com.Reboot.Minty.community.repository.CommunityLikeRepository;
import com.Reboot.Minty.community.repository.CommunityRepository;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class CommunityLikeController {

    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final CommunityLikeRepository communityLikeRepository;

    @Autowired
    public CommunityLikeController(UserRepository userRepository, CommunityRepository communityRepository, CommunityLikeRepository communityLikeRepository) {
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
        this.communityLikeRepository = communityLikeRepository;
    }

    @PutMapping("/likes/{communityId}")
    @ResponseBody
    public ResponseEntity<?> toggleLike(@PathVariable("communityId") Long communityId, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        Community community = communityRepository.findById(communityId).orElseThrow(EntityNotFoundException::new);

        Optional<CommunityLike> likeOptional = communityLikeRepository.findByUserAndCommunity(user, community);

        boolean isLiked;

        if (likeOptional.isPresent()) {
            CommunityLike communityLike = likeOptional.get();
            communityLike.setCheckStatus(!communityLike.isCheckStatus());
            communityLikeRepository.save(communityLike);
            isLiked = communityLike.isCheckStatus();

            if (communityLike.isCheckStatus()) {
                community.setInteresting(community.getInteresting() + 1);
            } else {
                community.setInteresting(community.getInteresting() - 1);
            }
        } else {
            CommunityLike newLike = new CommunityLike();
            newLike.setUser(user);
            newLike.setCommunity(community);
            newLike.setCheckStatus(true);
            communityLikeRepository.save(newLike);

            community.setInteresting(community.getInteresting() + 1);

            isLiked = true;
        }

        communityRepository.save(community);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("isLiked", isLiked);
        responseBody.put("likesCount", community.getInteresting());

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
