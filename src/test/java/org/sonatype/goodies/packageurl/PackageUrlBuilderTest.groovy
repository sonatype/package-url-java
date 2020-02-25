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
    try {
      new PackageUrlBuilder().name('foo').typeSpecificTransformations(false).build()
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
    try {
      new PackageUrlBuilder().type('foo').typeSpecificTransformations(false).build()
      fail()
    }
    catch (MissingComponentException e) {
      assert e.name == 'name'
    }
  }

  @Test
  void 'basic components'() {
    def builder = new PackageUrlBuilder().type('foo').name('bar').version('baz')
    builder.build().with {
      assert type == 'foo'
      assert name == 'bar'
      assert version == 'baz'
    }
    builder.typeSpecificTransformations(false).build().with {
      assert type == 'foo'
      assert name == 'bar'
      assert version == 'baz'
    }
  }

  @Test
  void 'from purl'() {
    PackageUrl purl1 = new PackageUrl('foo', ['a', 'b', 'c'], 'bar', 'baz', [a: '1'] as TreeMap, ['d', 'e', 'f'])
    log purl1
    PackageUrlBuilder builder = new PackageUrlBuilder().from(purl1)
    PackageUrl purl2 = builder.build()
    log purl2
    assert purl1 == purl2
    PackageUrl purl3 = builder.typeSpecificTransformations(false).build()
    log purl3
    assert purl1 == purl3
  }

  @Test
  void 'namespace asis'() {
    List<String> ns = ['a', 'b', 'c']
    def builder = new PackageUrlBuilder().type('foo').name('bar').namespace(ns)
    assert builder.build().namespace == ns
    assert builder.typeSpecificTransformations(false).build().namespace == ns
  }

  @Test
  void 'namespace parsed'() {
    List<String> ns = ['a', 'b', 'c']
    def builder = new PackageUrlBuilder().type('foo').name('bar').namespace(ns.join('/'))
    assert builder.build().namespace == ns
    assert builder.typeSpecificTransformations(false).build().namespace == ns
  }

  @Test
  void 'subpath asis'() {
    List<String> subpath = ['a', 'b', 'c']
    def builder = new PackageUrlBuilder().type('foo').name('bar').subpath(subpath)
    assert builder.build().subpath == subpath
    assert builder.typeSpecificTransformations(false).build().subpath == subpath
  }

  @Test
  void 'subpath parsed'() {
    List<String> subpath = ['a', 'b', 'c']
    def builder = new PackageUrlBuilder().type('foo').name('bar').subpath(subpath.join('/'))
    assert builder.build().subpath == subpath
    assert builder.typeSpecificTransformations(false).build().subpath == subpath
  }

  @Test
  void 'qualifiers append'() {
    PackageUrlBuilder builder = new PackageUrlBuilder().type('foo').name('bar')
    builder.qualifiers([a: '1', B: '2'])
    builder.qualifier('c', '3')
    builder.build().with {
      assert qualifiers == [a: '1', b: '2', c: '3']
    }
    builder.typeSpecificTransformations(false).build().with {
      assert qualifiers == [a: '1', b: '2', c: '3']
    }
  }

  @Test
  void 'qualifiers null resets'() {
    PackageUrlBuilder builder = new PackageUrlBuilder().type('foo').name('bar')
    builder.qualifier('a', '1')
    builder.qualifiers((Map)null)
    builder.qualifier('B', '2')
    builder.build().with {
      assert qualifiers == [b: '2']
    }
    builder.typeSpecificTransformations(false).build().with {
      assert qualifiers == [b: '2']
    }
  }

  @Test
  void 'qualifiers that are empty and uppercase keys'() {
    PackageUrlBuilder builder = new PackageUrlBuilder().type('foo').name('bar')
    builder.qualifiers([A: '1', b: ''])
    builder.qualifier('c', '3')
    builder.qualifier('d', '')
    builder.build().with {
      assert qualifiers == [a: '1', c: '3']
    }
    builder.typeSpecificTransformations(false).build().with {
      assert qualifiers == [a: '1', c: '3']
    }
  }

  @Test
  void 'bitbucket namespace and name'() {
    PackageUrlBuilder builder = new PackageUrlBuilder().type('bitbucket').namespace('fOo').name('BaR')
    builder.build().with {
      assert namespace == ['foo']
      assert name == 'bar'
    }
    builder.typeSpecificTransformations(false).build().with {
      assert namespace == ['fOo']
      assert name == 'BaR'
    }
  }

  @Test
  void 'github namespace and name'() {
    PackageUrlBuilder builder = new PackageUrlBuilder().type('github').namespace('fOo').name('BaR')
    builder.build().with {
      assert namespace == ['foo']
      assert name == 'bar'
    }
    builder.typeSpecificTransformations(false).build().with {
      assert namespace == ['fOo']
      assert name == 'BaR'
    }
  }

  @Test
  void 'pypi name'() {
    PackageUrlBuilder builder = new PackageUrlBuilder().type('pypi').name('fOo-BaR_baZ')
    builder.build().with {
      assert name == 'foo-bar-baz'
    }
    builder.typeSpecificTransformations(false).build().with {
      assert name == 'fOo-BaR_baZ'
    }
  }

}
