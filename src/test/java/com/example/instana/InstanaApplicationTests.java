package com.example.instana;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureWebTestClient
class InstanaApplicationTests {

	@Autowired
	private WebTestClient client;

	@Value("${spring.single-line-input}")
	private String singleLineInput;

	@Value("${spring.single-line-output}")
	private String singleLineOutput;

	@Value("${spring.multi-line-input}")
	private String multiLineInput;

	@Value("${spring.multi-line-output}")
	private String multiLineOutput;

	@Test
	void testIfSingleLineInputProducesExpectedOutput() throws IOException {
		assertNotNull(singleLineInput, "singleLineInput is null. Check application.properties.");
		assertNotNull(singleLineOutput, "singleLineOutput is null. Check application.properties.");

		this.testInputAndOutput(singleLineInput, singleLineOutput);
	}

	@Test
	void testIfMultiLineInputProducesExpectedOutput() throws IOException {
		assertNotNull(multiLineInput, "multiLineInput is null. Check application.properties.");
		assertNotNull(multiLineOutput, "multiLineOutput is null. Check application.properties.");

		this.testInputAndOutput(multiLineInput, multiLineOutput);
	}

	private void testInputAndOutput(String inputFileName, String outputFileName) throws IOException {
		byte[] inputFileBytes = readResourceAsByteArray(inputFileName);

		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder
				.part("file", inputFileBytes)
				.header("Content-Disposition", "form-data; name=\"file\"; filename=\"" + inputFileName + "\"")
				.contentType(MediaType.TEXT_PLAIN);

		String expectedOutput = new String(
				readResourceAsByteArray(outputFileName),
				StandardCharsets.UTF_8
		).trim().replace("\r\n", "\n");

		client.post()
				.uri("/stats")
				.body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(String.class)
				.value(actualOutput -> {
					String normalizedActualOutput = actualOutput.trim().replace("\r\n", "\n");
					assertEquals(expectedOutput, normalizedActualOutput);
				});
	}

	private byte[] readResourceAsByteArray(String resourcePath) throws IOException {
		try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
			Objects.requireNonNull(resourceStream, "Resource not found: " + resourcePath);

			return resourceStream.readAllBytes();
		}
	}
}