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

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

/**
 * Package URL specification tests for scheme-less.
 *
 * This is the older specification w/o {@code pkg:}.
 *
 * @see TestSuiteData
 */
class SchemelessSpecTest
    extends TestSupport
{
  static List<TestSuiteData.Entry> entries

  @BeforeClass
  static void 'load test-suite-data entries'() {
    entries = TestSuiteData.get('schemeless-data.json')
    RenderFlavor.default = RenderFlavor.SCHEMELESS
  }

  @AfterClass
  static void 'reset render-flavor'() {
    RenderFlavor.default = null
  }

  @Test
  void 'test parsing'() {
    entries.each { entry ->
      log "Entry: $entry"
      try {
        PackageUrl purl = PackageUrl.parse(entry.purl)
        log "PURL: ${purl.explain()} -> $purl"

        assert purl.toString() == entry.canonical_purl
        assert purl.type == entry.type
        assert purl.namespaceAsString == entry.namespace
        assert purl.name == entry.name
        assert purl.version == entry.version
        assert purl.qualifiers == entry.qualifiers
        assert purl.subpathAsString == entry.subpath
      }
      catch (e) {
        if (!entry.is_invalid) {
          throw e
        }
        // expected
      }
    }
  }

  @Test
  void 'test building'() {
    entries.each { entry ->
      log "Entry: $entry"
      PackageUrl purl
      try {
        purl = new PackageUrlBuilder()
            .type(entry.type)
            .namespace(entry.namespace)
            .name(entry.name)
            .version(entry.version)
            .qualifiers(entry.qualifiers)
            .subpath(entry.subpath)
            .build()
        log "PURL: ${purl.explain()} -> $purl"

        // puke if we built a PURL that was meant to be invalid
        assert !entry.is_invalid

        // if valid, ensure canonical form matches
        def canonical = PackageUrl.parse(entry.canonical_purl)

        // object match, as toString form could be different due to test-suite-data qualifier ordering
        assert purl == canonical
      }
      catch (e) {
        assert entry.is_invalid
      }
    }
  }
}
