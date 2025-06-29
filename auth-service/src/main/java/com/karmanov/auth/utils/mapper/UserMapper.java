package com.karmanov.auth.utils.mapper;

import com.karmanov.auth.dto.response.UserProfileInfo;
import com.karmanov.auth.model.UserEntity;

public class UserMapper {

    public static UserProfileInfo toUserProfileInfoDto(UserEntity user){
        return new UserProfileInfo(user.getId(),RoomMapper.toListOfUserCreatedRoomDto(user.getCreatedRooms()),RoomMapper.toListOfUserJoinedRoomDto(user.getRooms()));
    }
}
