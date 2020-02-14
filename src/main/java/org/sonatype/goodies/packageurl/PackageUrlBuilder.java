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
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.util.Objects.requireNonNull;
import static org.sonatype.goodies.packageurl.PackageUrlParser.parseNamespace;
import static org.sonatype.goodies.packageurl.PackageUrlParser.parseQualifiers;
import static org.sonatype.goodies.packageurl.PackageUrlParser.parseSubpath;
import static org.sonatype.goodies.packageurl.PackageUrlValidator.validateName;
import static org.sonatype.goodies.packageurl.PackageUrlValidator.validateNamespace;
import static org.sonatype.goodies.packageurl.PackageUrlValidator.validateQualifiers;
import static org.sonatype.goodies.packageurl.PackageUrlValidator.validateSubpath;
import static org.sonatype.goodies.packageurl.PackageUrlValidator.validateType;
import static org.sonatype.goodies.packageurl.PackageUrlValidator.validateVersion;

/**
 * {@link PackageUrl} builder.
 *
 * @since 1.1.0
 */
public class PackageUrlBuilder
{
  private String type;

  private List<String> namespace;

  private String name;

  private String version;

  private Map<String, String> qualifiers;

  private List<String> subpath;

  public PackageUrlBuilder from(final PackageUrl purl) {
    requireNonNull(purl);
    this.type = purl.getType();
    if (purl.getNamespace() != null) {
      this.namespace = new ArrayList<>(purl.getNamespace());
    }
    this.name = purl.getName();
    this.version = purl.getVersion();
    if (purl.getQualifiers() != null) {
      this.qualifiers = new LinkedHashMap<>(purl.getQualifiers());
    }
    if (purl.getSubpath() != null) {
      this.subpath = new ArrayList<>(purl.getSubpath());
    }
    return this;
  }

  public PackageUrlBuilder type(final String type) {
    this.type = type;
    return this;
  }

  public PackageUrlBuilder namespace(final List<String> namespace) {
    this.namespace = namespace;
    return this;
  }

  public PackageUrlBuilder namespace(final String namespace) {
    return namespace(parseNamespace(namespace));
  }

  public PackageUrlBuilder name(final String name) {
    this.name = name;
    return this;
  }

  public PackageUrlBuilder version(final String version) {
    this.version = version;
    return this;
  }

  private Map<String, String> getQualifiers() {
    if (qualifiers == null) {
      qualifiers = new LinkedHashMap<>();
    }
    return qualifiers;
  }

  public PackageUrlBuilder qualifiers(final Map<String, String> qualifiers) {
    if (qualifiers != null) {
      for (Entry<String, String> entry : qualifiers.entrySet()) {
        qualifier(entry.getKey(), entry.getValue());
      }
    }
    else {
      this.qualifiers = null;
    }
    return this;
  }

  public PackageUrlBuilder qualifiers(final String qualifiers) {
    return qualifiers(parseQualifiers(qualifiers));
  }

  public PackageUrlBuilder qualifier(final String key, final String value) {
    requireNonNull(key);
    requireNonNull(value);
    getQualifiers().put(key, value);
    return this;
  }

  public PackageUrlBuilder subpath(final List<String> subpath) {
    this.subpath = subpath;
    return this;
  }

  /**
   * Parse subpath from value.
   */
  public PackageUrlBuilder subpath(final String subpath) {
    return subpath(parseSubpath(subpath));
  }

  /**
   * Build {@link PackageUrl}.
   *
   * At minimal {@link #type} and {@link #name} must be specified.
   */
  public PackageUrl build() {
    return buildAndValidate(true);
  }

  /**
   * Build and optionally validate.
   *
   * Non-validate case is for parsed usage only.
   *
   * @since 1.0.1
   */
  PackageUrl buildAndValidate(final boolean validate) {
    if (validate) {
      validateType(type);
      validateNamespace(namespace);
      validateName(name);
      validateVersion(version);
      validateQualifiers(qualifiers);
      validateSubpath(subpath);
    }

    // FIXME: need to have some per-type transformation; which is unfortunate but spec requires some special handling per-type
    // FIXME: various type-specific transformation required by specification; very problematic
    // FIXME: https://github.com/package-url/purl-spec/issues/38
    List<String> correctedNamespace = namespace;
    String correctedName = name;
    switch (type) {
      case "github":
      case "bitbucket":
        correctedNamespace = MoreStrings.lowerCase(namespace);
        correctedName = MoreStrings.lowerCase(name);
        break;

      case "pypi":
        correctedName = name.replace('_', '-');
        correctedName = MoreStrings.lowerCase(correctedName);
        break;
    }

    SortedMap<String, String> correctedQualifiers = null;
    if (qualifiers != null) {
      correctedQualifiers = new TreeMap<>();
      for (Entry<String, String> entry : qualifiers.entrySet()) {
         if (!MoreStrings.isBlank(entry.getValue())) {
          correctedQualifiers.put(MoreStrings.lowerCase(entry.getKey()), entry.getValue());
         }
      }
    }

    return new PackageUrl(type, correctedNamespace, correctedName, version, correctedQualifiers, subpath);
  }
}
