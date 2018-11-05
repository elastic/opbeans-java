/*-
 * #%L
 * Opbeans Java Demo Application
 * %%
 * Copyright (C) 2018 the original author or authors
 * %%
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
 * #L%
 */
package co.elastic.apm.opbeans.controllers;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Controller
public class ElasticsearchController {
	private static final String NEW_LINE = "<br>";
	public static final String MY_INDEX = "my-index";
	public static final String DOC = "_doc";
	public static final String ID = "1";
	private static RestClient lowLevelClient;
	private static RestHighLevelClient client;

	@Autowired
	public ElasticsearchController() {
		// Create the client
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("user", "pass"));

		RestClientBuilder builder =  RestClient.builder(new HttpHost("localhost", 9200))
				.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
		lowLevelClient = builder.build();

		client = new RestHighLevelClient(builder);
	}

/*
	@RequestMapping({ "/invoke-es-scenario" })
	public ResponseEntity<String> testEs6_3Scenario() {
		StringBuilder body = new StringBuilder();
		try {
			client.indices().create(new CreateIndexRequest(MY_INDEX));
			body.append("Created the index").append(NEW_LINE);

			// Index a document
			DocWriteResponse response = client.index(new IndexRequest(MY_INDEX, DOC, ID).source(
					jsonBuilder()
							.startObject()
							.field("FOO", "BAR")
							.endObject()
			).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE));
			body.append("Indexed a document: ").append(response.status().toString()).append(NEW_LINE);

			// Search
			SearchRequest searchRequest = new SearchRequest(MY_INDEX);
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(QueryBuilders.termQuery("FOO", "BAR"));
			sourceBuilder.from(0);
			sourceBuilder.size(5);
			searchRequest.source(sourceBuilder);
			SearchResponse sr = client.search(searchRequest);
			body.append("Searched for the document: ").append(sr.status().toString()).append(NEW_LINE);

			//Update
			Map<String, Object> jsonMap = new HashMap<>();
			jsonMap.put("FOO", "BAZ");
			response = client.update(new UpdateRequest(MY_INDEX, DOC, ID).doc(jsonMap)
					.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE));
			body.append("Updated the document: ").append(response.status().toString()).append(NEW_LINE);

			// Delete the document
			response = client.delete(new DeleteRequest(MY_INDEX, DOC, ID));
			body.append("Deleted the document: ").append(response.status().toString()).append(NEW_LINE);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.indices().delete(new DeleteIndexRequest(MY_INDEX));
				body.append("Deleted the index: ").append(NEW_LINE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok().header("content-type", "text/html").body(body.toString());
	}
*/

	@RequestMapping({ "/invoke-es-scenario" })
	public ResponseEntity<String> testEs6_4Scenario() {
		StringBuilder body = new StringBuilder();
		try {
			client.indices().create(new CreateIndexRequest(MY_INDEX), RequestOptions.DEFAULT);
			body.append("Created the index: ").append(NEW_LINE);

			// Index a document
			IndexResponse ir = client.index(new IndexRequest(MY_INDEX, DOC, ID).source(
					jsonBuilder()
							.startObject()
							.field("FOO", "BAR")
							.endObject()
			).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE), RequestOptions.DEFAULT);
			body.append("Indexed a document: ").append(ir.status().toString()).append(NEW_LINE);

			// Search
			SearchRequest searchRequest = new SearchRequest(MY_INDEX);
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(QueryBuilders.termQuery("FOO", "BAR"));
			sourceBuilder.from(0);
			sourceBuilder.size(5);
			searchRequest.source(sourceBuilder);
			SearchResponse sr = client.search(searchRequest, RequestOptions.DEFAULT);
			body.append("Searched for the document: ").append(sr.status().toString()).append(NEW_LINE);

			//Update
			Map<String, Object> jsonMap = new HashMap<>();
			jsonMap.put("FOO", "BAZ");
			UpdateResponse ur = client.update(new UpdateRequest(MY_INDEX, DOC, ID).doc(jsonMap)
					.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE), RequestOptions.DEFAULT);
			body.append("Updated the document: ").append(ur.status().toString()).append(NEW_LINE);

			// Delete the document
			DeleteResponse dr = client.delete(new DeleteRequest(MY_INDEX, DOC, ID), RequestOptions.DEFAULT);
			body.append("Deleted the document: ").append(dr.status().toString()).append(NEW_LINE);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.indices().delete(new DeleteIndexRequest(MY_INDEX), RequestOptions.DEFAULT);
				body.append("Deleted the index: ").append(NEW_LINE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok().header("content-type", "text/html").body(body.toString());
	}

/*
	@RequestMapping({ "/invoke-es-scenario" })
	public ResponseEntity<String> testEs5Scenario() {
		StringBuilder body = new StringBuilder();

		try {
			Response response = lowLevelClient.performRequest("PUT", "/my-index");
			body.append("Created an index: ").append(response.getStatusLine()).append(NEW_LINE);

			String doc = "{\n" +
					"    \"FOO\" : \"BAR\" \n" +
					"}";
			HttpEntity entity = new NStringEntity(doc, ContentType.APPLICATION_JSON);
			Map<String, String> refreshPolicyParams = new HashMap<>();
			refreshPolicyParams.put("refresh", "true");
			response = lowLevelClient.performRequest("POST", "/my-index/_doc/1", refreshPolicyParams, entity);
			body.append("Indexed a document: ").append(response.getStatusLine()).append(NEW_LINE);

			entity = new NStringEntity("{\"from\":0,\"size\":5,\"query\":{\"term\":{\"foo\":{\"value\":\"bar\",\"boost\":1.0}}}}", ContentType.APPLICATION_JSON);
			response = lowLevelClient.performRequest("GET", "/my-index/_search", new HashMap<>(), entity);
			body.append("Searched for the document: number of hits: ").append(response.getStatusLine()).append(NEW_LINE);

			doc = "{\n" +
					"    \"doc\" : {\n" +
					"        \"FOO\" : \"BAZ\"\n" +
					"    }\n" +
					"}";
			entity = new NStringEntity(doc, ContentType.APPLICATION_JSON);
			response = lowLevelClient.performRequest("POST", "/my-index/_doc/1/_update", refreshPolicyParams, entity);
			body.append("Updated the document: ").append(response.getStatusLine()).append(NEW_LINE);

			response = lowLevelClient.performRequest("DELETE", "/my-index/_doc/1");
			body.append("Deleted the document: ").append(response.getStatusLine()).append(NEW_LINE);

			lowLevelClient.performRequest("DELETE", "/my-index");
		} catch (Exception e) {
			e.printStackTrace();
			body.append("Error while communicating with ES: ").append(e.getMessage()).append(NEW_LINE);
		}
		finally {
			try {
				Response response = lowLevelClient.performRequest("DELETE", "/my-index");
				body.append("Deleted the index: ").append(response.getStatusLine()).append(NEW_LINE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ResponseEntity.ok().header("content-type", "text/html").body(body.toString());
	}
*/

}