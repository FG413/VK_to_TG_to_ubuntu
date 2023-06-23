package com.example.nlmessagebot.service;

public class MessageData {
    private final String text;
    private final int date;
    private final String name;

    public MessageData(String text, String name, int date) {
        this.text = text;
        this.name = name;
        this.date = date;
    }

    public String getText() {
        return text;
    }


    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
    }


}
