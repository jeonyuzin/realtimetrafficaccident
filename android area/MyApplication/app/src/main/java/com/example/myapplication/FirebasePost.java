package com.example.myapplication;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class FirebasePost {
    public String id;
    public String pw;
    public String phone;

    public FirebasePost(){
    }

    public FirebasePost(String id, String pw, String phone) {
        this.id = id;
        this.pw = pw;
        this.phone = phone;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("pw", pw);
        result.put("phone", phone);
        return result;
    }
}