/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.operation.preprocess;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.Parameters;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link HeadersModifyingOperationPreprocessor}.
 *
 * @author Andy Wilkinson
 * @author Jihun Cha
 */
public class HeadersModifyingOperationPreprocessorTests {

	private final HeadersModifyingOperationPreprocessor preprocessor = new HeadersModifyingOperationPreprocessor();

	@Test
	public void addNewHeader() {
		HttpHeaders headers = new HttpHeaders();
		assertThat(this.preprocessor.add("a", "alpha").preprocess(createRequest(headers))
				.getHeaders()).containsEntry("a", Arrays.asList("alpha"));
	}

	@Test
	public void addValueToExistingHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("a", "apple");
		assertThat(this.preprocessor.add("a", "alpha").preprocess(createRequest(headers))
				.getHeaders()).containsEntry("a", Arrays.asList("apple", "alpha"));
	}

	@Test
	public void setNewHeader() {
		HttpHeaders headers = new HttpHeaders();
		assertThat(this.preprocessor.set("a", "alpha", "avocado")
				.preprocess(createRequest(headers)).getHeaders()).containsEntry("a",
						Arrays.asList("alpha", "avocado"));
	}

	@Test
	public void setExistingHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("a", "apple");
		assertThat(this.preprocessor.set("a", "alpha", "avocado")
				.preprocess(createRequest(headers)).getHeaders()).containsEntry("a",
						Arrays.asList("alpha", "avocado"));
	}

	@Test
	public void removeNonExistentHeader() {
		HttpHeaders headers = new HttpHeaders();
		assertThat(this.preprocessor.remove("a").preprocess(createRequest(headers))
				.getHeaders().size()).isEqualTo(1);
	}

	@Test
	public void removeHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("a", "apple");
		assertThat(this.preprocessor.set("a", "alpha", "avocado")
				.preprocess(createRequest(headers)).getHeaders()).containsEntry("a",
						Arrays.asList("alpha", "avocado"));
	}

	@Test
	public void removePatternHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("apple", "apple");
		headers.add("alpha", "alpha");
		headers.add("avocado", "avocado");
		headers.add("bravo", "bravo");
		assertThat(this.preprocessor.remove(Pattern.compile("^a.*"))
				.preprocess(createRequest(headers)).getHeaders().size()).isEqualTo(2);
	}

	@Test
	public void removeHeaderValueForNonExistentHeader() {
		HttpHeaders headers = new HttpHeaders();
		assertThat(this.preprocessor.remove("a", "apple")
				.preprocess(createRequest(headers)).getHeaders().size()).isEqualTo(1);
	}

	@Test
	public void removeHeaderValueWithMultipleValues() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("a", "apple");
		headers.add("a", "alpha");
		assertThat(this.preprocessor.remove("a", "apple")
				.preprocess(createRequest(headers)).getHeaders()).containsEntry("a",
						Arrays.asList("alpha"));
	}

	@Test
	public void removeHeaderValueWithSingleValueRemovesEntryEntirely() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("a", "apple");
		assertThat(this.preprocessor.remove("a", "apple")
				.preprocess(createRequest(headers)).getHeaders().size()).isEqualTo(1);
	}

	private OperationRequest createRequest(HttpHeaders headers) {
		return new OperationRequestFactory().create(URI.create("http://localhost:8080"),
				HttpMethod.GET, new byte[0], headers, new Parameters(),
				Collections.<OperationRequestPart>emptyList());
	}

}
