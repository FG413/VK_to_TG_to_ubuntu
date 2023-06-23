package com.example.nlmessagebot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VkData {
    final public static VkApiClient vk = new VkApiClient(new HttpTransportClient());
    public static List<MessageData> sumOfList = new ArrayList<>();

    public static void dataReader(UserActor actor) throws ApiException {
        try {
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

                    vk.messages().
                            getHistory(actor).
                            userId(conId.get(count)).
                            count(ConUnread.get(count)).
                            execute().
                            getItems().
                            stream().
                            map(Message -> {
                                try {
                                    log.info("ping");
                                    return sumOfList.add(new MessageData(Message.getText(),
                                            vk.users().get(actor).userIds(Message.getFromId().toString()).execute().get(0).getFirstName() + " " +
                                                    vk.users().get(actor).userIds(Message.getFromId().toString()).execute().get(0).getFirstName(),
                                            Message.getDate()));

                                } catch (ApiException | ClientException e) {
                                    log.error("Error occured:" + e.getMessage());
                                }
                                return null;
                            }).collect(Collectors.toList());


                }
            }

        } catch (ClientException e) {
            log.error("Error occured:" + e.getMessage());
        }
    }

    public static void dataCleaner() {

        sumOfList.clear();
    }
}
