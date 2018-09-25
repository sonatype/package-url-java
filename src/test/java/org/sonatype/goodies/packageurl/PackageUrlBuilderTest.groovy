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

import static org.junit.Assert.fail

/**
 * {@link PackageUrlBuilder} tests.
 */
class PackageUrlBuilderTest
    extends TestSupport
{
  @Test
  void 'type required'() {
    try {
      new PackageUrlBuilder().name('foo').build()
      fail()
    }
    catch (MissingComponentException e) {
      assert e.name == 'type'
    }
  }

  @Test
  void 'name required'() {
    try {
      new PackageUrlBuilder().type('foo').build()
      fail()
    }
    catch (MissingComponentException e) {
      assert e.name == 'name'
    }
  }

  @Test
  void 'basic components'() {
    new PackageUrlBuilder().type('foo').name('bar').version('baz').build().with {
      assert type == 'foo'
      assert name == 'bar'
      assert version == 'baz'
    }
  }

  @Test
  void 'from purl'() {
    PackageUrl purl1 = new PackageUrl('foo', ['a', 'b', 'c'], 'bar', 'baz', [a: '1'], ['d', 'e', 'f'])
    log purl1
    PackageUrlBuilder builder = new PackageUrlBuilder().from(purl1)
    PackageUrl purl2 = builder.build()
    log purl2
    assert purl1 == purl2
  }

  @Test
  void 'namespace asis'() {
    List<String> ns = ['a', 'b', 'c']
    PackageUrl purl = new PackageUrlBuilder().type('foo').name('bar').namespace(ns).build()
    assert purl.namespace == ns
  }

  @Test
  void 'namespace parsed'() {
    List<String> ns = ['a', 'b', 'c']
    PackageUrl purl = new PackageUrlBuilder().type('foo').name('bar').namespace(ns.join('/')).build()
    assert purl.namespace == ns
  }

  @Test
  void 'subpath asis'() {
    List<String> subpath = ['a', 'b', 'c']
    PackageUrl purl = new PackageUrlBuilder().type('foo').name('bar').subpath(subpath).build()
    assert purl.subpath == subpath
  }

  @Test
  void 'subpath parsed'() {
    List<String> subpath = ['a', 'b', 'c']
    PackageUrl purl = new PackageUrlBuilder().type('foo').name('bar').subpath(subpath.join('/')).build()
    assert purl.subpath == subpath
  }
}
