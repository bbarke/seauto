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

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brent Barker on 12/7/15.
 */
public class RequestBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(RequestBuilder.class);
  private final String domain;
  private String path;
  private HttpAdapter httpAdapter;
  private HttpMethod method;
  private String body;
  private String contentType;
  private List<HeaderAdapter> headers = new ArrayList<>();
  private List<Parameter> parameters = new ArrayList<>();
  private List<CookieAdapter> cookies = new ArrayList<>();

  public RequestBuilder(String domain, HttpAdapter httpAdapter) {
    this.domain = domain;
    this.httpAdapter = httpAdapter;
  }

  public RequestBuilder setMethod(HttpMethod method) {
    this.method = method;
    return this;
  }

  public RequestBuilder setPath(String path) {
    this.path = path;
    return this;
  }

  public RequestBuilder setBody(String body) {
    this.body = body;
    return this;
  }

  public RequestBuilder setBody(JSONObject body) {
    return setBody(body.toString());
  }

  public RequestBuilder setContentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  public RequestBuilder addParameter(String name, String value) {
    parameters.add(new Parameter(name, value));
    return this;
  }

  public RequestBuilder addHeader(HeaderAdapter header) {
    headers.add(header);
    return this;
  }

  public RequestBuilder addCookie(CookieAdapter cookie) {
    this.cookies.add(cookie);
    return this;
  }

  public RequestBuilder addHeader(String name, String value) {
    headers.add(new HeaderAdapter(name, value));
    return this;
  }


  public void clearCookies() {
    httpAdapter.clearCookies();
  }

  public Response build() {

    HeaderAdapter[] headerAdapArray = headers.toArray(new HeaderAdapter[headers.size()]);
    Response response;

    URI uri = getUri();

    httpAdapter.addCookies(cookies);

    if(method == null)
      throw new IllegalStateException("Http method can not be null!");

    if((method == HttpMethod.DELETE || method == HttpMethod.GET) && body != null)
      LOG.warn("Setting a body for DELETE or GET methods has no impact");

    switch(method){
      case GET:
        response = httpAdapter.get(uri, headerAdapArray);
        break;
      case POST:
        response = httpAdapter.post(uri, headerAdapArray, contentType, body);
        break;
      case PUT:
        response = httpAdapter.put(uri, headerAdapArray, contentType, body);
        break;
      case DELETE:
        response = httpAdapter.delete(uri, headerAdapArray);
        break;
      default:
        throw new IllegalArgumentException(String.format("Unknown http method: %s", method));
    }

    //clear previous values
    headers.clear();
    parameters.clear();
    path = null;
    return response;

  }

  private URI getUri() {

    StringBuilder sb = new StringBuilder();
    sb.append(domain).append(path);

    if(parameters.size() > 0) {
      sb.append("?")
          .append(parameters.get(0).name)
          .append("=")
          .append(parameters.get(0).value);
    }

    for(int i = 1; i < parameters.size(); i++) {
      sb.append("&")
          .append(parameters.get(i).name)
          .append("=")
          .append(parameters.get(i).value);
    }

    return URI.create(sb.toString());
  }

  private class Parameter {
    private final String name;
    private final String value;

    public Parameter(String name, String value) {
      this.name = name;
      this.value = value;
    }
  }


}
