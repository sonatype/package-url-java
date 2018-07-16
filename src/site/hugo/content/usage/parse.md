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
Parsing from string supports 2 options.  With `pkg` scheme or scheme-less.

## `pkg` scheme

```java
PackageUrl purl = PackageUrl.parse("pkg:maven/junit/junit@4.12");
```

## scheme-less

```java
PackageUrl purl = PackageUrl.parse("maven:junit/junit@4.12");
```