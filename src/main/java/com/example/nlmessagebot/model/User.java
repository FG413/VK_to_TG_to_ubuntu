package com.example.nlmessagebot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "user_info") // change name, user_info, userr
@Getter
@Setter
public class User {
    @Id
    private Long chatId;
    private int scenario;
    private int vkId;
    private String token;

    @Override
    public String toString() {
        return "User{" +
                "chatid=" + chatId +
                ", scenario=" + scenario +
                ", vk_id=" + vkId +
                ", token='" + token + '\'' +
                '}';
    }
}
