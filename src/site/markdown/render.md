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
# Render

Rendering to a string supports 2 options.  With `pkg` scheme or scheme-less.

This behavior is controlled by [RenderFlavor](apidocs/org/sonatype/goodies/packageurl/PackageUrl.RenderFlavor.html).

The *default* behavior can be installed JVM-wide, or can be specified when calling [toString(RenderFlavor)](apidocs/org/sonatype/goodies/packageurl/PackageUrl.html#toString-org.sonatype.goodies.packageurl.PackageUrl.RenderFlavor-).

## `pkg` scheme
    
    PackageUrl purl = PackageUrl.parse("maven:junit/junit@4.12");
    System.out.println(purl.toString(PackageUrl.RenderFlavor.SCHEME));

Or set the default:

    PackageUrl.RenderFlavor.setDefault(PackageUrl.RenderFlavor.SCHEME);
    System.out.println(purl.toString());
    
## scheme-less

    PackageUrl purl = PackageUrl.parse("maven:junit/junit@4.12");
    System.out.println(purl.toString(PackageUrl.RenderFlavor.SCHEMELESS));

Or set the default:

    PackageUrl.RenderFlavor.setDefault(PackageUrl.RenderFlavor.SCHEMELESS);
    System.out.println(purl.toString());
