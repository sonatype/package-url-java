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
 * {@link PackageUrlParser} tests.
 */
class PackageUrlParserTest
    extends TestSupport
{
  @SuppressWarnings("GroovyUnusedCatchParameter")
  private static void expectFailure(final Closure task) {
    try {
      task.call()
      fail()
    }
    catch (e) {
      // expected
    }
  }

  private PackageUrl parse(final String value) {
    log "Parse: $value"
    def purl = PackageUrl.parse(value)
    log "PURL: ${purl.explain()} -> $purl"
    return purl
  }

  private PackageUrl parseAsIs(final String value) {
    log "ParseAsIs: $value"
    def purl = PackageUrlParser.parse(value, true)
    log "PURL AsIs: ${purl.explain()} -> $purl"
    return purl
  }

  @Test
  void parse_basic() {
    parse('pkg:foo/bar@baz').with {
      assert type == 'foo'
      assert namespace == null
      assert name == 'bar'
      assert version == 'baz'
    }
    parseAsIs('pkg:foo/bar@baz').with {
      assert type == 'foo'
      assert namespace == null
      assert name == 'bar'
      assert version == 'baz'
    }
  }

  @Test
  void tostring_flavors() {
    parse('pkg:foo/bar@baz').with {
      assert it.toString(RenderFlavor.SCHEME) == 'pkg:foo/bar@baz'
      assert it.toString(RenderFlavor.SCHEMELESS) == 'foo:bar@baz'
    }
    parseAsIs('pkg:foo/bar@baz').with {
      assert it.toString(RenderFlavor.SCHEME) == 'pkg:foo/bar@baz'
      assert it.toString(RenderFlavor.SCHEMELESS) == 'foo:bar@baz'
    }
  }

  /**
   * Parsing tests based examples from: https://github.com/package-url/purl-spec#some-purl-examples
   */
  @Test
  void parse_examples() {
    parse('pkg:bitbucket/birkenfeld/pygments-main@244fd47e07d1014f0aed9c').with {
      assert type == 'bitbucket'
      assert namespace == ['birkenfeld']
      assert name == 'pygments-main'
      assert version == '244fd47e07d1014f0aed9c'
    }
    parse('pkg:bitbucket/BirkenFeld/Pygments-Main@244fd47e07d1014f0aed9c').with {
      assert type == 'bitbucket'
      assert namespace == ['birkenfeld']
      assert name == 'pygments-main'
      assert version == '244fd47e07d1014f0aed9c'
    }
    parseAsIs('pkg:bitbucket/BirkenFeld/Pygments-Main@244fd47e07d1014f0aed9c').with {
      assert type == 'bitbucket'
      assert namespace == ['BirkenFeld']
      assert name == 'Pygments-Main'
      assert version == '244fd47e07d1014f0aed9c'
    }

    parse('pkg:deb/debian/curl@7.50.3-1?arch=i386&distro=jessie').with {
      assert type == 'deb'
      assert namespace == ['debian']
      assert name == 'curl'
      assert version == '7.50.3-1'
      assert qualifiers.arch == 'i386'
      assert qualifiers.distro == 'jessie'
    }
    parse('pkg:deb/debian/curl@7.50.3-1?Arch=i386&Distro=jessie').with {
      assert type == 'deb'
      assert namespace == ['debian']
      assert name == 'curl'
      assert version == '7.50.3-1'
      assert qualifiers.arch == 'i386'
      assert qualifiers.distro == 'jessie'
      assert qualifiers.Arch == null
      assert qualifiers.Distro == null
    }
    parseAsIs('pkg:deb/debian/curl@7.50.3-1?Arch=i386&Distro=jessie').with {
      assert type == 'deb'
      assert namespace == ['debian']
      assert name == 'curl'
      assert version == '7.50.3-1'
      assert qualifiers.arch == 'i386'
      assert qualifiers.distro == 'jessie'
      assert qualifiers.Arch == null
      assert qualifiers.Distro == null
    }

    parse('pkg:docker/cassandra@sha256:244fd47e07d1004f0aed9c').with {
      assert type == 'docker'
      assert name == 'cassandra'
      assert version == 'sha256:244fd47e07d1004f0aed9c'
    }

    parse('pkg:docker/gcr.io/customer/dockerimage@sha256:244fd47e07d1004f0aed9c').with {
      assert type == 'docker'
      assert namespace == ['gcr.io', 'customer']
      assert name == 'dockerimage'
      assert version == 'sha256:244fd47e07d1004f0aed9c'
    }

    parse('pkg:gem/jruby-launcher@1.1.2?platform=java').with {
      assert type == 'gem'
      assert name == 'jruby-launcher'
      assert version == '1.1.2'
      assert qualifiers.platform == 'java'
    }

    parse('pkg:gem/jruby-launcher@1.1.2?Platform=java').with {
      assert type == 'gem'
      assert name == 'jruby-launcher'
      assert version == '1.1.2'
      assert qualifiers.platform == 'java'
      assert qualifiers.Platform == null
    }
    parseAsIs('pkg:gem/jruby-launcher@1.1.2?Platform=java').with {
      assert type == 'gem'
      assert name == 'jruby-launcher'
      assert version == '1.1.2'
      assert qualifiers.platform == 'java'
      assert qualifiers.Platform == null
    }

    parse('pkg:gem/jruby-launcher@1.1.2?Platform=').with {
      assert type == 'gem'
      assert name == 'jruby-launcher'
      assert version == '1.1.2'
      assert qualifiers == null
    }
    parseAsIs('pkg:gem/jruby-launcher@1.1.2?Platform=').with {
      assert type == 'gem'
      assert name == 'jruby-launcher'
      assert version == '1.1.2'
      assert qualifiers == null
    }

    parse('pkg:gem/ruby-advisory-db-check@0.12.4').with {
      assert type == 'gem'
      assert name == 'ruby-advisory-db-check'
      assert version == '0.12.4'
    }

    parse('pkg:github/package-url/purl-spec@244fd47e07d1004f0aed9c').with {
      assert type == 'github'
      assert namespace == ['package-url']
      assert name == 'purl-spec'
      assert version == '244fd47e07d1004f0aed9c'
    }
    parse('pkg:github/Package-Url/Purl-Spec@244fd47e07d1004f0aed9c').with {
      assert type == 'github'
      assert namespace == ['package-url']
      assert name == 'purl-spec'
      assert version == '244fd47e07d1004f0aed9c'
    }
    parseAsIs('pkg:github/Package-Url/Purl-Spec@244fd47e07d1004f0aed9c').with {
      assert type == 'github'
      assert namespace == ['Package-Url']
      assert name == 'Purl-Spec'
      assert version == '244fd47e07d1004f0aed9c'
    }

    parse('pkg:golang/google.golang.org/genproto#googleapis/api/annotations').with {
      assert type == 'golang'
      assert namespace == ['google.golang.org']
      assert name == 'genproto'
      assert subpath == ['googleapis', 'api', 'annotations']
    }

    parse('pkg:maven/org.apache.xmlgraphics/batik-anim@1.9.1?packaging=sources').with {
      assert type == 'maven'
      assert namespace == ['org.apache.xmlgraphics']
      assert name == 'batik-anim'
      assert version == '1.9.1'
      assert qualifiers.packaging == 'sources'
    }

    parse('pkg:maven/org.apache.xmlgraphics/batik-anim@1.9.1?repository_url=repo.spring.io/release').with {
      assert type == 'maven'
      assert namespace == ['org.apache.xmlgraphics']
      assert name == 'batik-anim'
      assert version == '1.9.1'
      assert qualifiers.repository_url == 'repo.spring.io/release'
    }

    parse('pkg:npm/%40angular/animation@12.3.1').with {
      assert type == 'npm'
      assert namespace == ['@angular']
      assert name == 'animation'
      assert version == '12.3.1'
    }

    parse('pkg:npm/foobar@12.3.1').with {
      assert type == 'npm'
      assert name == 'foobar'
      assert version == '12.3.1'
    }

    parse('pkg:nuget/EnterpriseLibrary.Common@6.0.1304').with {
      assert type == 'nuget'
      assert name == 'EnterpriseLibrary.Common'
      assert version == '6.0.1304'
    }

    parse('pkg:pypi/django_allauth@1.11.1').with {
      assert type == 'pypi'
      assert name == 'django-allauth'
      assert version == '1.11.1'
    }
    parseAsIs('pkg:pypi/django_allauth@1.11.1').with {
      assert type == 'pypi'
      assert name == 'django_allauth'
      assert version == '1.11.1'
    }

    parse('pkg:rpm/fedora/curl@7.50.3-1.fc25?arch=i386&distro=fedora-25').with {
      assert type == 'rpm'
      assert namespace == ['fedora']
      assert name == 'curl'
      assert version == '7.50.3-1.fc25'
      assert qualifiers.arch == 'i386'
      assert qualifiers.distro == 'fedora-25'
    }

    parse('pkg:rpm/opensuse/curl@7.56.1-1.1.?arch=i386&distro=opensuse-tumbleweed').with {
      assert type == 'rpm'
      assert namespace == ['opensuse']
      assert name == 'curl'
      assert version == '7.56.1-1.1.'
      assert qualifiers.arch == 'i386'
      assert qualifiers.distro == 'opensuse-tumbleweed'
    }
  }

  @Test
  void parse_ignoreLeadingSlash() {
    parse('pkg:/foo/bar@baz').with {
      assert type == 'foo'
      assert namespace == null
      assert name == 'bar'
      assert version == 'baz'
    }
  }

  @Test
  void parse_ignoreLeadingDoubleSlash() {
    parse('pkg://foo/bar@baz').with {
      assert type == 'foo'
      assert namespace == null
      assert name == 'bar'
      assert version == 'baz'
    }
  }

  @Test
  void parseType_invalid() {
    // type must not start with number
    expectFailure {
      parse('pkg:1foo/bar@baz')
    }

    // type must not contain spaces
    expectFailure {
      parse('pkg:f o o/bar@baz')
    }
  }

  @Test
  void parseType_withSpecial() {
    parse('pkg:foo-1+2/bar@baz').with {
      assert type == 'foo-1+2'
      assert namespace == null
      assert name == 'bar'
      assert version == 'baz'
    }
  }

  @Test
  void parseType_renderedLower() {
    parse('pkg:FOO/bar@baz').with {
      assert it.toString() == 'pkg:foo/bar@baz'
    }
  }

  @Test
  void parseNamespace() {
    parse('pkg:foo/bar/baz@qux').with {
      assert type == 'foo'
      assert namespace == ['bar']
      assert name == 'baz'
      assert version == 'qux'
    }
  }

  @Test
  void parseNamespace_withSegments() {
    parse('pkg:foo/a/b/c/baz@qux').with {
      assert type == 'foo'
      assert namespace == ['a', 'b', 'c']
      assert name == 'baz'
      assert version == 'qux'
    }
  }

  @Test
  void parseQualifiers_namespace_name_version() {
    parse('pkg:foo/bar/qux@baz?a=b&c=d#blah').with {
      assert type == 'foo'
      assert namespace == ['bar']
      assert name == 'qux'
      assert version == 'baz'
      assert qualifiers.a == 'b'
      assert qualifiers.c == 'd'
      assert subpath == ['blah']
    }
  }

  @Test
  void parseQualifiers_name_version() {
    parse('pkg:foo/bar@baz?a=b&c=d#blah').with {
      assert type == 'foo'
      assert namespace == null
      assert name == 'bar'
      assert version == 'baz'
      assert qualifiers.a == 'b'
      assert qualifiers.c == 'd'
      assert subpath == ['blah']
    }
  }

  @Test
  void parseQualifiers_name() {
    parse('pkg:foo/bar?a=b&c=d#blah').with {
      assert type == 'foo'
      assert namespace == null
      assert name == 'bar'
      assert version == null
      assert qualifiers.a == 'b'
      assert qualifiers.c == 'd'
      assert subpath == ['blah']
    }
  }

  @Test
  void parseSubpath() {
    parse('pkg:a/b/c@d#e').with {
      assert type == 'a'
      assert namespace == ['b']
      assert name == 'c'
      assert version == 'd'
      assert subpath == ['e']
    }
  }

  @Test
  void parseSubpath_name_version() {
    parse('pkg:a/b@c#d').with {
      assert type == 'a'
      assert namespace == null
      assert name == 'b'
      assert version == 'c'
      assert subpath == ['d']
    }
  }

  @Test
  void parseSubpath_name() {
    parse('pkg:a/b#c').with {
      assert type == 'a'
      assert namespace == null
      assert name == 'b'
      assert version == null
      assert subpath == ['c']
    }
  }

  @Test
  void asBuilder_mutation() {
    parse('pkg:foo/bar@baz#qux').asBuilder().version(null).build().with {
      assert type == 'foo'
      assert namespace == null
      assert name == 'bar'
      assert version == null
      assert subpath == ['qux']
    }
  }
}
