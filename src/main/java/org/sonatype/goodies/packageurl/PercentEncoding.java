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

import com.google.common.base.Charsets;
import com.google.common.net.PercentEscaper;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Percent encoding helper.
 *
 * Handles specific wrinkles related to Package URL specification.
 *
 * @since 1.0.0
 */
final class PercentEncoding
{
  private PercentEncoding() {
    // empty
  }

  private static final String UTF_8 = Charsets.UTF_8.name();

  // Package URL specification indicates that {@code :} and {@code /} are "unambiguous unencoded everywhere"
  private static final String SAFE = "-_.~:/";

  // Package URL specification; via https://en.wikipedia.org/wiki/Percent-encoding; indicates % encoding for space
  private static final PercentEscaper ESCAPER = new PercentEscaper(SAFE, false);

  public static String encode(final String value) {
    checkNotNull(value);
    return ESCAPER.escape(value);
  }

  // NOTE: guava doesn't provide any decoding support, but URLDecoder should properly decode value

  public static String decode(final String value) {
    checkNotNull(value);
    try {
      return URLDecoder.decode(value, UTF_8);
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
