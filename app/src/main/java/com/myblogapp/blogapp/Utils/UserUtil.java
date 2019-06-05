package com.myblogapp.blogapp.Utils;

import com.myblogapp.blogapp.Models.User;

/**
 * Created by piasatuito on 30/3/17.
 */

public class UserUtil {

    public static UserUtil instance;
    public User mLoggedUser;

    public synchronized static UserUtil getInstance() {
        if (instance == null)
            instance = new UserUtil();
        return instance;
    }

    public UserUtil(){
        //empty constructor
    }

    public void setLoggedUser(String name){
        mLoggedUser = RealmController.getInstance().getRealm().where(User.class).equalTo("name",name).findFirst();
    }

    public User getLoggedUser(){
        return mLoggedUser;
    }

    public void removeLoggedUser(){
        mLoggedUser = null;
    }


}