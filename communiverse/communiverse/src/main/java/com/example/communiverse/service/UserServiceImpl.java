package com.example.communiverse.service;

import com.example.communiverse.domain.Community;
import com.example.communiverse.domain.User;
import com.example.communiverse.exception.AccountViaGoogleException;
import com.example.communiverse.repository.CommunityRepository;
import com.example.communiverse.repository.UserRepository;
import com.example.communiverse.utils.EmailUtil;
import com.example.communiverse.utils.IdGenerator;
import com.example.communiverse.utils.RandomPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Override
    public User addUser(User user) {
        user.setId(IdGenerator.generateId());
        return userRepository.save(user);
    }

    public List<String> getAllCommunityIds(User user) {
        List<String> allCommunityIds = new ArrayList<>();

        // Add all community IDs from createdCommunities
        allCommunityIds.addAll(user.getCreatedCommunities());

        // Add all community IDs from moderatedCommunities
        allCommunityIds.addAll(user.getModeratedCommunities());

        // Add all community IDs from memberCommunities
        allCommunityIds.addAll(user.getMemberCommunities());

        return allCommunityIds;
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findByUsernameRegex(String usernamePattern) {
        return userRepository.findByUsernameRegex(usernamePattern);
    }

    @Override
    public User follow(User followingUser, User followedUser) {
        if (followingUser.getFollowed_id().contains(followedUser.getId()) && followedUser.getFollowers_id().contains(followingUser.getId())) {
            // Si ya se siguen
            followingUser.getFollowed_id().remove(followedUser.getId());
            followedUser.getFollowers_id().remove(followingUser.getId());
        } else {
            // Si no se siguen
            followingUser.getFollowed_id().add(followedUser.getId());
            followedUser.getFollowers_id().add(followingUser.getId());
        }
        userRepository.save(followingUser);
        return userRepository.save(followedUser);
    }


    @Override
    public User joinCommunity(Community community, User user) {
        // Verificar si el usuario ya está en la comunidad como creador, moderador o miembro
        if (user.getCreatedCommunities().remove(community.getId()) ||
                user.getModeratedCommunities().remove(community.getId()) ||
                user.getMemberCommunities().remove(community.getId())) {
            community.setFollowers(community.getFollowers() - 1);
        } else {
            // Verificar si el usuario está en la lista de usuarios prohibidos
            if (isUserBanned(community, user)) {
                // Permitir unirse a la comunidad si la prohibición ha caducado
                if (isBanExpired(community, user)) {
                    removeBannedUser(community, user); // Eliminar usuario prohibido
                    // Recuperar la instancia actualizada de Community
                    community = communityRepository.findById(community.getId()).orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));
                } else {
                    throw new RuntimeException("El usuario está prohibido en esta comunidad.");
                }
            }
            user.getMemberCommunities().add(community.getId());
            community.setFollowers(community.getFollowers() + 1);
        }

        // Guardar los cambios en la comunidad y en el usuario
        communityRepository.save(community);
        return userRepository.save(user);
    }


    @Override
    public List<User> findByMemberCommunitiesContaining(String communityId) {
        return userRepository.findByMemberCommunitiesContainingOrModeratedCommunitiesContainingOrCreatedCommunitiesContaining(communityId, communityId, communityId);
    }

    public List<User> removeUserFromCommunity(String userId, String communityId, int daysUntilBan) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Community community = communityRepository.findById(communityId).orElseThrow(() -> new IllegalArgumentException("Comunidad no encontrada"));
        if (user.getCreatedCommunities().remove(communityId) || user.getModeratedCommunities().remove(communityId) || user.getMemberCommunities().remove(communityId)) {
            community.setFollowers(community.getFollowers() - 1);
            // Obtener la fecha actual
            LocalDate currentDate = LocalDate.now();
            // Obtener la hora actual
            LocalTime currentTime = LocalTime.now();
            // Crear la fecha y hora exactas 24 horas después
            LocalDateTime untilDateTime = LocalDateTime.of(currentDate, currentTime).plusDays(daysUntilBan);
            Community.BannedUser bannedUser = new Community.BannedUser(userId, untilDateTime);
            if (community.getBanned() == null) {
                community.setBanned(new ArrayList<>());
            }
            Optional<User> userOp = userRepository.findById(bannedUser.getUser_id());
            if (userOp.isPresent()) {
                User userBanned = userOp.get();
                user.getUserStats().decreasePoints(4);
                userRepository.save(userBanned);
            }
            community.getBanned().add(bannedUser);
            userRepository.save(user);
            communityRepository.save(community);
            return findByMemberCommunitiesContaining(communityId);
        } else {
            throw new IllegalArgumentException("Usuario no encontrado en la comunidad");
        }
    }

    @Override
    public List<User> promote(String userId, String communityId, String idCreator) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Community community = communityRepository.findById(communityId).orElseThrow(() -> new IllegalArgumentException("Comunidad no encontrado"));

        User creatorUser = userRepository.findById(idCreator)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (user.getMemberCommunities().contains(communityId)) {
            user.getMemberCommunities().remove(communityId);
            user.getModeratedCommunities().add(communityId);
            user.getUserStats().increasePoints(5);
            userRepository.save(user);
            return findByMemberCommunitiesContaining(communityId);
        } else if (user.getModeratedCommunities().contains(communityId)) {
            user.getModeratedCommunities().remove(communityId);
            user.getCreatedCommunities().add(communityId);
            creatorUser.getCreatedCommunities().remove(communityId);
            creatorUser.getModeratedCommunities().add(communityId);
            userRepository.save(creatorUser);
            userRepository.save(user);
            return findByMemberCommunitiesContaining(communityId);
        }
        else {
            throw new IllegalArgumentException("Usuario no es miembro de la comunidad");
        }
    }
    @Override
    public List<User> demoteToMember(String userId, String communityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (user.getModeratedCommunities().contains(communityId)) {
            user.getModeratedCommunities().remove(communityId);
            user.getMemberCommunities().add(communityId);
            user.getUserStats().decreasePoints(4);
            userRepository.save(user);
            return findByMemberCommunitiesContaining(communityId);
        } else {
            throw new IllegalArgumentException("Usuario no es moderador de la comunidad");
        }
    }

    // Método para verificar si el usuario está en la lista de usuarios prohibidos
    private boolean isUserBanned(Community community, User user) {
        List<Community.BannedUser> bannedUsers = community.getBanned();
        if (bannedUsers != null) {
            for (Community.BannedUser bannedUser : bannedUsers) {
                if (bannedUser.getUser_id().equals(user.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Método para verificar si la prohibición ha caducado
    private boolean isBanExpired(Community community, User user) {
        List<Community.BannedUser> bannedUsers = community.getBanned();
        if (bannedUsers != null) {
            for (Community.BannedUser bannedUser : bannedUsers) {
                if (bannedUser.getUser_id().equals(user.getId())) {
                    // Obtener la fecha de caducidad de la prohibición
                    LocalDateTime untilDate = bannedUser.getUntil();
                    // Obtener la fecha y hora actuales
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    // Comprobar si la fecha y hora actuales son posteriores a la fecha de caducidad de la prohibición
                    return currentDateTime.isAfter(untilDate);
                }
            }
        }
        // Si el usuario no está en la lista de prohibidos, se considera que la prohibición ha caducado
        return true;
    }


    // Método para eliminar un usuario prohibido de la lista
    private void removeBannedUser(Community community, User user) {
        List<Community.BannedUser> bannedUsers = community.getBanned();
        if (bannedUsers != null) {
            for (Iterator<Community.BannedUser> iterator = bannedUsers.iterator(); iterator.hasNext(); ) {
                Community.BannedUser bannedUser = iterator.next();
                if (bannedUser.getUser_id().equals(user.getId())) {
                    iterator.remove();
                    break;
                }
            }
            community.setBanned(bannedUsers); // Actualizar la lista de usuarios prohibidos en la comunidad
            communityRepository.save(community); // Guardar la comunidad actualizada en la base de datos
        }
    }

    public String recuperatePassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = new User();
        if (userOptional.isPresent()){
            user = userOptional.get();
        } else {
            throw new RuntimeException("There is no email with this account.");
        }

        if (user.isGoogle()){
            throw new AccountViaGoogleException("Cannot recover password from an account registered via Google.");
        }

        // Generar una contraseña aleatoria
        String newPassword = RandomPassword.generateRandomPassword();

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        try {
            emailUtil.sendSetPasswordEmail(email, newPassword);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send set password email. Please try again.");
        }

        return "New password set successfully. Check your email for the new password.";
    }

}
