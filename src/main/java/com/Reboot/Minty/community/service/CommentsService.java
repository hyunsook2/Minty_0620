package com.Reboot.Minty.community.service;

import com.Reboot.Minty.community.entity.Comments;
import com.Reboot.Minty.community.entity.Community;
import com.Reboot.Minty.community.repository.CommentsRepository;
import com.Reboot.Minty.member.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class CommentsService {

    @Autowired
    private final CommentsRepository commentsRepository;

    public CommentsService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    public Comments addComments(Community community, User user, String commentContent, User tagUser) {
        Comments newComment = new Comments();

        newComment.setCommunity(community);
        newComment.setUser(user);
        newComment.setContent(commentContent);
        newComment.setTagUser(tagUser);

        newComment.setCreatedDate(Timestamp.from(Instant.now()));
        newComment.setModifiedDate(Timestamp.from(Instant.now()));

        newComment.setInteresting(0);

        return commentsRepository.save(newComment);
    }

    public List<Comments> getCommentsByCommunityId(Long communityId) {
        List<Comments> comments = commentsRepository.findByCommunityId(communityId);
        comments.sort(Comparator.comparing(Comments::getCreatedDate).reversed());
        return comments;
    }


}
