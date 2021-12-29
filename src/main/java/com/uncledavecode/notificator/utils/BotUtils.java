package com.uncledavecode.notificator.utils;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotUtils {
    public static void sendMessage(DefaultAbsSender sender, String text, Long chatId) {
        sendMessage(sender, text, chatId, null);
    }

    public static void sendMessage(DefaultAbsSender sender, String text, Long chatId, String parseMode) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(parseMode);
        sendMessage.setText(text);
        sendMessage.setChatId(chatId.toString());
        sendMessage(sender, sendMessage);
    }

    public static void sendMessage(DefaultAbsSender sender, SendMessage message) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(DefaultAbsSender sender,String message, Long[] chatIds){
        if(chatIds != null && chatIds.length > 0 && message != null){
            for (Long chatId : chatIds) {
                sendMessage(sender, message, chatId);
            }
        } 
    }
}
