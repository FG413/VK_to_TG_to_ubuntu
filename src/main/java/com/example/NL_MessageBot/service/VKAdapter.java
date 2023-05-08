package com.example.NL_MessageBot.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;

public class VKAdapter {

    public UserActor getActor() {
        return actor;
    }

    private UserActor actor;

    public int getId() {
        return id;
    }

    private int id;


    public String getToken() {
        return token;
    }

    private String token;

    public VKAdapter(int id,String token) {
        actor = new UserActor(id,token);
        this.id=id;
        this.token = token;
    }
}
