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

![License](https://img.shields.io/github/license/sonatype/package-url-java.svg?label=License)

Implementation of [Package URL](https://github.com/package-url/purl-spec) specification for Java.

## Building

### Requirements

* Apache Maven 3.3+
* JDK 7+ (10 is **NOT** supported)

### Build

    mvn clean install

## Site 

### Setup

Prepare `gh-pages` branch:

    git@github.com:jdillon/dionysus-example.git gh-pages
    cd gh-pages
    git co --orphan gh-pages
    rm -rf * .gitignore
    touch index.html
    git add index.html
    git ci -a -m "initial"
    git push origin gh-pages

### Building

    ./mvnw clean install dionysus:build
    
### Publishing

    ./mvnw dionysus:publish
