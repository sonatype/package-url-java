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
# Builder

Construction of a [PackageUrl](apidocs/org/sonatype/goodies/packageurl/PackageUrl.html) is done with a
[Builder](apidocs/org/sonatype/goodies/packageurl/PackageUrl.Builder.html):

    PackageUrl purl = new PackageUrl.Builder()
        .type("maven")
        .namespace("junit")
        .name("junit")
        .version("4.12")
        .build();

## Mutation

Mutation is also handled by a builder by converting an existing `PackageUrl` into a `Builder`:

    PackageUrl purl = PackageUrl.parse("maven:junit/junit@4.12");
    PackageUrl purlNoVersion = purl.asBuilder().version(null).build();
