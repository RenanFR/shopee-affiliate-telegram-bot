package com.agamatec.shopee_affiliate_telegram_bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ConversionReport {
	private long conversionId;
	private long purchaseTime;
	private long clickTime;
	private String orderStatus;
	private String totalCommission;
	private String device;
	private String referrer;

}
