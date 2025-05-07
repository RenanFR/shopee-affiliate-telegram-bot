package com.agamatec.shopee_affiliate_telegram_bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class PageInfo {
	private boolean hasNextPage;
	private String scrollId;
	private int limit;

}