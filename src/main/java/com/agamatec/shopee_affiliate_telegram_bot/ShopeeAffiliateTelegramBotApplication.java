package com.agamatec.shopee_affiliate_telegram_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.agamatec.shopee_affiliate_telegram_bot.bot.ShopeeBot;

@SpringBootApplication
public class ShopeeAffiliateTelegramBotApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext ctx = SpringApplication.run(ShopeeAffiliateTelegramBotApplication.class, args);

		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		ShopeeBot bot = ctx.getBean(ShopeeBot.class);
		botsApi.registerBot(bot);
	}
}