package com.example.nlmessagebot.service;

import java.util.List;
import java.util.stream.Collectors;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;

public class VkData {
    final public static VkApiClient vk = new VkApiClient(new HttpTransportClient());
    static int k = 0;
    static String history = "";

    public static String dataReader(UserActor actor) throws ClientException, ApiException {

        List<Integer> conId = vk.messages().
                getConversations(actor).
                execute().
                getItems().
                stream().
                map(ConversationWithMessage::getConversation).
                map(Conversation::getPeer).
                map(ConversationPeer::getId).
                toList();
        List<Integer> ConUnread = vk.messages().
                getConversations(actor).
                execute().
                getItems().
                stream().
                map(ConversationWithMessage::getConversation).
                map(Conversation::getUnreadCount).
                toList();
        for (int count = 0; count < 20; count++) {
            if (ConUnread.get(count) != null) {
                history = history + vk.messages().
                        getHistory(actor).
                        userId(conId.get(count)).
                        count(ConUnread.get(count)).
                        execute().
                        getItems().
                        stream().
                        map(Message::getText).
                        toList();
            }
        }
        String subHistory = history;
        history = "";
        return subHistory;
    }
}
