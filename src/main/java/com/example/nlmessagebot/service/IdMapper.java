package com.example.nlmessagebot.service;

import com.vk.api.sdk.client.actors.UserActor;

import java.util.HashMap;
import java.util.Map;

public class IdMapper {

    public static Map<Long, VKAdapter> idToAdapter = new HashMap<>();
    public static Map<Long, Integer> idToScenario = new HashMap<>();
    public static Map<Long, Integer> idToVkId = new HashMap<>();

    public static int getScenario(long id) {
        return idToScenario.get(id);
    }

    public static int getVkId(long id) {
        return idToAdapter.get(id).getId();
    }

    public static String getToken(long id) {
        return idToAdapter.get(id).getActor().getAccessToken();
    }

    public static UserActor getActor(long id) {
        return idToAdapter.get(id).getActor();
    }

    public static void setNewId(long id) {
        idToAdapter.put(id, new VKAdapter(0, "vk.q"));
        idToScenario.put(id, 0);
        idToVkId.put(id, 0);
    }

    public static void setNewVkId(long id, int vkId) {
        idToAdapter.put(id, new VKAdapter(vkId, idToAdapter.get(id).getToken()));
        idToVkId.put(id, vkId);
    }

    public static void setNewScenario(long id, int plan) {
        idToScenario.put(id, plan);
    }

    public static void setNewToken(long id, String token) {
        idToAdapter.put(id, new VKAdapter(idToVkId.get(id), token));
    }
}
