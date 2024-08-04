package com.caesar.entity.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class CaesarGroupReviewConfig {

    int taskId;
    String uuid;
    String reviewBatch;
    int reviewLevel;
    String reviewDesc;
    String reviewUsers;



    public List<UserMapper> getReviewUserList() {

        ArrayList<UserMapper> userList = new ArrayList<>();
        String[] users = this.reviewUsers.split(",");
        for(String user:users){
            String[] userTuple = user.split("\\|");
            userList.add(
                    new UserMapper(
                            Integer.valueOf(userTuple[0]),
                            String.valueOf(userTuple[1])
                    )
            );
        }
        return userList;
    }

    public String getReviewUserName(int userId) {
        List<UserMapper> userList = getReviewUserList();
        for(UserMapper user:userList){
            if(userId == user.getUserId()){
                return user.getUserName();
            }
        }
        return null;
    }

    public static class UserMapper{
        int userId;
        String userName;

        public UserMapper(int userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
