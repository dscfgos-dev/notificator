// package com.uncledavecode.notificator.controllers;

// import com.uncledavecode.notificator.bot.NotificatorBot;
// import com.uncledavecode.notificator.utils.BotUtils;

// import org.springframework.stereotype.Component;

// @Component
// public class BotController {
//     private final NotificatorBot notificatorBot;

//     public BotController(NotificatorBot notificatorBot) {
//         this.notificatorBot = notificatorBot;
//     }

//     public void sendMessage(String message, Long chatId){
//         if(chatId != null && message != null){
//             BotUtils.sendMessage(notificatorBot, message, chatId);
//         }
//     }

//     public void sendMessage(String message, Long[] chatIds){
//         if(chatIds != null && chatIds.length > 0 && message != null){
//             for (Long chatId : chatIds) {
//                 BotUtils.sendMessage(notificatorBot, message, chatId);
//             }
//         } 
//     }

// }
