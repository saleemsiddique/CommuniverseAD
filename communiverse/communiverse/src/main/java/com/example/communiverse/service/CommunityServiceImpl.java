package com.example.communiverse.service;

import com.example.communiverse.domain.Community;
import com.example.communiverse.repository.CommunityRepository;
import com.example.communiverse.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommunityServiceImpl implements CommunityService{

    @Autowired
    private CommunityRepository communityRepository;

    @Override
    public List<Community> findTop5ByOrderByFollowersDesc() {
        return communityRepository.findTop5ByOrderByFollowersDesc();
    }

    @Override
    public Community createCommunity(Community community) {
        community.setId(IdGenerator.generateUserId());
        return communityRepository.save(community);
    }
}
