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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <a href="https://github.com/package-url/purl-spec">Package URL</a>.
 *
 * @since ???
 *
 * @see PackageUrl.Builder
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
        // FIXME: need to have some per-type transformation; which is unfortunate but spec requires some special handling per-type

        this.type = checkNotNull(type);
        this.namespace = namespace != null ? ImmutableList.copyOf(namespace) : null;
        this.name = checkNotNull(name);
        this.version = version;
        this.qualifiers = qualifiers != null ? ImmutableMap.copyOf(qualifiers): null;
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
     * Return {@link #namespace} as a string representation.
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
     * Return {@link #subpath} as a string representation.
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();

        buff.append(lowerCase(type)).append(':');
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
    private static StringBuilder renderSegments(final StringBuilder buff, final List<String> segments, final boolean encode) {
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

    private static String lowerCase(final String value) {
        return value.toLowerCase(Locale.ENGLISH);
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

    private static final Pattern PURL_PATTERN = Pattern.compile(String.format(
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
     * Value format: {@code type:namespace/name@version?qualifiers#subpath)
     */
    public static PackageUrl parse(final String value) {
        checkNotNull(value);

        Matcher m = PURL_PATTERN.matcher(value);
        if (m.matches()) {
            String type = lowerCase(m.group("type"));

            List<String> namespace = parseNamespace(m.group("namespace"));

            String name = PercentEncoding.decode(m.group("name"));

            String version = m.group("version");
            if (version != null) {
                version = PercentEncoding.decode(version);
            }

            Map<String, String> qualifiers = parseQualifiers(m.group("qualifiers"));

            List<String> subpath = parseSubpath(m.group("subpath"));

            return new PackageUrl(type, namespace, name, version, qualifiers, subpath);
        }
        throw new InvalidException(value);
    }

    /**
     * Parse {@link #qualifiers} map.
     */
    @Nullable
    private static Map<String, String> parseQualifiers(@Nullable final String value) {
        if (value == null) {
            return null;
        }

        Map<String,String> pairs = QUALIFIER_SPLITTER.split(value);
        Map<String, String> result = new LinkedHashMap<>(pairs.size());
        for (Map.Entry<String,String> entry : pairs.entrySet()) {
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
     * Parse {@link #namespace} segments.
     */
    @Nullable
    private static List<String> parseNamespace(@Nullable final String value) {
        return parseSegments(value);
    }

    /**
     * Parse {@link #subpath} segments.
     */
    @Nullable
    private static List<String> parseSubpath(@Nullable final String value) {
        return parseSegments(value,".", "..");
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

    private static void validateType(final String value) {
        if (value == null) {
            throw new MissingComponentException("type");
        }
        validate("type", TYPE_PATTERN, value);
    }

    @Nullable
    private static void validateNamespace(@Nullable final List<String> namespace) {
        if (namespace != null) {
            for (String segment : namespace) {
                validate("namespace.segment", NAMESPACE_SEGMENT_PATTERN, segment);
            }
        }
    }

    private static void validateName(final String value) {
        if (value == null) {
            throw new MissingComponentException("name");
        }
        validate("name", NAME_PATTERN, value);
    }

    @Nullable
    private static void validateVersion(@Nullable final String value) {
        if (value != null) {
            validate("version", VERSION_PATTERN, value);
        }
    }

    @Nullable
    private static void validateQualifiers(@Nullable final Map<String,String> qualifiers) {
        if (qualifiers != null) {
            for (Map.Entry<String, String> entry : qualifiers.entrySet()) {
                validate("qualifier.key", QUALIFIER_KEY_PATTERN, entry.getKey());
                validate("qualifier.value", QUALIFIER_VALUE_PATTERN, entry.getValue());
            }
        }
    }

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
     * {@link PackageUrl} builder.
     */
    public static class Builder
    {
        private String type;

        private List<String> namespace;

        private String name;

        private String version;

        private Map<String,String> qualifiers;

        private List<String> subpath;

        public Builder type(final String type) {
            this.type = type;
            return this;
        }

        public Builder namespace(final List<String> namespace) {
            this.namespace = namespace;
            return this;
        }

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

        public Builder qualifiers(final Map<String,String> qualifiers) {
            this.qualifiers = qualifiers;
            return this;
        }

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
