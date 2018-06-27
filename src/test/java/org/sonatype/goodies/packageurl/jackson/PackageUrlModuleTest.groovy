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
package org.sonatype.goodies.packageurl.jackson

import org.sonatype.goodies.packageurl.PackageUrl
import org.sonatype.goodies.testsupport.TestSupport

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.ToString
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link PackageUrlModule}.
 */
class PackageUrlModuleTest
    extends TestSupport
{
  private ObjectMapper objectMapper

  @Before
  void setUp() {
    objectMapper = new ObjectMapper()
        .registerModule(new PackageUrlModule())
  }

  private String fixture(final String path) {
    def url = getClass().getResource('/' + path)
    assert url != null
    return url.text
  }

  @Test
  void 'serialize single'() {
    def purl = 'maven:foo/bar@1'
    def value = PackageUrl.parse(purl)
    def expected = fixture('fixtures/packageurl/simple.json')
    assert objectMapper.writeValueAsString(value) == expected
  }

  @Test
  void 'deserialize single'() {
    def content = fixture('fixtures/packageurl/simple.json')
    objectMapper.readValue(content, PackageUrl.class).with {
      log it
      assert type == 'maven'
      assert namespaceAsString == 'foo'
      assert name == 'bar'
      assert version == '1'
    }
  }

  @ToString(includePackage = false, includeNames = true)
  private static class Envelope
  {
    @JsonProperty
    List<PackageUrl> coordinates
  }

  @Test
  void 'serialize complex'() {
    def value = new Envelope(
        coordinates: [
            PackageUrl.parse('maven:foo/bar@1'),
            PackageUrl.parse('maven:baz/qux@2')
        ]
    )

    def expected = fixture('fixtures/packageurl/complex.json')
    assert objectMapper.writeValueAsString(value) == expected
  }

  @Test
  void 'deserialize complex'() {
    def content = fixture('fixtures/packageurl/complex.json')
    objectMapper.readValue(content, Envelope.class).with {
      log it
      assert coordinates != null
      assert coordinates.size() == 2

      coordinates.get(0).with {
        log it
        assert type == 'maven'
        assert namespaceAsString == 'foo'
        assert name == 'bar'
        assert version == '1'
      }

      coordinates.get(1).with {
        log it
        assert type == 'maven'
        assert namespaceAsString == 'baz'
        assert name == 'qux'
        assert version == '2'
      }
    }
  }
}
