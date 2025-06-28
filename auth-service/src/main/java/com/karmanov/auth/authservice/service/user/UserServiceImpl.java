package com.karmanov.auth.authservice.service.user;

import com.karmanov.auth.authservice.dto.response.UserProfileInfo;
import com.karmanov.auth.authservice.model.UserEntity;
import com.karmanov.auth.authservice.repo.UserRepo;
import com.karmanov.auth.authservice.utils.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    @Override
    public UserProfileInfo getOrCreateUser(String keycloakId) {
        if (userRepo.existsByKeycloakId(keycloakId)) {
            return UserMapper.toUserProfileInfoDto(userRepo.findByKeycloakId(keycloakId));
        }
        UserEntity user = new UserEntity(keycloakId, Set.of(), Set.of());
        userRepo.save(user);
        return UserMapper.toUserProfileInfoDto(user);
    }

}
