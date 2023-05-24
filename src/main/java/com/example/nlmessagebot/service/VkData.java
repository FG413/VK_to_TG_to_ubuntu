package com.example.nlmessagebot.service;

import java.util.ArrayList;
import java.util.Collections;
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

    public static List<String> globalListOfText= new ArrayList<>();
    public static List<String> globalListOfName = new ArrayList<>();

    public static List<Integer> globalListOfDate = new ArrayList<>();

    public static void dataReader(UserActor actor) throws ClientException, ApiException {

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
                globalListOfText.addAll(vk.messages().
                        getHistory(actor).
                        userId(conId.get(count)).
                        count(ConUnread.get(count)).
                        execute().
                        getItems().
                        stream().
                        map(Message::getText).
                        toList());

                List<Integer> listOfId= vk.messages().
                        getHistory(actor).
                        userId(conId.get(count)).
                        count(ConUnread.get(count)).
                        execute().
                        getItems().
                        stream().
                        map(Message::getFromId).
                        toList();

                 globalListOfDate.addAll(vk.messages().
                         getHistory(actor).
                         userId(conId.get(count)).
                         count(ConUnread.get(count)).
                         execute().
                         getItems().
                         stream().
                         map(Message::getDate).toList());
                for(int count1 = 0; count1 < listOfId.size(); count1++){
                    globalListOfName.add(vk.users().get(actor).userIds(listOfId.get(count).toString()).execute().get(0).getFirstName() +
                           " " +
                            vk.users().get(actor).userIds(listOfId.get(count).toString()).execute().get(0).getLastName());

                }
            }
        }

    }
    public static void dataCleaner(){
        globalListOfName.clear();
        globalListOfText.clear();
    }
}
