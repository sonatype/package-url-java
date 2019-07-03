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

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

/**
 * {@link PackageUrl} validator.
 *
 * @since 1.1.0
 */
class PackageUrlValidator
{
  private PackageUrlValidator() {
    // empty
  }

  private static final Pattern TYPE_PATTERN = Pattern.compile(PackageUrlParser.TYPE);

  private static final Pattern NAMESPACE_SEGMENT_PATTERN = Pattern.compile(PackageUrlParser.NAMESPACE_SEGMENT);

  private static final Pattern NAME_PATTERN = Pattern.compile(PackageUrlParser.NAME);

  private static final Pattern VERSION_PATTERN = Pattern.compile(PackageUrlParser.VERSION);

  private static final Pattern QUALIFIER_KEY_PATTERN = Pattern.compile(PackageUrlParser.QUALIFIER_KEY);

  private static final Pattern QUALIFIER_VALUE_PATTERN = Pattern.compile(PackageUrlParser.QUALIFIER_VALUE);

  private static final Pattern SUBPATH_SEGMENT_PATTERN = Pattern.compile(PackageUrlParser.SUBPATH_SEGMENT);

  private static void validate(final String type, final Pattern pattern, final String value) {
    if (!pattern.matcher(value).matches()) {
      throw new InvalidException("Invalid " + type + ": " + value);
    }
  }

  /**
   * Validate {@link PackageUrl#type} value.
   */
  static void validateType(final String value) {
    if (value == null) {
      throw new MissingComponentException("type");
    }
    validate("type", TYPE_PATTERN, value);
  }

  /**
   * Validate {@link PackageUrl#namespace} value.
   */
  static void validateNamespace(@Nullable final List<String> namespace) {
    if (namespace != null) {
      for (String segment : namespace) {
        validate("namespace.segment", NAMESPACE_SEGMENT_PATTERN, segment);
      }
    }
  }

  /**
   * Validate {@link PackageUrl#name} value.
   */
  static void validateName(final String value) {
    if (value == null) {
      throw new MissingComponentException("name");
    }
    // FIXME: name gets url encoding so not sure there is validation to do here?
    //validate("name", NAME_PATTERN, value);
  }

  /**
   * Validate {@link PackageUrl#version} value.
   */
  static void validateVersion(@Nullable final String value) {
    if (value != null) {
      validate("version", VERSION_PATTERN, value);
    }
  }

  /**
   * Validate {@link PackageUrl#qualifiers} value.
   */
  static void validateQualifiers(@Nullable final Map<String, String> qualifiers) {
    if (qualifiers != null) {
      for (Map.Entry<String, String> entry : qualifiers.entrySet()) {
        validate("qualifier.key", QUALIFIER_KEY_PATTERN, entry.getKey());
        // FIXME: value is precent encoding, so not sure there is validation to do here
        //validate("qualifier.value", QUALIFIER_VALUE_PATTERN, entry.getValue());
      }
    }
  }

  /**
   * Validate {@link PackageUrl#subpath} value.
   */
  static void validateSubpath(@Nullable final List<String> subpath) {
    if (subpath != null) {
      for (String segment : subpath) {
        validate("subpath.segment", SUBPATH_SEGMENT_PATTERN, segment);
      }
    }
  }
}
