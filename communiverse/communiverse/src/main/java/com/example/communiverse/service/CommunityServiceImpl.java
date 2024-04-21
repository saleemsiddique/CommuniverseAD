package com.example.communiverse.service;

import com.example.communiverse.domain.Community;
import com.example.communiverse.domain.User;
import com.example.communiverse.repository.CommunityRepository;
import com.example.communiverse.repository.UserRepository;
import com.example.communiverse.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl implements CommunityService{

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlobStorageService blobStorageService;

    @Override
    public Optional<Community> findById(String id) {
        return communityRepository.findById(id);
    }

    @Override
    public List<Community> findTop5ByOrderByFollowersDesc() {
        return communityRepository.findTop5ByOrderByFollowersDesc();
    }

    public List<Community> getMyCommunities(List<String> communityIds) {
        return communityRepository.findAllByIdIn(communityIds);
    }

    @Override
    public Community createCommunity(Community community) {
        community.setId(IdGenerator.generateId());
        String photoBase64 = community.getPhoto();
        if(!community.getPhoto().equalsIgnoreCase("")) {
            String photoUrl = blobStorageService.uploadPhoto(photoBase64, IdGenerator.generateId() + "-community_" + community.getId() + "-image.jpg");
            community.setPhoto(photoUrl);
        }
        return communityRepository.save(community);
    }
    @Override
    public User deleteCommunity(Community community) {
        User user = userRepository.findById(community.getUserCreator_id()).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        user.getCreatedCommunities().remove(community.getId());
        communityRepository.deleteCommunityById(community.getId());
        return userRepository.save(user);
    }
}
