---
title: Builder
subtitle: Build a package-url programmatically
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
Construction of a [PackageUrl](../../maven/apidocs/org/sonatype/goodies/packageurl/PackageUrl.html) is done with a
[Builder](../../maven/apidocs/org/sonatype/goodies/packageurl/PackageUrl.Builder.html):

```java
PackageUrl purl = new PackageUrl.Builder()
    .type("maven")
    .namespace("junit")
    .name("junit")
    .version("4.12")
    .build();
```

## Mutation

Mutation is also handled by a builder by converting an existing `PackageUrl` into a `Builder`:

```java
PackageUrl purl = PackageUrl.parse("pkg:maven/junit/junit@4.12");
PackageUrl purlNoVersion = purl.asBuilder().version(null).build();
```