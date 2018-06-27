<!--

    Copyright (c) 2018-present Sonatype, Inc. All rights reserved.

    This program is licensed to you under the Apache License Version 2.0,
    and you may not use this file except in compliance with the Apache License Version 2.0.
    You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.

    Unless required by applicable law or agreed to in writing,
    software distributed under the Apache License Version 2.0 is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.

-->
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

#### pkg scheme

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

#### pkg scheme
    
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

## Site 

### Staging

    mvn -Psite-stage && open target/staging/index.html 

### Publishing

    mvn -Psite-stage && mvn scm-publish:publish-scm