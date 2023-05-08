package com.example.NL_MessageBot.service;
import java.util.*;
import com.example.NL_MessageBot.config.BotConfig;
import com.vk.api.sdk.exceptions.ApiAuthException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.String;
import java.util.Arrays;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    Message message = new Message();
    private final BotConfig config;


    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = Arrays.asList(new BotCommand("/get_messages", "send last 4 messages"), new BotCommand("/get_mydata", "send actual id and token"), new BotCommand("/set_id", "allow to set new id"), new BotCommand("set_token", "allow to set new token"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    long chatId;
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            if(!IdMapper.scenario.containsKey(chatId)){
                IdMapper.setNewId(chatId);
            }

            if (IdMapper.getScenario(chatId)==0) {
                String messageText = update.getMessage().getText();

                switch (messageText) {
                    case "/get_messages":
                        try {
                            sendMessage(chatId, VkData.dataReader(IdMapper.getActor(chatId)));
                        } catch (ApiAuthException e) {
                            sendMessage(chatId, "произошла ошибка, пожалуйста введите  новые id и/или токен");
                        }
                        break;
                    case "/set_token":
                        sendMessage(chatId, "Пожалуйста установите новый токен");
                        IdMapper.setNewScenario(chatId,1);
                        break;
                    case "/set_id":
                        sendMessage(chatId, "Пожалуйста установите новый id");
                        IdMapper.setNewScenario(chatId,2);
                        break;
                    case "/get_mydata":
                        sendMessage(chatId, IdMapper.getToken(chatId) + "\n" + IdMapper.getVkId(chatId));
                        break;
                    default:
                        sendMessage(chatId, "sorry");
                }
            } else if (IdMapper.getScenario(chatId)==1) {
                IdMapper.setNewToken(chatId,update.getMessage().getText());
                IdMapper.setNewScenario(chatId,0);
            } else if (IdMapper.getScenario(chatId)==2) {
                try {
                    IdMapper.setNewVkId(chatId,Integer.parseInt(update.getMessage().getText()));
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "Некорректный ввод");
                }
                IdMapper.setNewScenario(chatId,0);
            }
        }
    }

    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
