---
title: Parsing
subtitle: Parse package-url from a string
glyph: fab fa-java

draft: false

menu:
  topnav:
    parent: Usage

categories:
  - usage
tags:
  - package-url-java
---
Parsing from string with [parse(String)](../../maven/apidocs/org/sonatype/goodies/packageurl/PackageUrl.html#parse-java.lang.String-)
supports 2 options.  With `pkg` scheme or scheme-less.

## `pkg` scheme

Preferred latest form of the specification.

```java
PackageUrl purl = PackageUrl.parse("pkg:maven/junit/junit@4.12");
```

## scheme-less

Backwards compatible parsing for pre-`pkg`-scheme.

```java
PackageUrl purl = PackageUrl.parse("maven:junit/junit@4.12");
```