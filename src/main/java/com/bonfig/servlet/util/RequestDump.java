/*
 * Copyright 2017 Bonfig GmbH
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
package com.bonfig.servlet.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Dumps the request header, parameter, cookies, attributes and properties of a <code>HttpServletRequest</code> for
 * diagnostic purposes.
 *
 * @author Dipl.-Ing. Robert C. Bonfig
 */
public class RequestDump {

  private final HttpServletRequest request;
  private final StringBuilder builder;

  /**
   * Dumps the request header, parameter, cookies, attributes and properties of a <code>HttpServletRequest</code> for
   * diagnostic purposes.
   *
   * @param request the request to dump
   * @return a <code>String</code> containing the dump of the request
   */
  public static String dump(final HttpServletRequest request) {
    return new RequestDump(request).dump();
  }

  private RequestDump(final HttpServletRequest request) {
    Objects.requireNonNull(request);
    this.request = request;
    builder = new StringBuilder(2048);
  }

  private String dump() {
    append("Header: ", request.getHeaderNames(), String::toString, request::getHeader);
    append("Parameter:", request.getParameterNames(), String::toString, name -> {
      final String[] values = request.getParameterValues(name);
      return values.length == 1 ? values[0] : Arrays.toString(values);
    });
    append("Cookies: ", request.getCookies(), Cookie::getName, Cookie::getValue);
    append("Attributes: ", request.getAttributeNames(), String::toString, request::getAttribute);

    append("Properties:");
    append("asyncStarted", request.isAsyncStarted());
    append("asyncSupported", request.isAsyncSupported());
    append("authType", request.getAuthType());
    append("characterEncoding", request.getCharacterEncoding());
    append("contentLength", request.getContentLength());
    append("contentType", request.getContentType());
    append("contextPath", request.getContextPath());
    append("dispatcherType", request.getDispatcherType());
    append("localAddr", request.getLocalAddr());
    append("localName", request.getLocalName());
    append("localPort", request.getLocalPort());
    append("locale", request.getLocale());
    append("method", request.getMethod());
    append("pathInfo", request.getPathInfo());
    append("pathTranslated", request.getPathTranslated());
    append("protocol", request.getProtocol());
    append("queryString", request.getQueryString());
    append("remoteAddr", request.getRemoteAddr());
    append("remoteHost", request.getRemoteHost());
    append("remotePort", request.getRemotePort());
    append("remoteUser", request.getRemoteUser());
    append("requestedSessionId", request.getRequestedSessionId());
    append("requestedSessionIdFromCookie", request.isRequestedSessionIdFromCookie());
    append("requestedSessionIdFromURL", request.isRequestedSessionIdFromURL());
    append("requestedSessionIdValid", request.isRequestedSessionIdValid());
    append("requestURI", request.getRequestURI());
    append("scheme", request.getScheme());
    append("secure", request.isSecure());
    append("serverName", request.getServerName());
    append("serverPort", request.getServerPort());
    append("servletPath", request.getServletPath());

    return builder.toString();
  }

  private void append(final Object value) {
    builder.append(String.format("%s%n", value));
  }

  private void append(final Object name, final Object value) {
    builder.append(String.format("%-30s = %s%n", name, value));
  }

  private <T, U extends Comparable<? super U>> void append(String header, Enumeration<T> source,
      Function<? super T, ? extends U> nameExtractor, Function<? super T, ?> valueExtractor) {
    if (source != null && source.hasMoreElements()) {
      append(header, Collections.list(source).stream(), nameExtractor, valueExtractor);
    }
  }

  private <T, U extends Comparable<? super U>> void append(String header, T[] source,
      Function<? super T, ? extends U> nameExtractor, Function<? super T, ?> valueExtractor) {
    if (source != null && source.length > 0) {
      append(header, Stream.of(source), nameExtractor, valueExtractor);
    }
  }

  private <T, U extends Comparable<? super U>> void append(String header, Stream<T> stream,
      Function<? super T, ? extends U> nameExtractor, Function<? super T, ?> valueExtractor) {
    append(header);
    stream.sorted(Comparator.comparing(nameExtractor))
        .forEach(element -> append(nameExtractor.apply(element), valueExtractor.apply(element)));
    append("");
  }

}
