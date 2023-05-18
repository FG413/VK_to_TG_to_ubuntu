package com.example.nlmessagebot.service;

import com.vk.api.sdk.client.actors.UserActor;

public class VKAdapter {

    public UserActor getActor() {
        return actor;
    }

    private final UserActor actor;

    public int getId() {
        return id;
    }

    private final int id;


    public String getToken() {
        return token;
    }

    private final String token;

    public VKAdapter(int id,String token) {
        actor = new UserActor(id,token);
        this.id=id;
        this.token = token;
    }
}
