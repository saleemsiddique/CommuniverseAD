package com.example.communiverse.service;

import com.example.communiverse.domain.Community;
import com.example.communiverse.domain.User;
import com.example.communiverse.exception.CommunityNotFoundException;
import com.example.communiverse.repository.CommunityRepository;
import com.example.communiverse.repository.UserRepository;
import com.example.communiverse.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<User> getBannedUsers(Community community) {
        List<User> users = new ArrayList<>();
        for (Community.BannedUser bannedUser : community.getBanned()) {
            User user = userRepository.findById(bannedUser.getUser_id())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            users.add(user);
        }
        return users;
    }

    @Override
    public Community updateCommunity(String id, Community newCommunity) {
        Community community = communityRepository.findById(id).orElseThrow();
        if(!newCommunity.getPhoto().equalsIgnoreCase("")) {
            String photoUrl = blobStorageService.uploadPhoto(newCommunity.getPhoto(), IdGenerator.generateId() + "-community_" + newCommunity.getId() + "-image.jpg");
            newCommunity.setPhoto(photoUrl);
        }
        newCommunity.setId(community.getId());
        newCommunity.setFollowers(community.getFollowers());
        newCommunity.setUserCreator_id(community.getUserCreator_id());
        newCommunity.setPosts_id(community.getPosts_id());
        if (newCommunity.getDescription().equalsIgnoreCase("")){
            newCommunity.setDescription(community.getDescription());
        }
        if (newCommunity.getName().equalsIgnoreCase("")){
            newCommunity.setName(community.getName());
        }
        if (newCommunity.getPhoto().equalsIgnoreCase("")){
            newCommunity.setPhoto(community.getPhoto());
        }
        System.out.println(newCommunity);
        return communityRepository.save(newCommunity);
    }

    public Community unbanUser(String communityId, String userId) {
        // Obtener la comunidad por su ID
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Comunidad no encontrada"));

        // Verificar si la comunidad tiene usuarios baneados
        List<Community.BannedUser> bannedUsers = community.getBanned();
        if (bannedUsers != null && !bannedUsers.isEmpty()) {
            // Eliminar el usuario baneado con el userId proporcionado
            bannedUsers.removeIf(bannedUser -> bannedUser.getUser_id().equals(userId));
        }
        return communityRepository.save(community);
    }
}
