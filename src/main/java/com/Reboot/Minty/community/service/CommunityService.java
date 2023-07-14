package com.Reboot.Minty.community.service;

import com.Reboot.Minty.community.entity.Community;
import com.Reboot.Minty.community.repository.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;

    @Autowired
    public CommunityService(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    public Community addPost(Community community) {
        return communityRepository.save(community);
    }

    public void deletePost(Long id) {
        communityRepository.deleteById(id);
    }

}
