package com.karmanov.auth.service.user;

import com.karmanov.auth.dto.response.UserProfileInfo;
import com.karmanov.auth.dto.response.UserRooms;
import com.karmanov.auth.model.UserEntity;
import com.karmanov.auth.repo.UserRepo;
import com.karmanov.auth.utils.mapper.RoomMapper;
import com.karmanov.auth.utils.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{
    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    @Override
    public UserProfileInfo getOrCreateUser(String keycloakId) {
        if (userRepo.existsByKeycloakId(keycloakId)) {
            log.debug("User exist with keycloak id {}",keycloakId);
            return UserMapper.toUserProfileInfoDto(userRepo.findByKeycloakId(keycloakId));
        }
        UserEntity user = new UserEntity(keycloakId, Set.of(), Set.of());
        userRepo.save(user);

        log.debug("This user did not exist, it was created");
        return UserMapper.toUserProfileInfoDto(user);
    }

    @Transactional
    @Override
    public UserProfileInfo getUserInfo(UUID userId) {
        return UserMapper.toUserProfileInfoDto(getExistingUserOrThrow(userId));
    }

    @Transactional
    @Override
    public UserRooms getUserRooms(UUID userId) {
        UserEntity user = getExistingUserOrThrow(userId);
        return new UserRooms(
                RoomMapper.toListOfUserCreatedRoomDto(user.getCreatedRooms()),
                RoomMapper.toListOfUserJoinedRoomDto(user.getRooms()));
    }

    private UserEntity getExistingUserOrThrow(UUID userId) {
        if (userRepo.existsById(userId)) {
            log.debug("User exists with id {}", userId);
            return userRepo.getReferenceById(userId);
        } else {
            log.error("User with id {} doesn't exist", userId);
            throw new IllegalArgumentException("User with this id doesn't exist");
        }
    }

}
