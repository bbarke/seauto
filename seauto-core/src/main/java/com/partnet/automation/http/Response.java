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

import java.util.List;

/**
 * Created by Brent Barker on 12/4/15.
 */
public class Response {

  private final String body;
  private final int statusCode;
  private final List<CookieAdapter> cookies;
  private final HeaderAdapter[] headers;
  private final HeaderAdapter contentType;


  private Response(Builder responseBuilder) {
    this.body = responseBuilder.body;
    this.statusCode = responseBuilder.statusCode;
    this.contentType = responseBuilder.contentType;
    this.headers = responseBuilder.headers;
    this.cookies = responseBuilder.cookies;
  }

  public HeaderAdapter getContentType() {
    return contentType;
  }

  public HeaderAdapter[] getHeaders() {
    return headers;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getBody() {
    return body;
  }

  public JSONObject getBodyAsJson() {
    return new JSONObject(getBody());
  }

  public JSONArray getBodyAsJsonArray() {
    return new JSONArray(getBody());
  }

  public List<CookieAdapter> getCookies() {
    return cookies;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Response:\n");
    sb.append("statusCode:\n\t").append(statusCode).append("\n");

    sb.append("cookies:\n");
    for(CookieAdapter cookie : cookies) {
      sb.append("\t").append(cookie).append("\n");
    }

    sb.append("headers:\n");
    for(HeaderAdapter header : headers)
      sb.append("\t").append(header).append("\n");

    sb.append("contentType:\n\t").append(contentType).append("\n");

    if(!body.isEmpty())
      sb.append("body\n").append(body);

    return sb.toString();
  }

  public static class Builder {
    private String body;
    private int statusCode;
    private HeaderAdapter[] headers;
    private HeaderAdapter contentType;
    private List<CookieAdapter> cookies;

    public Response build() {
      return new Response(this);
    }

    public Builder setStatusCode(int statusCode) {
      this.statusCode = statusCode;
      return this;
    }

    public Builder setBody(String body) {
      this.body = body;
      return this;
    }

    public Builder setHeaders(HeaderAdapter[] headers) {
      this.headers = headers;
      return this;
    }


    public Builder setContentType(HeaderAdapter contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder setCookies(List<CookieAdapter> cookies) {
      this.cookies = cookies;
      return this;
    }
  }
}
