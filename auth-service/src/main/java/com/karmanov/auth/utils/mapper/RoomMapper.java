package com.karmanov.auth.utils.mapper;

import com.karmanov.auth.dto.response.UserCreatedRoom;
import com.karmanov.auth.dto.response.UserJoinedRoom;
import com.karmanov.auth.model.RoomEntity;

import java.util.List;
import java.util.Set;

public class RoomMapper {

    public static UserCreatedRoom toUserCreatedRoomDto(RoomEntity room){
        return new UserCreatedRoom(room.getId(),room.getName());
    }

    public static List<UserCreatedRoom> toListOfUserCreatedRoomDto(Set<RoomEntity> rooms){
        return rooms.stream().map(RoomMapper::toUserCreatedRoomDto).toList();
    }

    public static UserJoinedRoom toUserJoinedRoomDto(RoomEntity room){
        return new UserJoinedRoom(room.getId(),room.getName());
    }

    public static List<UserJoinedRoom> toListOfUserJoinedRoomDto(Set<RoomEntity> rooms){
        return rooms.stream().map(RoomMapper::toUserJoinedRoomDto).toList();
    }
}
