# Sonatype Goodies - Package URL

Implementation of [Package URL](https://github.com/package-url/purl-spec) specification for Java.

## Usage

### Maven

    <dependency>
      <groupId>org.sonatype.goodies</groupId>
      <artifactId>package-url-java</artifactId>
      <version>1-SNAPSHOT</version>
    </dependency>

### Parsing

#### `pkg:` scheme

    PackageUrl purl = PackageUrl.parse("pkg:maven/junit/junit@4.12");

#### scheme-less

    PackageUrl purl = PackageUrl.parse("maven:junit/junit@4.12");

### Builder

    PackageUrl purl = new PackageUrl.Builder()
        .type("maven")
        .namespace("junit")
        .name("junit")
        .version("4.12")
        .build();

### Mutation
    
    PackageUrl purl = PackageUrl.parse("maven:junit/junit@4.12");
    PackageUrl purlNoVersion = purl.asBuilder().version(null).build();

## Rendering

#### `pkg:` scheme
    
    PackageUrl purl = PackageUrl.parse("maven:junit/junit@4.12");
    System.out.println(purl.toString(PackageUrl.RenderFlavor.SCHEME));

Or set the default:

    PackageUrl.RenderFlavor.setDefault(PackageUrl.RenderFlavor.SCHEME);
    System.out.println(purl.toString());
    
#### scheme-less

    PackageUrl purl = PackageUrl.parse("maven:junit/junit@4.12");
    System.out.println(purl.toString(PackageUrl.RenderFlavor.SCHEMELESS));

Or set the default:

    PackageUrl.RenderFlavor.setDefault(PackageUrl.RenderFlavor.SCHEMELESS);
    System.out.println(purl.toString());
