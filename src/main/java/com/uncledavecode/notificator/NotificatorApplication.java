package com.uncledavecode.notificator;

import com.uncledavecode.notificator.bot.NotificatorBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class NotificatorApplication {

	public static void main(String[] args) {
		try {
			ApplicationContext ctx = SpringApplication.run(NotificatorApplication.class, args);

			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new NotificatorBot(ctx));
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

	}

}
