---
title: Render
subtitle: Render a package-url to a string
glyph: fab fa-java

draft: false

menu:
  topnav:
    parent: Usage
---
Rendering to a string supports 2 options.  With `pkg` scheme or scheme-less.

This behavior is controlled by [RenderFlavor](../../apidocs/org/sonatype/goodies/packageurl/PackageUrl.RenderFlavor.html).

The *default* behavior can be installed JVM-wide, or can be specified when calling [toString(RenderFlavor)](../../apidocs/org/sonatype/goodies/packageurl/PackageUrl.html#toString-org.sonatype.goodies.packageurl.PackageUrl.RenderFlavor-).

## `pkg` scheme

```java
PackageUrl purl = PackageUrl.parse("maven:junit/junit@4.12");
System.out.println(purl.toString(PackageUrl.RenderFlavor.SCHEME));
```

Or set the default:

```java
PackageUrl.RenderFlavor.setDefault(PackageUrl.RenderFlavor.SCHEME);
System.out.println(purl.toString());
```
    
## scheme-less

```java
PackageUrl purl = PackageUrl.parse("maven:junit/junit@4.12");
System.out.println(purl.toString(PackageUrl.RenderFlavor.SCHEMELESS));
```

Or set the default:

```java
PackageUrl.RenderFlavor.setDefault(PackageUrl.RenderFlavor.SCHEMELESS);
System.out.println(purl.toString());
```
