/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.packageurl

import org.sonatype.goodies.testsupport.TestSupport

import org.junit.Test

/**
 * {@link PackageUrl} tests.
 */
class PackageUrlTest
    extends TestSupport
{
  @Test
  void 'render as string'() {
    def assertRendering = { final PackageUrl purl, final String expected ->
      log "PURL: ${purl.explain()} -> $purl"
      assert purl.toString() == expected
    }

    assertRendering new PackageUrl('foo', ['bar'], 'baz', 'qux', [a: 'b', c: 'd'] as TreeMap, ['blah']),
        'pkg:foo/bar/baz@qux?a=b&c=d#blah'

    assertRendering new PackageUrlBuilder()
        .type('foo')
        .namespace('ns1/ns2')
        .name('n')
        .version('1.2.3')
        .qualifier('a', 'b')
        .qualifier('c', 'd')
        .subpath('sp1/sp2')
        .build(),
        'pkg:foo/ns1/ns2/n@1.2.3?a=b&c=d#sp1/sp2'

    assertRendering new PackageUrlBuilder()
        .type('foo')
        .namespace('ns1/!ns2')
        .name('n')
        .version('1.2.3')
        .qualifier('a', 'b')
        .qualifier('c', 'd')
        .subpath('sp1/!sp2')
        .build(),
        'pkg:foo/ns1/%21ns2/n@1.2.3?a=b&c=d#sp1/%21sp2'

    assertRendering new PackageUrl('foo', (List) null, 'bar', 'baz', null, (List) null),
        'pkg:foo/bar@baz'

    assertRendering new PackageUrl('foo', ['bar'], 'baz', 'qux', null, null),
        'pkg:foo/bar/baz@qux'

    assertRendering new PackageUrl('foo', null, 'bar', 'baz', null, ['qux']),
        'pkg:foo/bar@baz#qux'
  }

  @Test
  void 'render qualifiers lexicographically'() {
    def assertRendering = { final PackageUrl purl, final String expected ->
      log "PURL: ${purl.explain()} -> $purl"
      assert purl.toString() == expected
    }

    assertRendering new PackageUrlBuilder()
        .type('foo')
        .name('n')
        .version('1.2.3')
        .qualifier('c', 'd')
        .qualifier('a', 'b')
        .build(),
        'pkg:foo/n@1.2.3?a=b&c=d'
  }
}
