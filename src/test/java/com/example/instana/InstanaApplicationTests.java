package com.example.instana;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisabledInNativeImage
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
		this.testInputAndOutput(singleLineInput, singleLineOutput);
	}

	@Test
	void testIfMultiLineInputProducesExpectedOutput() throws IOException {
		this.testInputAndOutput(multiLineInput, multiLineOutput);
	}

	private void testInputAndOutput(String inputFileName, String outputFileName) throws IOException {
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder
			.part("file", new ClassPathResource(inputFileName))
			.contentType(MediaType.MULTIPART_FORM_DATA);
		String expectedOutput = Files.readString(
			new ClassPathResource(outputFileName).getFile().toPath(),
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

}
