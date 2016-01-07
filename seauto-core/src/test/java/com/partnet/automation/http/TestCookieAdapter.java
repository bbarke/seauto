package com.partnet.automation.http;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Cookie;

import java.util.Date;

/**
 * @author bbarker@part.net
 * @since 12/22/15
 */
public class TestCookieAdapter {

  @Test
  public void test_cookieAdapterToSelenium() {

    String domain = "blah-domain";
    Date date = new Date();
    date.setTime(1450820991000L);

    String name = "cookie-name-blah";
    String path = "/to/no/where";
    String value = "jibberish23232342fafa";
    int version = 55;

    CookieAdapter cAdapt = new CookieAdapter.Builder()
        .setDomain(domain)
        .setExpiryDate(date)
        .setName(name)
        .setPath(path)
        .setValue(value)
        .setVersion(55)
        .build();

    Cookie selCookie = cAdapt.getSeleniumCookie();

    Assert.assertEquals(domain, selCookie.getDomain());
    Assert.assertEquals(cAdapt.getExpiryDate().getTime(), selCookie.getExpiry().getTime());
    Assert.assertEquals(name, selCookie.getName());
    Assert.assertEquals(path, selCookie.getPath());
    Assert.assertEquals(value, selCookie.getValue());

  }
}
