package com.example.demo.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.HttpCookie;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestRestClient {
	private HttpStatus expectedStatus;
	private String path;
	private int port;
	private String payload;
	private FileSystemResource payloadByteArray;
	private List<HttpCookie> cookies = new ArrayList<HttpCookie>();
	private HttpMethod method;
	private Map<String, String> urlParameters = new HashMap<>();
	private Map<String, String> headers = new HashMap<>();
	private MediaType mediaType;
	
	public TestRestClient expectOkay() {
		this.expectedStatus = HttpStatus.OK;
		return this;
	}
	
	public TestRestClient withContentType(MediaType mediaType) {
		this.mediaType = mediaType;
		return this;
	}

	public TestRestClient withExpectedStatus(HttpStatus expectedStatus) {
		this.expectedStatus = expectedStatus;
		return this;
	}

	public TestRestClient withPath(String path) {
		this.path = path;
		return this;
	}

	public TestRestClient withMethod(HttpMethod method) {
		this.method = method;
		return this;
	}

	public TestRestClient withPort(int port) {
		this.port = port;
		return this;
	}

	public TestRestClient withPayload(String payload) {
		this.payload = payload;
		return this;
	}
	
	public TestRestClient withPayload(FileSystemResource payload) {
		this.payloadByteArray = payload;
		return this;
	}
	
	public TestRestClient withURLParameter(String name, String value) {
		this.urlParameters.put(name, value);
		return this;
	}

	public <T extends ValidateableVO> TestRestClient withPayload(T payload) throws JsonProcessingException {
		this.payload = new ObjectMapper().writeValueAsString(payload);
		return this;
	}
	
	public TestRestClient withCookie(String name, String value) {
		HttpCookie httpCookie = new HttpCookie(name, value);
		this.cookies.add(httpCookie);
		return this;
	}
	
	public TestRestClient withHeader(String name, String value) {
		this.headers.put(name, value);
		return this;
	}
	
	public <T> ResponseEntity<T> fire(Class<T> responseType) {
		
		//preconditions
		if (this.path == null) {
			throw new IllegalStateException("Path darf nicht null sein");
		}
		
		TestRestTemplate restTemplate = new TestRestTemplate();
		restTemplate.getRestTemplate().getMessageConverters().add(0,
				new StringHttpMessageConverter(Charset.forName("UTF-8")));
		HttpHeaders httpHeaders = new HttpHeaders();
		if (this.mediaType != null) {
			httpHeaders.setContentType(this.mediaType);
		}
		
		for (HttpCookie cookie : this.cookies) {
			httpHeaders.add(HttpHeaders.COOKIE, cookie.toString());
		}
		for (String h : this.headers.keySet()) {
			httpHeaders.add(h, this.headers.get(h));
		}
		
		
		String urlAddition = new String();

		if (!urlParameters.isEmpty()) {
			boolean first = true;
			for (String u : this.urlParameters.keySet()) {
				if (first) {
					urlAddition += "?";
					first = false;
				} else {
					urlAddition += "&";
				}
				urlAddition += u + "=" + this.urlParameters.get(u);
			}
		}

		if (this.method == null) {
			if (this.payload == null) {
				this.method = HttpMethod.GET;
			} else {
				this.method = HttpMethod.POST;
			}
		}

		try {
			ResponseEntity<T> response = sendRequest(responseType, restTemplate, httpHeaders, urlAddition, this.method);
			assertEquals("Unexpectetd HttpStatus", expectedStatus.value(), response.getStatusCode().value());
			return response;
		} catch (IOException e) {
			fail(e.getMessage());
		}
		return null;

	}

	private <T> ResponseEntity<T> sendRequest(Class<T> responseType, TestRestTemplate restTemplate, HttpHeaders headers,
			String urlAddition, HttpMethod method) throws IOException {
		ResponseEntity<T> response;
		if (payload != null) {
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(payload, headers);
			response = restTemplate.exchange("http://localhost:" + port + path + urlAddition, method, entity,
					responseType);
		} else if (this.payloadByteArray != null) {
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", this.payloadByteArray);
			HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
			response = restTemplate.postForEntity("http://localhost:" + port + path + urlAddition, entity, responseType);
		} else {
			HttpEntity<String> entity = new HttpEntity<>(null, headers);
			response = restTemplate.exchange("http://localhost:" + port + path + urlAddition, method, entity,
					responseType);
		}
		return response;
	}

	public TestRestClient withCookie(HttpCookie cookie) {
		this.cookies.add(cookie);
		return this;
	}

}
