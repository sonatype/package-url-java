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
 * Tests for {@link PercentEncoding}.
 */
class PercentEncodingTest
    extends TestSupport
{
  private void assertEncoding(final String value, final String encoded) {
    def result = PercentEncoding.encode(value)
    log result
    assert result == encoded

    def decoded = PercentEncoding.decode(encoded)
    log decoded
    assert value == decoded
  }

  private void assertNameEncoding(final String value, final String encoded) {
    def result = PercentEncoding.encodeName(value)
    log result
    assert result == encoded

    def decoded = PercentEncoding.decode(encoded)
    log decoded
    assert value == decoded
  }

  @Test
  void 'space is encoded as %20'() {
    assertEncoding 'bar baz', 'bar%20baz'
  }

  @Test
  void 'slash is not encoded'() {
    assertEncoding '1.2.3-FOO/bar baz', '1.2.3-FOO/bar%20baz'
  }

  @Test
  void 'slash is encoded in a name'() {
    assertNameEncoding '1.2.3-FOO/bar baz', '1.2.3-FOO%2Fbar%20baz'
  }

  @Test
  void 'colon is not encoded'() {
    assertEncoding 'sha1:123 foo', 'sha1:123%20foo'
  }

  @Test
  void 'tilda is not encoded'() {
    assertEncoding '~user', '~user'
  }

  @Test
  void 'simpleReplace'() {
    assert PercentEncoding.simpleReplace("xxxxyyyyxxxx", "y", "a") == "xxxxaaaaxxxx"
    assert PercentEncoding.simpleReplace("xxxxyyyyxxxx", "yyyy", "a") == "xxxxaxxxx"
    assert PercentEncoding.simpleReplace("xxxxyyyyxxxx", "y", "aaaa") == "xxxxaaaaaaaaaaaaaaaaxxxx"
    assert PercentEncoding.simpleReplace("xxxxyyyyxxxx", "x", "a") == "aaaayyyyaaaa"
    assert PercentEncoding.simpleReplace("xxxxyyyyxxxx", "xx", "a") == "aayyyyaa"
    assert PercentEncoding.simpleReplace("xxxxyyyyxxxx", "xxx", "a") == "axyyyyax"
  }
}
