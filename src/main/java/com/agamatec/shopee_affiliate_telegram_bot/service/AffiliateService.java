package com.agamatec.shopee_affiliate_telegram_bot.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.agamatec.shopee_affiliate_telegram_bot.model.AffiliateCredentials;

@Service
public class AffiliateService {

	private final Map<Long, AffiliateCredentials> store = new ConcurrentHashMap<>();

	public void saveCredentials(Long chatId, String appId, String secret) {
		store.put(chatId, new AffiliateCredentials(appId, secret));
	}

	public Optional<AffiliateCredentials> findByChatId(Long chatId) {
		return Optional.ofNullable(store.get(chatId));
	}
}