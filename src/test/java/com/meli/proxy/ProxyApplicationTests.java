package com.meli.proxy;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProxyApplicationTests {
	private static final String CATEGORIES = "/categories/MLA97994";

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void sendRequest() {
		ResponseEntity<String> response = restTemplate.getForEntity(CATEGORIES, String.class);
		assertEquals(OK, response.getStatusCode());
		HttpHeaders headers = response.getHeaders();
		assertEquals("1", headers.getFirst("X-RateLimit-Replenish-Rate"));
		assertEquals("2", headers.getFirst("X-RateLimit-Burst-Capacity"));
	}

	@Test
	public void sendTooManyRequests() throws InterruptedException {
		ResponseEntity<String> response = restTemplate.getForEntity(CATEGORIES, String.class);
		assertEquals(OK, response.getStatusCode());

		for (int i = 0; i < 3; i++) {
			response = restTemplate.getForEntity(CATEGORIES, String.class);
		}

		assertEquals(TOO_MANY_REQUESTS, response.getStatusCode());

		TimeUnit.SECONDS.sleep(1);

		response = this.restTemplate.getForEntity(CATEGORIES, String.class);
		assertEquals(OK, response.getStatusCode());
	}
}
