/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.packageurl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

/**
 * Percent encoding helper.
 *
 * Handles specific wrinkles related to Package URL specification.
 *
 * Specification indicates that {@code :} and {@code /} are "unambiguous unencoded everywhere".
 *
 * Specification; via https://en.wikipedia.org/wiki/Percent-encoding; indicates % encoding for space.
 *
 * @since 1.0.0
 */
final class PercentEncoding
{
  private PercentEncoding() {
    // empty
  }

  private static final String UTF_8 = StandardCharsets.UTF_8.name();

  public static String encode(final String value) {
    requireNonNull(value);
    try {
      return URLEncoder.encode(value, UTF_8)
          .replace("+", "%20")
          .replace("%3A", ":")
          .replace( "%2F", "/");
    }
    catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static String encodeName(final String value) {
    requireNonNull(value);
    try {
      return URLEncoder.encode(value, UTF_8)
          .replace("+", "%20")
          .replace("%3A", ":");
    }
    catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static String encodeVersion(final String value) {
    return encode(value);
  }

  public static String encodeSegment(final String value) {
    // TODO: this may need to have same treatment as name?
    return encode(value);
  }

  public static String encodeQualifierValue(final String value) {
    return encode(value);
  }

  public static String decode(final String value) {
    requireNonNull(value);
    try {
      return URLDecoder.decode(value, UTF_8);
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
