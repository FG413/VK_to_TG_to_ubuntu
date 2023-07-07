package com.example.nlmessagebot.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MessageData {

    private final String text;

    private final String name;

    private final int date;
}
