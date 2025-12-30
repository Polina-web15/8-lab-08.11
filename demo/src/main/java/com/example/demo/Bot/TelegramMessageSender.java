package com.example.demo.Bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramMessageSender {
    private final String botToken;
    private final RestTemplate restTemplate;

    public TelegramMessageSender(@Value("${telegram.bot.token}") String botToken) {
        this.botToken = botToken;
        this.restTemplate = new RestTemplate();
    }

    public void sendMessage(Long chatId, String text) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        var payload = java.util.Map.of("chat_id", chatId, "text", text);
        restTemplate.postForObject(url, payload, Void.class);
    }
}
