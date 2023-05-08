package com.example.NL_MessageBot.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.queries.messages.MessagesGetHistoryQuery;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollServerQuery;

public class VkData {
    final public static VkApiClient vk = new VkApiClient(new HttpTransportClient());
    static int k=0;
    static String history="";
    public static String dataReader(UserActor actor) throws ClientException, ApiException {

        List<Integer> conId =vk.messages().getConversations(actor).execute().getItems().stream().map(ConversationWithMessage::getConversation).map(Conversation::getPeer).map(ConversationPeer::getId).collect(Collectors.toList());
        List<Integer> ConUnread= vk.messages().getConversations(actor).execute().getItems().stream().map(ConversationWithMessage::getConversation).map(Conversation::getUnreadCount).collect(Collectors.toList());
        for(int count = 0; count <20; count++) {
        if (ConUnread.get(count)!=null){
            history =history + vk.messages().getHistory(actor).userId(conId.get(count)).count(ConUnread.get(count)).execute().getItems().stream().map(Message::getText).collect(Collectors.toList()).toString();
        }
        }
        String subHistory=history;
        history="";
        return subHistory;
    }
}
