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
import java.nio.charset.StandardCharsets;

import com.google.common.net.PercentEscaper;

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

  private static final PercentEscaper ESCAPER = new PercentEscaper("-_.~:/", false);

  /**
   * Name has some wrinkles and non-clarity about the specification for encoding.  As the {@code /} is used
   * as a separator but its also "unambiguous unencoded everywhere".
   */
  private static final PercentEscaper NAME_ESCAPER = new PercentEscaper("-_.~:", false);

  public static String encode(final String value) {
    requireNonNull(value);
    return ESCAPER.escape(value);
  }

  public static String encodeName(final String value) {
    requireNonNull(value);
    return NAME_ESCAPER.escape(value);
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

  // NOTE: guava doesn't provide any decoding support, but URLDecoder should properly decode value

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
