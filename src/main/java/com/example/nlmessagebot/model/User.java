package com.example.nlmessagebot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "newmap_table")
public class User {
    @Id
    private Long chat_id;
    private int scenario;
    private int vk_id;
    private String token;

    public long getChat_id() {
        return chat_id;
    }

    public void setChat_id(long chat_id) {
        this.chat_id = chat_id;
    }

    public int getScenario() {
        return scenario;
    }

    public void setScenario(int scenario) {
        this.scenario = scenario;
    }

    public int getVk_id() {
        return vk_id;
    }

    public void setVk_id(int vk_id) {
        this.vk_id = vk_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatid=" + chat_id +
                ", scenario=" + scenario +
                ", vk_id=" + vk_id +
                ", token='" + token + '\'' +
                '}';
    }
}
