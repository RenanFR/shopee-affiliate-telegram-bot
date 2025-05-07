package com.agamatec.shopee_affiliate_telegram_bot.bot;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.agamatec.shopee_affiliate_telegram_bot.model.ConversionReport;
import com.agamatec.shopee_affiliate_telegram_bot.model.ConversionReportConnection;
import com.agamatec.shopee_affiliate_telegram_bot.service.AffiliateService;
import com.agamatec.shopee_affiliate_telegram_bot.service.ShopeeApiService;

import jakarta.annotation.PostConstruct;

@Component
public class ShopeeBot extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(ShopeeBot.class);

	@Value("${telegram.bot.username}")
	private String botUsername;

	@Value("${telegram.bot.token}")
	private String botToken;

	private final AffiliateService affiliateService;
	private final ShopeeApiService apiService;

	public ShopeeBot(AffiliateService affiliateService, ShopeeApiService apiService) {
		this.affiliateService = affiliateService;
		this.apiService = apiService;
	}

	@PostConstruct
	public void init() {
		logger.info("ShopeeBot iniciado com username='{}'", botUsername);
	}

	@Override
	public String getBotUsername() {
		return botUsername;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

	@Override
	public void onUpdateReceived(Update update) {
		logger.debug("onUpdateReceived chamado com update: {}", update);
		if (update.hasMessage() && update.getMessage().hasText()) {
			String text = update.getMessage().getText().trim();
			Long chatId = update.getMessage().getChatId();
			String[] parts = text.split("\\s+");

			try {
				switch (parts[0].toLowerCase()) {
				case "/config":
					handleConfig(parts, chatId);
					break;
				case "/conversions":
					handleConversions(parts, chatId);
					break;
				default:
					sendMessage(chatId, "Comando não reconhecido. Use /config ou /conversions.");
				}
			} catch (Exception e) {
				logger.error("Erro ao processar comando: {}", text, e);
				sendMessage(chatId, "Erro interno: " + e.getMessage());
			}
		}
	}

	private void handleConfig(String[] parts, Long chatId) {
		if (parts.length == 3) {
			affiliateService.saveCredentials(chatId, parts[1], parts[2]);
			sendMessage(chatId, "Credenciais salvas em memória");
		} else {
			sendMessage(chatId, "Uso: /config <AppID> <Secret>");
		}
	}

	private void handleConversions(String[] parts, Long chatId) {
		affiliateService.findByChatId(chatId).ifPresentOrElse(creds -> {
			long purchaseStart;
			long purchaseEnd;
			if (parts.length == 3) {
				purchaseStart = LocalDate.parse(parts[1]).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
				purchaseEnd = LocalDate.parse(parts[2]).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
			} else {
				purchaseEnd = Instant.now().getEpochSecond();
				purchaseStart = Instant.now().minusSeconds(7 * 24 * 3600).getEpochSecond();
			}
			ConversionReportConnection report = apiService.fetchConversionReport(creds.getAppId(), creds.getSecret(),
					(int) purchaseStart, (int) purchaseEnd, 50, "");
			List<ConversionReport> nodes = report.getNodes();
			if (nodes == null || nodes.isEmpty()) {
				sendMessage(chatId, "Nenhuma conversão encontrada nesse período.");
			} else {
				StringBuilder sb = new StringBuilder();
				nodes.forEach(r -> sb.append(r.getConversionId()).append(" | pedido em: ").append(r.getPurchaseTime())
						.append(" | click: ").append(r.getClickTime()).append(" | comissão: ")
						.append(r.getTotalCommission()).append("\n"));
				sendMessage(chatId, sb.toString());
			}
		}, () -> sendMessage(chatId, "Por favor, configure suas credenciais com /config primeiro."));
	}

	private void sendMessage(Long chatId, String text) {
		SendMessage msg = SendMessage.builder().chatId(chatId.toString()).text(text).build();
		try {
			execute(msg);
		} catch (Exception e) {
			logger.error("Falha ao enviar mensagem para chatId={}: {}", chatId, text, e);
		}
	}
}
