package com.example.nlmessagebot.service;

import java.time.Instant;

import com.example.nlmessagebot.config.BotConfig;
import com.example.nlmessagebot.model.User;
import com.example.nlmessagebot.repository.UserRepository;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiAuthException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    @Autowired
    private UserRepository userRepository;

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> commands = List.of(
                new BotCommand("/get_messages", "send last 4 messages"),
                new BotCommand("/get_mydata", "send actual id and token"),
                new BotCommand("/set_id", "allow to set new id"),
                new BotCommand("/set_token", "allow to set new token"),
                new BotCommand("/help", "gives information about setting your data in bot")
        );
        try {
            execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error occured:" + e.getMessage());
        }
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (!(update.hasMessage() && update.getMessage().hasText())) {
            log.debug("No message");
            return;
        }

        long chatId = update.getMessage().getChatId();
        registerUser(update.getMessage());
        User localUser = new User();
        if (userRepository.findById(chatId).get().getScenario() == 0) {
            String messageText = update.getMessage().getText();
            switch (messageText) {
                case "/get_messages" -> {
                    try {
                        UserActor actor = new UserActor(userRepository.findById(chatId).get().getVkId(), userRepository.findById(chatId).get().getToken());
                        List<MessageData> unreadMessages = VkDataCollector.getUnreadMessages(actor);
                        for (MessageData list : unreadMessages) {
                            sendMessage(chatId, "время: " +
                                    Instant.ofEpochSecond(list.getDate()) + "\n" +
                                    list.getName() + ": \n" +
                                    list.getText());
                        }
                        log.info(VkDataCollector.sumOfList.toString());
                    } catch (ApiAuthException e) {
                        sendMessage(chatId, "произошла ошибка, пожалуйста введите  новые id и/или токен");
                        log.error("Error occured:" + e.getMessage());
                    }
                }
                case "/set_token" -> {
                    sendMessage(chatId, "Пожалуйста установите новый токен");
                    localUser = userRepository.findById(chatId).get();
                    localUser.setScenario(1);
                    userRepository.save(localUser);
                }
                case "/set_id" -> {
                    sendMessage(chatId, "Пожалуйста установите новый id");
                    localUser = userRepository.findById(chatId).get();
                    localUser.setScenario(2);
                    userRepository.save(localUser);
                }
                case "/get_mydata" -> {
                    sendMessage(chatId, userRepository.findById(chatId).get().getToken() + "\n" + userRepository.findById(chatId).get().getVkId());
                    log.info(String.valueOf(userRepository.findById(chatId).get().getChatId()));
                }
                case "/start" -> sendMessage(chatId, """
                        Привет! Этот бот позволит вам получить ваши непрочитанные сообщения из vk в telegram.
                        Для начала вам нужно сообщить боту свои vk_id и access_token с помощь комманд /set_id и /set_token.
                        Если вам непонятно как получить эти данные, возпользуйтесь командой /help""");
                case "/help" -> {
                    sendMessage(chatId, "Для получения vk_id зайдите на свою страницу во вконтакте, откройте свою фотографию и в ссылке на страницу скопируйте цифры находящиеся между =photo и _");
                    sendMessage(chatId, "Для получения access_token, перейдите по ссылке: https://vkhost.github.io/, " +
                            "в настройках выберете сообщения и доступ в любое время, после чего нажмите получить, " +
                            "а затем разрешить и в конце выберете из полученной страницы скопируйте последовательность между access_token= и &expires_in");
                }
                default -> sendMessage(chatId, "sorry");
            }
        } else if (userRepository.findById(chatId).get().getScenario() == 1) {
            localUser = userRepository.findById(chatId).get();
            localUser.setToken(update.getMessage().getText());
            localUser.setScenario(0);
            userRepository.save(localUser);
        } else if (userRepository.findById(chatId).get().getScenario() == 2) {
            localUser = userRepository.findById(chatId).get();
            try {

                localUser.setVkId(Integer.parseInt(update.getMessage().getText()));

                log.info("user saved:" + localUser);
            } catch (NumberFormatException e) {
                sendMessage(chatId, "Некорректный ввод");
            }

            localUser.setScenario(0);
            userRepository.save(localUser);
        }
    }

    public String getBotToken() {return config.getToken();}

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
            log.error("Error occured:" + e.getMessage());
        }
    }

    public void registerUser(Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()) {
            var chatId = message.getChatId();
            var scenario = 0;
            var vk_id = 0;
            var token = "vk.q";
            User user = new User();
            user.setChatId(chatId);
            user.setScenario(scenario);
            user.setVkId(vk_id);
            user.setToken(token);
            userRepository.save(user);
            log.info("user saved:" + user);
        }
    }

}