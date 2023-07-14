package com.Reboot.Minty.community.repository;
import com.Reboot.Minty.community.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> findByCommunityId(Long communityId);
}
