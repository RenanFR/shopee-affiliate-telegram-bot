package com.agamatec.shopee_affiliate_telegram_bot.service;

import java.time.Instant;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.agamatec.shopee_affiliate_telegram_bot.model.ConversionReportConnection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ShopeeApiService {
	private static final String API_ENDPOINT = "https://open-api.affiliate.shopee.com.br/graphql";
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	public ShopeeApiService() {
		this.restTemplate = new RestTemplate();
		this.objectMapper = new ObjectMapper();
	}

	public ConversionReportConnection fetchConversionReport(String appId, String secret, long purchaseTimeStart,
			long purchaseTimeEnd, int limit, String scrollId) {

		String query = """
				    query getConversionReport(
				      $purchaseTimeStart: Int64!,
				      $purchaseTimeEnd:   Int64!,
				      $limit:             Int!,
				      $scrollId:          String
				    ) {
				      conversionReport(
				        purchaseTimeStart: $purchaseTimeStart,
				        purchaseTimeEnd:   $purchaseTimeEnd,
				        limit:             $limit,
				        scrollId:          $scrollId
				      ) {
				        nodes {
				          conversionId
				          purchaseTime
				          clickTime
				          conversionStatus
				          totalCommission
				          device
				          referrer
				        }
				        pageInfo {
				          hasNextPage
				          scrollId
				          limit
				        }
				      }
				    }
				""";

		Map<String, Object> variables = Map.of("purchaseTimeStart", String.valueOf(purchaseTimeStart),
				"purchaseTimeEnd", String.valueOf(purchaseTimeEnd), "limit", limit, "scrollId", scrollId);
		Map<String, Object> payload = Map.of("query", query, "variables", variables);

		String body;
		try {
			body = objectMapper.writeValueAsString(payload);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao serializar payload GraphQL", e);
		}

		String timestamp = String.valueOf(Instant.now().getEpochSecond());
		String signature = DigestUtils.sha256Hex(appId + timestamp + body + secret);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization",
				String.format("SHA256 Credential=%s,Timestamp=%s,Signature=%s", appId, timestamp, signature));

		HttpEntity<String> request = new HttpEntity<>(body, headers);
		JsonNode root = restTemplate.postForObject(API_ENDPOINT, request, JsonNode.class);

		if (root == null || root.path("data").isMissingNode()) {
			throw new RuntimeException("Resposta inválida da API Shopee");
		}

		JsonNode conv = root.path("data").path("conversionReport");
		try {
			return objectMapper.convertValue(conv, new TypeReference<ConversionReportConnection>() {
			});
		} catch (Exception e) {
			throw new RuntimeException("Erro ao desserializar resposta da Shopee", e);
		}
	}
}
