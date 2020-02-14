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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * {@link PackageUrl} parser.
 *
 * @since 1.1.0
 */
class PackageUrlParser
{
  private PackageUrlParser() {
    // empty
  }

  static final String TYPE = "[a-zA-Z][a-zA-Z\\d.+-]*";

  static final String NAMESPACE = "[^@#?]+";

  static final String NAMESPACE_SEGMENT = "[^/]+";

  static final String NAME = "[^/@#?]+";

  static final String VERSION = ".+?";

  static final String QUALIFIER_KEY = "[a-zA-Z.\\-_][a-zA-Z\\d.\\-_]*";

  static final String QUALIFIER_VALUE = "[^&]+?";

  static final String QUALIFIER = String.format("%s=%s", QUALIFIER_KEY, QUALIFIER_VALUE);

  static final String QUALIFIERS = String.format("%s(&%s)*", QUALIFIER, QUALIFIER);

  static final String SUBPATH = ".+";

  static final String SUBPATH_SEGMENT = "[^/]+";

  static final Pattern PURL_SCHEME_PATTERN = Pattern.compile(String.format(
      "%s:(/)*(?<type>%s)/" +
      "((?<namespace>%s)/)?" +
      "(?<name>%s)" +
      "(@(?<version>%s))?" +
      "(\\?(?<qualifiers>%s))?" +
      "(#(?<subpath>%s))?",
      PackageUrl.SCHEME, TYPE, NAMESPACE, NAME, VERSION, QUALIFIERS, SUBPATH
  ));

  static final Pattern PURL_SCHEMELESS_PATTERN = Pattern.compile(String.format(
      "(?<type>%s):(//)?" +
      "((?<namespace>%s)/)?" +
      "(?<name>%s)" +
      "(@(?<version>%s))?" +
      "(\\?(?<qualifiers>%s))?" +
      "(#(?<subpath>%s))?",
      TYPE, NAMESPACE, NAME, VERSION, QUALIFIERS, SUBPATH
  ));

  /**
   * Parse package-url from given value.
   *
   * Value format: {@code type:namespace/name@version?qualifiers#subpath}
   */
  public static PackageUrl parse(final String value) {
    requireNonNull(value);

    Pattern pattern;
    if (value.startsWith(PackageUrl.SCHEME + ":")) {
      pattern = PURL_SCHEME_PATTERN;
    }
    else {
      pattern = PURL_SCHEMELESS_PATTERN;
    }

    Matcher m = pattern.matcher(value);
    if (m.matches()) {
      return new PackageUrlBuilder()
          .type(parseType(m.group("type")))
          .namespace(parseNamespace(m.group("namespace")))
          .name(parseName(m.group("name")))
          .version(parseVersion(m.group("version")))
          .qualifiers(parseQualifiers(m.group("qualifiers")))
          .subpath(parseSubpath(m.group("subpath")))
          .buildAndValidate(false);
    }

    throw new InvalidException(value);
  }

  /**
   * Parse {@link PackageUrl#type}.
   */
  static String parseType(final String value) {
    return MoreStrings.lowerCase(value);
  }

  /**
   * Parse {@link PackageUrl#namespace} segments.
   */
  @Nullable
  static List<String> parseNamespace(@Nullable final String value) {
    return parseSegments(value);
  }

  /**
   * Parse {@link PackageUrl#name}.
   */
  static String parseName(final String value) {
    return PercentEncoding.decode(value);
  }

  /**
   * Parse {@link PackageUrl#version}.
   */
  @Nullable
  static String parseVersion(@Nullable final String value) {
    if (value != null) {
      return PercentEncoding.decode(value);
    }
    return null;
  }

  /**
   * Parse {@link PackageUrl#qualifiers} map.
   */
  @Nullable
  static Map<String, String> parseQualifiers(@Nullable final String value) {
    if (value == null) {
      return null;
    }

    String[] pairs = value.split("&");
    Map<String, String> result = new LinkedHashMap<>(pairs.length);
    for (String pair : pairs) {
      String[] split = pair.split("=", 2); // Splits the pair into either one or two pieces
      if (split.length == 1) {
        // qualifiers with missing values should be skipped
        continue;
      }
      String k = split[0];
      String v = split[1];
      if (MoreStrings.isBlank(v)) {
        // qualifiers with empty values should be skipped
        continue;
      }

      result.put(k, PercentEncoding.decode(v));
    }

    return result.isEmpty() ? null : result;
  }

  /**
   * Parse {@link PackageUrl#subpath} segments.
   */
  @Nullable
  static List<String> parseSubpath(@Nullable final String value) {
    return parseSegments(value, ".", "..");
  }

  /**
   * Parse segments from value.
   */
  @Nullable
  static List<String> parseSegments(@Nullable final String value, final String... invalids) {
    if (value == null) {
      return null;
    }

    String[] parts = stripSlashes(value).split("/");
    List<String> result = new ArrayList<>();
    for (String part : parts) {
      if (part.isEmpty()) {
        throw new EmptySegmentException(value);
      }
      part = PercentEncoding.decode(part);

      // decoded segment must not contain a segment separator
      if (part.contains("/")) {
        throw new IllegalSegmentContentException("/", value);
      }

      // decoded segment must not contain any invalid values
      for (String invalid : invalids) {
        if (part.equals(invalid)) {
          throw new IllegalSegmentContentException(invalid, value);
        }
      }
      result.add(part);
    }

    return result.isEmpty() ? null : result;
  }

  /**
   * Strip leading and trailing {@code /} chars from value.
   */
  private static String stripSlashes(String value) {
    if (value.charAt(0) == '/') {
      value = value.substring(1);
    }
    if (value.charAt(value.length() - 1) == '/') {
      value = value.substring(0, value.length() - 1);
    }
    return value;
  }
}
