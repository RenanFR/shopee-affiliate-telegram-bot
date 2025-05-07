package com.agamatec.shopee_affiliate_telegram_bot.model;

public class AffiliateCredentials {
	private final String appId;
	private final String secret;

	public AffiliateCredentials(String appId, String secret) {
		this.appId = appId;
		this.secret = secret;
	}

	public String getAppId() {
		return appId;
	}

	public String getSecret() {
		return secret;
	}
}
