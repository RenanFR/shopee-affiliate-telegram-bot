package com.agamatec.shopee_affiliate_telegram_bot.model;

public class Click {
	private String clickTime;
	private String platform;
	private String referrer;

	public Click() {
	}

	public String getClickTime() {
		return clickTime;
	}

	public void setClickTime(String clickTime) {
		this.clickTime = clickTime;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}
}