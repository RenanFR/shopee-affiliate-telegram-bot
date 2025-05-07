package com.agamatec.shopee_affiliate_telegram_bot.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ConversionReportConnection {
	private List<ConversionReport> nodes;
	private PageInfo pageInfo;

}