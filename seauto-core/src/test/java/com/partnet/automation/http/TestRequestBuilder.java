/*
 * Copyright 2015 Partnet, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.partnet.automation.http;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;


/**
 * @author bbarker
 * @since 12/8/15
 */
public class TestRequestBuilder {

  private RequestBuilder rb;
  @Before
  public void setup() {
    rb = new RequestBuilder("http://jsonplaceholder.typicode.com", new ApacheHttpAdapter());
    rb.setContentType("application/json");
  }

  @After
  public void tearDown() {
    rb = null;
  }

  @Test
  public void test_postMethod() {
    JSONObject expected = new JSONObject();
    expected.put("title", "blah");
    expected.put("body", "new content");
    expected.put("userId", 1);

    Response response = rb.setMethod(HttpMethod.POST).setPath("/posts").setBody(expected).build();
    Assert.assertEquals(String.format("Response from POST was not correct\n%s", response),
        201, response.getStatusCode());

    expected.put("id", 101);
    Assert.assertEquals(String.format("Response body did not have correct values:\n%s", response),
        expected.toString(), response.getBodyAsJson().toString());
  }

  @Test
  public void test_getMethod() {
    Response response = rb.setMethod(HttpMethod.GET)
        .addParameter("userId", "1")
        .setPath("/posts")
        .addParameter("userId", "2")
        .build();

    JSONArray jsonArray = response.getBodyAsJsonArray();

    Set<Integer> userId = new HashSet<>();
    for(int i = 0; i < jsonArray.length(); i++) {
      userId.add(jsonArray.getJSONObject(i).getInt("userId"));
    }

    Set<Integer> expected = new HashSet<>();
    expected.add(1);
    expected.add(2);

    Assert.assertEquals("Returned json did not have correct amount of userIds", 2, userId.size());
    Assert.assertEquals(expected, userId);
  }

  @Test
  public void test_putMethod() {

    JSONObject json = new JSONObject();
    json.put("title", "nesciunt quas odio");
    json.put("body", "test");
    json.put("userId", "500");

    Response response = rb.setMethod(HttpMethod.PUT)
        .setBody(json)
        .setPath("/posts/5")
        .build();

    JSONObject expect = new JSONObject();
    expect.put("id", 5);
    expect.put("title", "nesciunt quas odio");
    expect.put("body", "test");
    expect.put("userId", 500);
    Assert.assertEquals(expect.toString(), response.getBodyAsJson().toString());
  }

  @Test
  public void test_putValidateResponse() {

    JSONObject json = new JSONObject();
    json.put("title", "nesciunt quas odio");
    json.put("body", "test");
    json.put("userId", "500");

    Response response = rb.setMethod(HttpMethod.PUT)
        .setBody(json)
        .setPath("/posts/5")
        .build()
        .validateStatusCode(200);

    JSONObject expect = new JSONObject();
    expect.put("id", 5);
    expect.put("title", "nesciunt quas odio");
    expect.put("body", "test");
    expect.put("userId", 500);

    Assert.assertEquals(expect.toString(), response.getBodyAsJson().toString());
    Assert.assertEquals(200, response.getStatusCode());
  }

  @Test
  public void test_deleteMethod() {
    Response response = rb.setMethod(HttpMethod.DELETE)
        .setPath("/posts/5")
        .build();

    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("{}", response.getBody());
  }

  @Test
  public void test_cookies() {

    JSONObject json = new JSONObject();
    json.put("title", "nesciunt quas odio");
    json.put("body", "test");
    json.put("userId", "500");

    CookieAdapter cookie = new CookieAdapter.Builder()
        .setName("test")
        .setValue("test-value")
        .setDomain("jsonplaceholder.typicode.com")
        .build();

    CookieAdapter secondCookie = new CookieAdapter.Builder()
        .setName("testTwo")
        .setValue("test-second-cookie")
        .setDomain("jsonplaceholder.typicode.com")
        .build();

    Response response = rb.setMethod(HttpMethod.PUT)
        .setBody(json)
        .setPath("/posts/5")
        .addCookie(cookie)
        .build();

    JSONObject expect = new JSONObject();
    expect.put("id", 5);
    expect.put("title", "nesciunt quas odio");
    expect.put("body", "test");
    expect.put("userId", 500);
    Assert.assertEquals(expect.toString(), response.getBodyAsJson().toString());

    Assert.assertEquals(response.getCookies().size(), 1);
    Assert.assertEquals(response.getCookies().get(0), cookie);


    rb.addCookie(secondCookie);

    response = rb.setMethod(HttpMethod.PUT)
        .setBody(json)
        .setPath("/posts/5")
        .addCookie(cookie)
        .build();

    Assert.assertEquals(response.getCookies().size(), 2);
    Assert.assertEquals(response.getCookies().get(0), cookie);
    Assert.assertEquals(response.getCookies().get(1), secondCookie);

    rb.clearCookies();
  }
}
