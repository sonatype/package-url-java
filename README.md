# Sonatype Goodies - Package URL

Implementation of [Package URL](https://github.com/package-url/purl-spec) specification for Java.

## Usage

### Parsing

    PackageUrl purl = PackageUrl.parse("maven:junit/junit@4.12");

### Builder

    PackageUrl purl = new PackageUrl.Builder()
        .type("maven")
        .namespace("junit")
        .name("junit")
        .version("4.12")
        .build();