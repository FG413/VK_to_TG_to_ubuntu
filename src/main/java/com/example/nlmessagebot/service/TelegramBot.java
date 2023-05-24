package com.example.nlmessagebot.service;

import java.time.Instant;
import java.util.*;

import com.example.nlmessagebot.config.BotConfig;
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
        List<BotCommand> listofCommands = Arrays.asList(new BotCommand("/get_messages", "send last 4 messages"),
                new BotCommand("/get_mydata", "send actual id and token"),
                new BotCommand("/set_id", "allow to set new id"),
                new BotCommand("/set_token", "allow to set new token"),
                new BotCommand("/help", "gives information about setting your data in bot")

        );
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
            if (!IdMapper.idToScenario.containsKey(chatId)) {
                IdMapper.setNewId(chatId);

            }

            if (IdMapper.getScenario(chatId) == 0) {
                String messageText = update.getMessage().getText();

                switch (messageText) {
                    case "/get_messages":
                        try {
                            VkData.dataReader(IdMapper.getActor(chatId));
                            for(int count=VkData.globalListOfText.size()-1;count>-1;count--){
                                sendMessage(chatId,"время: "+
                                        Instant.ofEpochSecond(VkData.globalListOfDate.get(count)) + "\n" +
                                        VkData.globalListOfName.get(count)+": \n" +
                                        VkData.globalListOfText.get(count));
                            }
                            VkData.dataCleaner();
                        } catch (ApiAuthException e) {
                            sendMessage(chatId, "произошла ошибка, пожалуйста введите  новые id и/или токен");
                        }
                        break;
                    case "/set_token":
                        sendMessage(chatId, "Пожалуйста установите новый токен");
                        IdMapper.setNewScenario(chatId, 1);
                        break;
                    case "/set_id":
                        sendMessage(chatId, "Пожалуйста установите новый id");
                        IdMapper.setNewScenario(chatId, 2);
                        break;
                    case "/get_mydata":
                        sendMessage(chatId, IdMapper.getToken(chatId) + "\n" + IdMapper.getVkId(chatId));
                        break;
                    case "/start":
                        sendMessage(chatId,"Привет! Этот бот позволит вам получить ваши непрочитанные сообщения из vk в telegram." +
                                "\n" + "Для начала вам нужно сообщить боту свои vk_id и access_token с помощь комманд /set_id и /set_token." +
                                "\n" + "Если вам непонятно как получить эти данные, возпользуйтесь командой /help" );
                        break;
                    case "/help":
                        sendMessage(chatId,"Для получения vk_id зайдите на свою страницу во вконтакте, откройте свою фотографию и в ссылке на страницу скопируйте цифры находящиеся между =photo и _");
                        sendMessage(chatId,"Для получения access_token, перейдите по ссылке: https://vkhost.github.io/, " +
                                "в настройках выберете сообщения и доступ в любое время, после чего нажмите получить, " +
                                "а затем разрешить и в конце выберете из полученной страницы скопируйте последовательность между access_token= и &expires_in");
                        break;
                    default:
                        sendMessage(chatId, "sorry");
                }
            } else if (IdMapper.getScenario(chatId) == 1) {
                IdMapper.setNewToken(chatId, update.getMessage().getText());
                IdMapper.setNewScenario(chatId, 0);
            } else if (IdMapper.getScenario(chatId) == 2) {
                try {
                    IdMapper.setNewVkId(chatId, Integer.parseInt(update.getMessage().getText()));
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "Некорректный ввод");
                }
                IdMapper.setNewScenario(chatId, 0);
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