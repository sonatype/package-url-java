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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

/**
 * String helpers.
 *
 * @since 1.1.0
 */
final class MoreStrings
{
  private MoreStrings() {
    // empty
  }

  /**
   * Convert given value to lower-case.
   */
  static String lowerCase(final String value) {
    return value.toLowerCase(Locale.ENGLISH);
  }

  /**
   * Convert given list of values to lower-case.
   */
  @Nullable
  static List<String> lowerCase(@Nullable final List<String> values) {
    if (values != null) {
      List<String> result = new ArrayList<>(values.size());
      for (String value : values) {
        result.add(lowerCase(value));
      }
      return result;
    }
    return null;
  }
}
