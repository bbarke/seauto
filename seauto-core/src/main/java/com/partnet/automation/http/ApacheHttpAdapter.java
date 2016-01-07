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

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bbarker
 */
public class ApacheHttpAdapter
    implements HttpAdapter {


  //TODO - Make thread safe
  //http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/impl/conn/PoolingClientConnectionManager.html

  private CookieStore cookieStore = new BasicCookieStore();
  private static final Logger LOG = LoggerFactory.getLogger(ApacheHttpAdapter.class);

  private Response method(HttpRequestBase httpRequestBase, String contentType, String body) {
    Response.Builder responseBuilder;

    if(httpRequestBase instanceof HttpEntityEnclosingRequestBase) {
      this.setEntity((HttpEntityEnclosingRequestBase)httpRequestBase, contentType, body);
    }

    try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
      LOG.debug("Executing request " + httpRequestBase.getRequestLine());

      if(httpRequestBase instanceof HttpEntityEnclosingRequestBase) {
        HttpEntity entity = (((HttpEntityEnclosingRequestBase) httpRequestBase).getEntity());
        if(entity != null) {
          LOG.debug("Body being sent: " + EntityUtils.toString(entity));
        }
      }

      //handle response
      ResponseHandler<Response.Builder> responseHandler = new ResponseHandler<Response.Builder>() {

        @Override
        public Response.Builder handleResponse(final HttpResponse response)
            throws ClientProtocolException, IOException {
          Response.Builder rb = new Response.Builder();

          if(response.getEntity() != null) {
            rb.setBody(EntityUtils.toString(response.getEntity()));
          }

          rb.setStatusCode(response.getStatusLine().getStatusCode());
          rb.setHeaders(convertApacheToHeaderAdapter(response.getAllHeaders()));

          if(response.getEntity() != null) {
            rb.setContentType(convertApacheToHeaderAdapter(response.getEntity().getContentType())[0]);
          }

          return rb;
        }
      };

      //handle cookies
      HttpClientContext context = HttpClientContext.create();
      context.setCookieStore(cookieStore);

      responseBuilder = httpclient.execute(httpRequestBase, responseHandler, context);

      responseBuilder.setCookies(convertCookieToAdapter(context.getCookieStore().getCookies()));


    } catch (IOException e) {
      throw new IllegalStateException("IOException occurred while attempting to communicate with endpoint", e);
    }
    return responseBuilder.build();
  }


  @Override
  public void addCookies(List<CookieAdapter> cookieAdapter) {
    List<Cookie> apacheCookies = convertCookieAdapterToApacheCookie(cookieAdapter);
    for(Cookie cookie : apacheCookies)
    cookieStore.addCookie(cookie);
  }

  @Override
  public void clearCookies() {
    cookieStore.clear();
  }

  @Override
  public Response get(URI uri, HeaderAdapter[] headers) {
    HttpGet post = new HttpGet(uri);
    post.setHeaders(convertHeaderAdapterToApache(headers));
    return method(post, null, null);
  }

  @Override
  public Response post(URI uri, HeaderAdapter[] headers, String contentType, String body) {
    HttpPost post = new HttpPost(uri);
    post.setHeaders(convertHeaderAdapterToApache(headers));
    return method(post, contentType, body);

  }

  @Override
  public Response delete(URI uri, HeaderAdapter[] headers) {
    HttpDelete delete = new HttpDelete(uri);
    delete.setHeaders(convertHeaderAdapterToApache(headers));
    return method(delete, null, null);
  }

  @Override
  public Response put(URI uri, HeaderAdapter[] headers, String contentType, String body) {
    HttpPut put = new HttpPut(uri);
    put.setHeaders(convertHeaderAdapterToApache(headers));
    return method(put, contentType, body);
  }

  private HeaderAdapter[] convertApacheToHeaderAdapter(org.apache.http.Header ... headers)
  {
    if(headers == null) {
      return null;
    }

    HeaderAdapter headerAdapter[] = new HeaderAdapter[headers.length];

    for(int i = 0; i < headers.length; i++) {

      if(headers[i] == null)
        continue;

      headerAdapter[i] = new HeaderAdapter(headers[i].getName(), headers[i].getValue());
    }
    return headerAdapter;
  }

  private Header[] convertHeaderAdapterToApache(HeaderAdapter ... headerAdapters)
  {
    if(headerAdapters == null) {
      return null;
    }

    Header headers[] = new Header[headerAdapters.length];

    for(int i = 0; i < headerAdapters.length; i++) {
      headers[i] = new BasicHeader(headerAdapters[i].getName(), headerAdapters[i].getValue());
    }

    return headers;
  }

  private void setEntity(HttpEntityEnclosingRequestBase httpBase, String contentType, String body) {
    //TODO make charset configurable?
    if(body != null) {
      StringEntity entity = new StringEntity(body, Consts.UTF_8);

      if(contentType == null && body != null) {
        LOG.warn("Content type is not setup for request {} {}", httpBase.getMethod(), httpBase.getURI());
      }


      entity.setContentType(contentType);
      httpBase.setEntity(entity);

    }
  }

  private List<CookieAdapter> convertCookieToAdapter(List<Cookie> cookies) {
    List<CookieAdapter> adapt = new ArrayList<>();

    for(Cookie cookie : cookies) {
      adapt.add(new CookieAdapter.Builder()
          .setDomain(cookie.getDomain())
          .setName(cookie.getName())
          .setExpiryDate(cookie.getExpiryDate())
          .setPath(cookie.getPath())
          .setValue(cookie.getValue())
          .setVersion(cookie.getVersion())
          .build());
    }
    return adapt;
  }

  private List<Cookie> convertCookieAdapterToApacheCookie(List<CookieAdapter> cookies) {
    List<Cookie> apacheCookie = new ArrayList<>();

    for(CookieAdapter adaptCookie : cookies) {
      BasicClientCookie basicCookie = new BasicClientCookie(adaptCookie.getName(), adaptCookie.getValue());
      basicCookie.setDomain(adaptCookie.getDomain());
      basicCookie.setExpiryDate(adaptCookie.getExpiryDate());
      basicCookie.setPath(adaptCookie.getPath());
      basicCookie.setValue(adaptCookie.getValue());
      basicCookie.setVersion(adaptCookie.getVersion());
      apacheCookie.add(basicCookie);
    }
    return apacheCookie;
  }
}
