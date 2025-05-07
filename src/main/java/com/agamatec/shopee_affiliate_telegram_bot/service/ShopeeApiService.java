package com.agamatec.shopee_affiliate_telegram_bot.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.agamatec.shopee_affiliate_telegram_bot.model.Click;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ShopeeApiService {
	private static final String API_ENDPOINT = "https://open-api.affiliate.shopee.com.my/graphql";
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	public ShopeeApiService() {
		this.restTemplate = new RestTemplate();
		this.objectMapper = new ObjectMapper();
	}

	public List<Click> fetchClickReport(String appId, String secret, String fromDate, String toDate) {
		String query = """
				    query getClickReport($from: String!, $to: String!, $offset: Int!, $limit: Int!) {
				      clickReport(fromDate: $from, toDate: $to, pagination: { offset: $offset, limit: $limit }) {
				        nodes {
				          clickTime
				          platform
				          referrer
				        }
				      }
				    }
				""";

		Map<String, Object> variables = Map.of("from", fromDate, "to", toDate, "offset", 0, "limit", 50);
		Map<String, Object> payload = Map.of("query", query, "variables", variables);

		String body;
		try {
			body = objectMapper.writeValueAsString(payload);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao serializar payload GraphQL", e);
		}

		String timestamp = String.valueOf(Instant.now().getEpochSecond());
		String baseString = appId + timestamp + body + secret;
		String signature = DigestUtils.sha256Hex(baseString);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization",
				String.format("SHA256 Credential=%s,Timestamp=%s,Signature=%s", appId, timestamp, signature));

		HttpEntity<String> request = new HttpEntity<>(body, headers);
		JsonNode root = restTemplate.postForObject(API_ENDPOINT, request, JsonNode.class);
		if (root == null || root.path("data").isMissingNode()) {
			throw new RuntimeException("Resposta inválida da API Shopee");
		}

		JsonNode nodes = root.path("data").path("clickReport").path("nodes");
		try {
			return objectMapper.convertValue(nodes, new TypeReference<List<Click>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException("Erro ao desserializar resposta da Shopee", e);
		}
	}
}