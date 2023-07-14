package com.Reboot.Minty.community.controller;

import com.Reboot.Minty.community.constant.BoardStatus;
import com.Reboot.Minty.community.entity.Comments;
import com.Reboot.Minty.community.entity.Community;
import com.Reboot.Minty.community.entity.CommunityLike;
import com.Reboot.Minty.community.repository.CommunityRepository;
import com.Reboot.Minty.community.repository.CommunityLikeRepository;
import com.Reboot.Minty.community.service.CommentsService;
import com.Reboot.Minty.community.service.CommunityService;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.member.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Controller
public class CommunityController {

    private final CommunityService communityService;

    private final UserRepository userRepository;

    private final CommunityRepository communityRepository;

    private final CommentsService commentsService;

    private final UserService userService;

    private final CommunityLikeRepository communityLikeRepository;

    @Autowired
    public CommunityController(CommunityService communityService, UserRepository userRepository, CommunityRepository communityRepository, CommentsService commentsService, UserService userService, CommunityLikeRepository communityLikeRepository) {
        this.communityService = communityService;
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
        this.commentsService = commentsService;
        this.userService = userService;
        this.communityLikeRepository = communityLikeRepository;
    }

    @GetMapping("/communityDetail/{postId}")
    public String communityDetail(@PathVariable("postId") Long id, Model model, HttpServletRequest request) {
        Community community = communityRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");
        User writer = community.getUser();
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        community.setVisitCount(community.getVisitCount() + 1);
        communityRepository.save(community);

        String status;
        switch (community.getStatus().toString()) {
            case "GENERAL":
                status = "일반";
                break;
            case "NOTICE":
                status = "공지";
                break;
            case "AD":
                status = "광고";
                break;
            default:
                status = "일반";
                break;
        }

        List<Comments> comments = commentsService.getCommentsByCommunityId(community.getId());

        Optional<CommunityLike> communityLikeOpt = communityLikeRepository.findByCommunityAndUser(community, user);
        boolean isLiked = false;
        if (communityLikeOpt.isPresent()) {
            CommunityLike communityLike = communityLikeOpt.get();
            isLiked = communityLike.isCheckStatus();
        }

        System.out.println(isLiked);

        model.addAttribute("isLiked", isLiked);
        model.addAttribute("community", community);
        model.addAttribute("status", status);
        model.addAttribute("status", status);
        model.addAttribute("writer", writer);
        model.addAttribute("user", user);
        model.addAttribute("comments", comments);

        return "/community/communityDetail";
    }

    @PostMapping("/api/posts")
    public ResponseEntity<Community> addPost(@RequestBody Community community, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        community.setUser(user);

        BoardStatus boardStatus = community.getStatus();
        community.setStatus(boardStatus);

        Community savedCommunity = communityService.addPost(community);

        return ResponseEntity.ok(savedCommunity);
    }


    @GetMapping("/communityList")
    public String communityList(Model model) {
        List<Community> communities = communityRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("communities", communities);
        return "/community/communityList";
    }

    @PostMapping("/comments")
    public String addComments(HttpServletRequest request, @RequestParam Long communityId, @RequestParam String commentContent, @RequestParam(required = false) Long tagUserId) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        Community community = communityRepository.findById(communityId).orElseThrow(EntityNotFoundException::new);

        User tagUser = null;
        if (tagUserId != null) {
            tagUser = userRepository.findById(tagUserId).orElseThrow(EntityNotFoundException::new);
        }

        commentsService.addComments(community, user, commentContent, tagUser);

        return "redirect:/communityDetail/" + community.getId();
    }

    @GetMapping("/editPost/{postId}")
    public String showEditForm(@PathVariable("postId") Long id, Model model,HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        Community community = communityRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        User writer = community.getUser();

        model.addAttribute("community", community);
        model.addAttribute("writer", writer);
        return "/community/editPost";
    }

    @PostMapping("/editPost/{postId}")
    public String editPost(@PathVariable("postId") Long postId, @ModelAttribute("community") Community updatedCommunity) {
        Community existingCommunity = communityRepository.findById(postId).orElseThrow(EntityNotFoundException::new);

        // 수정된 정보 업데이트
        existingCommunity.setTitle(updatedCommunity.getTitle());
        existingCommunity.setContent(updatedCommunity.getContent());
        existingCommunity.setModifiedDate(new Timestamp(System.currentTimeMillis()));

        communityRepository.save(existingCommunity); // 수정된 커뮤니티 저장

        return "redirect:/communityDetail/" + postId;
    }

    @PostMapping("/deletePost/{postId}")
    public String deletePost(@PathVariable("postId") Long id) {
        communityService.deletePost(id);
        return "redirect:/communityList";
    }



}
