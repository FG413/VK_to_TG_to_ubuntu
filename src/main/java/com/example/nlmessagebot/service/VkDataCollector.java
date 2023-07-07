package com.example.nlmessagebot.service;

import java.util.*;
import java.util.stream.Collectors;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.objects.users.UserMin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VkDataCollector {
    public final static VkApiClient vk = new VkApiClient(new HttpTransportClient());
    public static List<MessageData> sumOfList = new ArrayList<>();

    public static List<MessageData> getUnreadMessages(UserActor actor) throws ApiException, ClientException {
        List<Conversation> conversations = vk.messages()
                .getConversations(actor)
                .execute()
                .getItems()
                .stream()
                .map(ConversationWithMessage::getConversation).toList();

        Map<Integer, Integer> conversationToUnreadMessages = new HashMap<>();
        conversations.stream().map(c -> conversationToUnreadMessages.put(c.getPeer().getId(),c.getUnreadCount())).collect(Collectors.toList());
        List<MessageData> unreadMessages = new ArrayList<>();

        for (Map.Entry<Integer, Integer> conversationToUnreadMessagesCount : conversationToUnreadMessages.entrySet()) {
            if (conversationToUnreadMessagesCount.getValue() == null) {
                continue;
            }

            List<Message> messages = vk.messages().getHistory(actor)
                    .userId(conversationToUnreadMessagesCount.getKey())
                    .count(conversationToUnreadMessagesCount.getValue())
                    .execute()
                    .getItems();

            List<String> messageSendersIds = messages.stream()
                    .map(m -> m.getFromId().toString())
                    .distinct().toList();

            Map<Integer, String> senderIdToName = vk.users()
                    .get(actor)
                    .userIds(messageSendersIds)
                    .execute()
                    .stream()
                    .collect(Collectors.toMap(UserMin::getId, u -> u.getFirstName() + " " + u.getLastName()));

            unreadMessages.addAll(
                    messages.stream()
                            .map(m -> new MessageData(m.getText(), senderIdToName.get(m.getFromId()), m.getDate()))
                            .toList()
            );
        }
        return unreadMessages;
    }
}
