package com.karmanov.auth.authservice.utils.mapper;

import com.karmanov.auth.authservice.dto.response.UserProfileInfo;
import com.karmanov.auth.authservice.model.UserEntity;

public class UserMapper {

    public static UserProfileInfo toUserProfileInfoDto(UserEntity user){
        return new UserProfileInfo(user.getKeycloakId(),RoomMapper.toListOfUserCreatedRoomDto(user.getCreatedRooms()),RoomMapper.toListOfUserJoinedRoomDto(user.getRooms()));
    }
}
