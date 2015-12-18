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

import java.util.Date;

/**
 * @author bbarker
 * @since 12/7/15.
 */
public class CookieAdapter {

  private final int version;
  private final String name;
  private final String value;
  private final String domain;
  private final String path;
  private final Date expiryDate;

  private CookieAdapter(Builder builder) {
    this.version = builder.version;
    this.name = builder.name;
    this.value = builder.value;
    this.domain = builder.domain;
    this.path = builder.path;
    this.expiryDate = builder.expiryDate;
  }

  public int getVersion() {
    return version;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public String getDomain() {
    return domain;
  }

  public String getPath() {
    return path;
  }

  public Date getExpiryDate() {
    return expiryDate;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("CookieAdapter{");
    sb.append("version=").append(version);
    sb.append(", name='").append(name).append('\'');
    sb.append(", value='").append(value).append('\'');
    sb.append(", domain='").append(domain).append('\'');
    sb.append(", path='").append(path).append('\'');
    sb.append(", expiryDate=").append(expiryDate);
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CookieAdapter that = (CookieAdapter) o;

    if (version != that.version) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (value != null ? !value.equals(that.value) : that.value != null) return false;
    if (!domain.equals(that.domain)) return false;
    if (path != null ? !path.equals(that.path) : that.path != null) return false;
    return !(expiryDate != null ? !expiryDate.equals(that.expiryDate) : that.expiryDate != null);

  }

  @Override
  public int hashCode() {
    int result = version;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (value != null ? value.hashCode() : 0);
    result = 31 * result + domain.hashCode();
    result = 31 * result + (path != null ? path.hashCode() : 0);
    result = 31 * result + (expiryDate != null ? expiryDate.hashCode() : 0);
    return result;
  }

  public static class Builder {
    private int version;
    private String name;
    private String value;
    private String domain;
    private String path;
    private Date expiryDate;



    public Builder setVersion(int version) {
      this.version = version;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setValue(String value) {
      this.value = value;
      return this;
    }

    public Builder setDomain(String domain) {
      this.domain = domain;
      return this;
    }

    public Builder setPath(String path) {
      this.path = path;
      return this;
    }

    public Builder setExpiryDate(Date expiry) {
      this.expiryDate = expiry;
      return this;
    }

    public CookieAdapter build() {

      if(domain == null) {
        throw new IllegalArgumentException("The cookie domain can not be null!");
      }

      return new CookieAdapter(this);
    }
  }
}
