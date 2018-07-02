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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <a href="https://github.com/package-url/purl-spec">Package URL</a>.
 *
 * @see PackageUrl.Builder
 * @since ???
 */
@Immutable
public class PackageUrl
    implements Serializable
{
  private static final long serialVersionUID = 1L;

  private final String type;

  @Nullable
  private final List<String> namespace;

  private final String name;

  @Nullable
  private final String version;

  @Nullable
  private final Map<String, String> qualifiers;

  @Nullable
  private final List<String> subpath;

  @VisibleForTesting
  PackageUrl(final String type,
             @Nullable final List<String> namespace,
             final String name,
             @Nullable final String version,
             @Nullable final Map<String, String> qualifiers,
             @Nullable final List<String> subpath)
  {
    this.type = checkNotNull(type);
    this.namespace = namespace != null ? ImmutableList.copyOf(namespace) : null;
    this.name = checkNotNull(name);
    this.version = version;
    this.qualifiers = qualifiers != null ? ImmutableMap.copyOf(qualifiers) : null;
    this.subpath = subpath != null ? ImmutableList.copyOf(subpath) : null;
  }

  public String getType() {
    return type;
  }

  @Nullable
  public List<String> getNamespace() {
    return namespace;
  }

  /**
   * Return {@link #namespace} as a string representation (unencoded segments).
   */
  @Nullable
  public String getNamespaceAsString() {
    if (namespace != null && !namespace.isEmpty()) {
      return renderSegments(new StringBuilder(), namespace, false).toString();
    }
    return null;
  }

  public String getName() {
    return name;
  }

  @Nullable
  public String getVersion() {
    return version;
  }

  @Nullable
  public Map<String, String> getQualifiers() {
    return qualifiers;
  }

  @Nullable
  public List<String> getSubpath() {
    return subpath;
  }

  /**
   * Return {@link #subpath} as a string representation (unencoded segments).
   */
  @Nullable
  public String getSubpathAsString() {
    if (subpath != null && !subpath.isEmpty()) {
      return renderSegments(new StringBuilder(), subpath, false).toString();
    }
    return null;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackageUrl that = (PackageUrl) o;
    return Objects.equals(type, that.type) &&
        Objects.equals(namespace, that.namespace) &&
        Objects.equals(name, that.name) &&
        Objects.equals(version, that.version) &&
        Objects.equals(qualifiers, that.qualifiers) &&
        Objects.equals(subpath, that.subpath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, namespace, name, version, qualifiers, subpath);
  }

  @VisibleForTesting
  String explain() {
    return "{type='" + type + '\'' +
        ", namespace=" + namespace +
        ", name='" + name + '\'' +
        ", version='" + version + '\'' +
        ", qualifiers=" + qualifiers +
        ", subpath=" + subpath +
        '}';
  }

  /**
   * Convert to a builder for mutation.
   *
   * @see Builder#from(PackageUrl)
   */
  public Builder asBuilder() {
    return new Builder().from(this);
  }

  /**
   * How to render the Package-URL.
   */
  public enum RenderFlavor
  {
    /**
     * Render with {@code pkg:} scheme.
     */
    SCHEME,

    /**
     * Render w/o {@code pkg:} scheme.
     */
    SCHEMELESS;

    private static RenderFlavor _default = SCHEMELESS;

    public static RenderFlavor getDefault() {
      return _default;
    }

    /**
     * Set the default flavor, or null for the original default.
     */
    public static void setDefault(@Nullable final RenderFlavor flavor) {
      _default = flavor == null ? SCHEMELESS : flavor;
    }
  }

  /**
   * Convert to canonical string representation with {@link RenderFlavor#getDefault() default flavor}.
   */
  @Override
  public String toString() {
    return toString(RenderFlavor.getDefault());
  }

  /**
   * Convert to canonical string representation with given rendering flavor.
   */
  public String toString(final RenderFlavor flavor) {
    checkNotNull(flavor);

    StringBuilder buff = new StringBuilder();

    if (flavor == RenderFlavor.SCHEME) {
      buff.append(SCHEME).append(':');
    }

    buff.append(lowerCase(type));

    if (flavor == RenderFlavor.SCHEME) {
      buff.append('/');
    }
    else {
      buff.append(':');
    }

    if (namespace != null && !namespace.isEmpty()) {
      renderSegments(buff, namespace, true);
      buff.append('/');
    }

    buff.append(PercentEncoding.encode(name));

    if (version != null) {
      buff.append('@').append(PercentEncoding.encode(version));
    }

    if (qualifiers != null && !qualifiers.isEmpty()) {
      buff.append('?');
      Iterator<Map.Entry<String, String>> iter = qualifiers.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry<String, String> entry = iter.next();
        buff.append(entry.getKey()).append('=').append(PercentEncoding.encode(entry.getValue()));
        if (iter.hasNext()) {
          buff.append('&');
        }
      }
    }

    if (subpath != null && !subpath.isEmpty()) {
      buff.append('#');
      renderSegments(buff, subpath, true);
    }

    return buff.toString();
  }

  /**
   * Render segments to buffer.
   */
  private static StringBuilder renderSegments(final StringBuilder buff,
                                              final List<String> segments,
                                              final boolean encode)
  {
    Iterator<String> iter = segments.iterator();
    while (iter.hasNext()) {
      String segment = iter.next();
      if (encode) {
        segment = PercentEncoding.encode(segment);
      }
      buff.append(segment);
      if (iter.hasNext()) {
        buff.append('/');
      }
    }
    return buff;
  }

  /**
   * Convert given value to lower-case.
   */
  private static String lowerCase(final String value) {
    return value.toLowerCase(Locale.ENGLISH);
  }

  /**
   * Convert given list of values to lower-case.
   */
  @Nullable
  private static List<String> lowerCase(@Nullable final List<String> values) {
    if (values != null) {
      List<String> result = new ArrayList<>(values.size());
      for (String value : values) {
        result.add(lowerCase(value));
      }
      return result;
    }
    return null;
  }

  //
  // Parsing
  //

  private static final String TYPE = "[a-zA-Z][a-zA-Z\\d.+-]*";

  private static final Pattern TYPE_PATTERN = Pattern.compile(TYPE);

  private static final String NAMESPACE = "[^@#]+";

  private static final String NAMESPACE_SEGMENT = "[^/]+";

  private static final Pattern NAMESPACE_SEGMENT_PATTERN = Pattern.compile(NAMESPACE_SEGMENT);

  private static final String NAME = "[^/@]+";

  private static final Pattern NAME_PATTERN = Pattern.compile(NAME);

  private static final String VERSION = ".+?";

  private static final Pattern VERSION_PATTERN = Pattern.compile(VERSION);

  private static final String QUALIFIER_KEY = "[a-zA-Z.\\-_][a-zA-Z\\d.\\-_]*";

  private static final Pattern QUALIFIER_KEY_PATTERN = Pattern.compile(QUALIFIER_KEY);

  private static final String QUALIFIER_VALUE = "[^&]+?";

  private static final Pattern QUALIFIER_VALUE_PATTERN = Pattern.compile(QUALIFIER_VALUE);

  private static final String QUALIFIER = String.format("%s=%s", QUALIFIER_KEY, QUALIFIER_VALUE);

  private static final String QUALIFIERS = String.format("%s(&%s)*", QUALIFIER, QUALIFIER);

  private static final Splitter.MapSplitter QUALIFIER_SPLITTER = Splitter.on('&').withKeyValueSeparator('=');

  private static final String SUBPATH = ".+";

  private static final String SUBPATH_SEGMENT = "[^/]+";

  private static final Pattern SUBPATH_SEGMENT_PATTERN = Pattern.compile(SUBPATH_SEGMENT);

  private static final Splitter SEGMENT_SPLITTER = Splitter.on('/');

  private static final String SCHEME = "pkg";

  private static final Pattern PURL_SCHEME_PATTERN = Pattern.compile(String.format(
      "%s:(/)*(?<type>%s)/" +
      "((?<namespace>%s)/)?" +
      "(?<name>%s)" +
      "(@(?<version>%s))?" +
      "(\\?(?<qualifiers>%s))?" +
      "(#(?<subpath>%s))?",
      SCHEME, TYPE, NAMESPACE, NAME, VERSION, QUALIFIERS, SUBPATH
  ));

  private static final Pattern PURL_SCHEMELESS_PATTERN = Pattern.compile(String.format(
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
    checkNotNull(value);

    Pattern pattern;
    if (value.startsWith(SCHEME + ":")) {
      pattern = PURL_SCHEME_PATTERN;
    }
    else {
      pattern = PURL_SCHEMELESS_PATTERN;
    }

    Matcher m = pattern.matcher(value);
    if (m.matches()) {
      String type = parseType(m.group("type"));
      List<String> namespace = parseNamespace(m.group("namespace"));
      String name = parseName(m.group("name"));
      String version = parseVersion(m.group("version"));
      Map<String, String> qualifiers = parseQualifiers(m.group("qualifiers"));
      List<String> subpath = parseSubpath(m.group("subpath"));

      // FIXME: need to have some per-type transformation; which is unfortunate but spec requires some special handling per-type
      // FIXME: various type-specific transformation required by specification; very problematic
      switch (type) {
        case "github":
        case "bitbucket":
          name = lowerCase(name);
          namespace = lowerCase(namespace);
          break;

        case "pypi":
          name = name.replace('_', '-');
          name = lowerCase(name);
          break;
      }

      return new PackageUrl(type, namespace, name, version, qualifiers, subpath);
    }

    throw new InvalidException(value);
  }

  /**
   * Parse {@link #type}.
   */
  private static String parseType(final String value) {
    return lowerCase(value);
  }

  /**
   * Parse {@link #namespace} segments.
   */
  @Nullable
  private static List<String> parseNamespace(@Nullable final String value) {
    return parseSegments(value);
  }

  /**
   * Parse {@link #name}.
   */
  private static String parseName(final String value) {
    return PercentEncoding.decode(value);
  }

  /**
   * Parse {@link #version}.
   */
  @Nullable
  private static String parseVersion(@Nullable final String value) {
    if (value != null) {
      return PercentEncoding.decode(value);
    }
    return null;
  }

  /**
   * Parse {@link #qualifiers} map.
   */
  @Nullable
  private static Map<String, String> parseQualifiers(@Nullable final String value) {
    if (value == null) {
      return null;
    }

    Map<String, String> pairs = QUALIFIER_SPLITTER.split(value);
    Map<String, String> result = new LinkedHashMap<>(pairs.size());
    for (Map.Entry<String, String> entry : pairs.entrySet()) {
      String v = entry.getValue();

      // qualifiers with empty values should be skipped
      if (v.isEmpty()) {
        continue;
      }

      String k = lowerCase(entry.getKey());
      result.put(k, PercentEncoding.decode(v));
    }
    return result;
  }

  /**
   * Parse {@link #subpath} segments.
   */
  @Nullable
  private static List<String> parseSubpath(@Nullable final String value) {
    return parseSegments(value, ".", "..");
  }

  /**
   * Parse segments from value.
   */
  @Nullable
  private static List<String> parseSegments(@Nullable final String value, final String... invalids) {
    if (value == null) {
      return null;
    }

    Iterable<String> parts = SEGMENT_SPLITTER.split(stripSlashes(value));
    List<String> result = new ArrayList<>();
    for (String part : parts) {
      if (part.isEmpty()) {
        throw new EmptySegmentException(value);
      }
      part = PercentEncoding.decode(part);

      // decoded segment must not contain a segment separator
      if (part.contains("/")) {
        throw new IllegalSegmentContent("/", value);
      }

      // decoded segment must not contain any invalid values
      for (String invalid : invalids) {
        if (part.equals(invalid)) {
          throw new IllegalSegmentContent(invalid, value);
        }
      }
      result.add(part);
    }

    return result;
  }

  /**
   * Strip leading and trailing {@code /} chars from value.
   */
  private static String stripSlashes(String value) {
    if (value.charAt(0) == '/') {
      value = value.substring(1, value.length());
    }
    if (value.charAt(value.length() - 1) == '/') {
      value = value.substring(0, value.length() - 1);
    }
    return value;
  }

  //
  // Validation
  //

  private static void validate(final String type, final Pattern pattern, final String value) {
    if (!pattern.matcher(value).matches()) {
      throw new InvalidException("Invalid " + type + ": " + value);
    }
  }

  /**
   * Validate {@link #type} value.
   */
  private static void validateType(final String value) {
    if (value == null) {
      throw new MissingComponentException("type");
    }
    validate("type", TYPE_PATTERN, value);
  }

  /**
   * Validate {@link #namespace} value.
   */
  @Nullable
  private static void validateNamespace(@Nullable final List<String> namespace) {
    if (namespace != null) {
      for (String segment : namespace) {
        validate("namespace.segment", NAMESPACE_SEGMENT_PATTERN, segment);
      }
    }
  }

  /**
   * Validate {@link #name} value.
   */
  private static void validateName(final String value) {
    if (value == null) {
      throw new MissingComponentException("name");
    }
    validate("name", NAME_PATTERN, value);
  }

  /**
   * Validate {@link #version} value.
   */
  @Nullable
  private static void validateVersion(@Nullable final String value) {
    if (value != null) {
      validate("version", VERSION_PATTERN, value);
    }
  }

  /**
   * Validate {@link #qualifiers} value.
   */
  @Nullable
  private static void validateQualifiers(@Nullable final Map<String, String> qualifiers) {
    if (qualifiers != null) {
      for (Map.Entry<String, String> entry : qualifiers.entrySet()) {
        validate("qualifier.key", QUALIFIER_KEY_PATTERN, entry.getKey());
        validate("qualifier.value", QUALIFIER_VALUE_PATTERN, entry.getValue());
      }
    }
  }

  /**
   * Validate {@link #subpath} value.
   */
  @Nullable
  private static void validateSubpath(@Nullable final List<String> subpath) {
    if (subpath != null) {
      for (String segment : subpath) {
        validate("subpath.segment", SUBPATH_SEGMENT_PATTERN, segment);
      }
    }
  }

  //
  // Builder
  //

  /**
   * Helper to create a builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * {@link PackageUrl} builder.
   */
  public static class Builder
  {
    private String type;

    private List<String> namespace;

    private String name;

    private String version;

    private Map<String, String> qualifiers;

    private List<String> subpath;

    public Builder from(final PackageUrl purl) {
      checkNotNull(purl);
      this.type = purl.type;
      if (purl.namespace != null) {
        this.namespace = new ArrayList<>(purl.namespace);
      }
      this.name = purl.name;
      this.version = purl.version;
      if (purl.qualifiers != null) {
        this.qualifiers = new LinkedHashMap<>(purl.qualifiers);
      }
      if (purl.subpath != null) {
        this.subpath = new ArrayList<>(purl.subpath);
      }
      return this;
    }

    public Builder type(final String type) {
      this.type = type;
      return this;
    }

    public Builder namespace(final List<String> namespace) {
      this.namespace = namespace;
      return this;
    }

    /**
     * Parse namespace from value.
     */
    public Builder namespace(final String namespace) {
      return namespace(parseNamespace(namespace));
    }

    public Builder name(final String name) {
      this.name = name;
      return this;
    }

    public Builder version(final String version) {
      this.version = version;
      return this;
    }

    public Builder qualifiers(final Map<String, String> qualifiers) {
      this.qualifiers = qualifiers;
      return this;
    }

    /**
     * Parse qualifiers from value.
     */
    public Builder qualifiers(final String qualifiers) {
      return qualifiers(parseQualifiers(qualifiers));
    }

    public Builder qualifer(final String key, final String value) {
      if (qualifiers == null) {
        qualifiers = new LinkedHashMap<>();
      }
      qualifiers.put(key, value);
      return this;
    }

    public Builder subpath(final List<String> subpath) {
      this.subpath = subpath;
      return this;
    }

    /**
     * Parse subpath from value.
     */
    public Builder subpath(final String subpath) {
      return subpath(parseSubpath(subpath));
    }

    /**
     * Build {@link PackageUrl}.
     *
     * At minimal {@link #type} and {@link #name} must be specified.
     */
    public PackageUrl build() {
      validateType(type);
      validateNamespace(namespace);
      validateName(name);
      validateVersion(version);
      validateQualifiers(qualifiers);
      validateSubpath(subpath);

      return new PackageUrl(type, namespace, name, version, qualifiers, subpath);
    }
  }

  //
  // Exceptions
  //

  /**
   * Thrown when package-url is detected to be invalid for some reason.
   */
  public static class InvalidException
      extends RuntimeException
  {
    private InvalidException(final String message) {
      super(message);
    }
  }

  /**
   * Thrown when package-url component that is required is missing.
   */
  public static class MissingComponentException
      extends InvalidException
  {
    private MissingComponentException(final String name) {
      super("Missing required component: " + name);
    }
  }

  /**
   * Thrown when segmented property has an empty value.
   */
  public static class EmptySegmentException
      extends InvalidException
  {
    private EmptySegmentException(final String value) {
      super("Empty segment in: " + value);
    }
  }

  /**
   * Throw when segmented property contains and illegal value.
   */
  public static class IllegalSegmentContent
      extends InvalidException
  {
    private IllegalSegmentContent(final String content, final String value) {
      super("Illegal segment content: " + content + " in: " + value);
    }
  }
}
