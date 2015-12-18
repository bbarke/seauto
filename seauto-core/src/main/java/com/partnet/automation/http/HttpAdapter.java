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

import java.net.URI;
import java.util.List;

/**
 * @author bbarker
 * @since 12/7/15.
 */
public interface HttpAdapter {
  public Response get(URI uri, HeaderAdapter[] headers);
  public Response post(URI uri, HeaderAdapter[] headers, String contentType, String body);
  public Response put(URI uri, HeaderAdapter[] headers, String contentType, String body);
  public Response delete(URI uri, HeaderAdapter[] headers);
  public void clearCookies();
  public void addCookies(List<CookieAdapter> cookies);
}
