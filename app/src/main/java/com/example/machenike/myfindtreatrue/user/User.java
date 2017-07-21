package com.example.machenike.myfindtreatrue.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MACHENIKE on 2017/7/13.
 */

public class User {
    /**
     * UserName : qjd
     * Password : 654321
     */

    @SerializedName("UserName")
    private String userName;

    @SerializedName("Password")
    private String password;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
