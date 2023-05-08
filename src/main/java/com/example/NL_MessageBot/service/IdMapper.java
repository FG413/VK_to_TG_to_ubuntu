package com.example.NL_MessageBot.service;

import com.vk.api.sdk.client.actors.UserActor;

import java.util.HashMap;
import java.util.Map;

public  class IdMapper {

    public static Map<Long,VKAdapter> adapter=new HashMap<>();
    public static Map<Long,Integer> scenario = new HashMap<>();
    public static Map<Long,Integer> idToId = new HashMap<>();

    public static int getScenario(long id){
        return scenario.get(id);
    }
    public static int getVkId(long id){
        return adapter.get(id).getId();
    }
    public static String getToken(long id){
        return adapter.get(id).getActor().getAccessToken();
    }
    public static UserActor getActor(long id){
        return adapter.get(id).getActor();
    }

    public static void setNewId(long id){
        adapter.put(id,new VKAdapter(0,"vk.q"));
        scenario.put(id,0);
        idToId.put(id,0);
    }
    public static void setNewVkId(long id, int vkId){
        adapter.put(id,new VKAdapter(vkId,adapter.get(id).getToken()));
        idToId.put(id,vkId);
    }
    public static void setNewScenario(long id, int plan){
        scenario.put(id,plan);
    }
    public static void setNewToken(long id, String token){
        adapter.put(id, new VKAdapter(idToId.get(id),token));
    }
}
