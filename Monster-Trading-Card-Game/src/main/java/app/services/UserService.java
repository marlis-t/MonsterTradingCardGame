package app.services;

import app.models.UserModel;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.ArrayList;

public class UserService {
    @Setter(AccessLevel.PRIVATE)
    private ArrayList<UserModel> userData;

    public UserService(){
        setUserData(new ArrayList<UserModel>());
        userData.add(new UserModel(1, "User1", "1234pw", 10, 10, 2));
        userData.add(new UserModel(2, "User2", "5678pw", 15, 15, 2));
        userData.add(new UserModel(3, "User3", "91011pw", 20, 0, 0));
    }
    public UserModel getUserByID(int id){
        UserModel foundUserModel = userData.stream()
                .filter(userModel -> id == userModel.getUserID())
                .findAny()
                .orElse(null);

        return foundUserModel;
    }
    public ArrayList<UserModel> getAllUsersExceptSelf(int Uid){
        ArrayList<UserModel> foundUsers = new ArrayList<UserModel>();
        for(UserModel user : userData){
            if(user.getUserID() != Uid){
                foundUsers.add(user);
            }
        }
        return foundUsers;
    }
    public UserModel checkIfUserExists(String username, String password){
        for(UserModel user : userData){
            if(user.getUsername() == username && user.getPassword() == password){
                return user;
            }
        }
        return null;
    }
    public void registerUser(UserModel user){
        userData.add(user);
    }
    public void deleteUser(int id){
        userData.removeIf(userModel -> id == userModel.getUserID());
    }
    public void updateUser(int id, UserModel user){
        userData.removeIf(userModel -> id == userModel.getUserID());
        userData.add(user);
    }
}
