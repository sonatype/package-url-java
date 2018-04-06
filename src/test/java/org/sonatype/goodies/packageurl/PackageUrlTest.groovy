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

import org.junit.Test
import org.sonatype.goodies.testsupport.TestSupport

import static org.junit.Assert.fail

/**
 * Tests for {@link PackageUrl}.
 */
class PackageUrlTest
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

    @Test
    void 'render as string'() {
        def assertRendering = { final PackageUrl purl, final String expected ->
            log "PURL: ${purl.explain()} -> $purl"
            assert purl.toString() == expected
        }

        assertRendering new PackageUrl('foo', ['bar'], 'baz', 'qux', [a: 'b', c: 'd'], ['blah']),
                'foo:bar/baz@qux?a=b&c=d#blah'

        assertRendering new PackageUrl.Builder()
                .type('foo')
                .namespace('ns1/ns2')
                .name('n')
                .version('1.2.3')
                .qualifer('a', 'b')
                .qualifer('c', 'd')
                .subpath('sp1/sp2')
                .build(),
                'foo:ns1/ns2/n@1.2.3?a=b&c=d#sp1/sp2'

        assertRendering new PackageUrl.Builder()
                .type('foo')
                .namespace('ns1/!ns2')
                .name('n')
                .version('1.2.3')
                .qualifer('a', 'b')
                .qualifer('c', 'd')
                .subpath('sp1/!sp2')
                .build(),
                'foo:ns1/%21ns2/n@1.2.3?a=b&c=d#sp1/%21sp2'

        assertRendering new PackageUrl('foo', (List)null, 'bar', 'baz', null, (List)null),
                'foo:bar@baz'

        assertRendering new PackageUrl('foo', ['bar'], 'baz', 'qux', null, null),
                'foo:bar/baz@qux'

        assertRendering new PackageUrl('foo', null, 'bar', 'baz', null, ['qux']),
                'foo:bar@baz#qux'
    }

    private PackageUrl parse(final String value) {
        def purl = PackageUrl.parse(value)
        log "PURL: ${purl.explain()} -> $purl"
        return purl
    }

    @Test
    void parse_basic() {
        parse('foo:bar@baz').with {
            assert type == 'foo'
            assert namespace == null
            assert name == 'bar'
            assert version == 'baz'
        }
    }

    /**
     * Parsing tests based examples from: https://github.com/package-url/purl-spec#some-purl-examples
     */
    @Test
    void parse_examples() {
        parse('bitbucket:birkenfeld/pygments-main@244fd47e07d1014f0aed9c').with {
            assert type == 'bitbucket'
            assert namespace == [ 'birkenfeld' ]
            assert name == 'pygments-main'
            assert version == '244fd47e07d1014f0aed9c'
        }

        parse('deb:debian/curl@7.50.3-1?arch=i386&distro=jessie').with {
            assert type == 'deb'
            assert namespace == [ 'debian' ]
            assert name == 'curl'
            assert version == '7.50.3-1'
            assert qualifiers.arch == 'i386'
            assert qualifiers.distro == 'jessie'
        }

        parse('docker:cassandra@sha256:244fd47e07d1004f0aed9c').with {
            assert type == 'docker'
            assert name == 'cassandra'
            assert version == 'sha256:244fd47e07d1004f0aed9c'
        }

        parse('docker:gcr.io/customer/dockerimage@sha256:244fd47e07d1004f0aed9c').with {
            assert type == 'docker'
            assert namespace == [ 'gcr.io', 'customer' ]
            assert name == 'dockerimage'
            assert version == 'sha256:244fd47e07d1004f0aed9c'
        }

        parse('gem:jruby-launcher@1.1.2?platform=java').with {
            assert type == 'gem'
            assert name == 'jruby-launcher'
            assert version == '1.1.2'
            assert qualifiers.platform == 'java'
        }

        parse('gem:ruby-advisory-db-check@0.12.4').with {
            assert type == 'gem'
            assert name == 'ruby-advisory-db-check'
            assert version == '0.12.4'
        }

        parse('github:package-url/purl-spec@244fd47e07d1004f0aed9c').with {
            assert type == 'github'
            assert namespace == [ 'package-url' ]
            assert name == 'purl-spec'
            assert version == '244fd47e07d1004f0aed9c'
        }

        parse('golang:google.golang.org/genproto#googleapis/api/annotations').with {
            assert type == 'golang'
            assert namespace == [ 'google.golang.org' ]
            assert name == 'genproto'
            assert subpath == [ 'googleapis', 'api', 'annotations' ]
        }

        parse('maven:org.apache.xmlgraphics/batik-anim@1.9.1?packaging=sources').with {
            assert type == 'maven'
            assert namespace == [ 'org.apache.xmlgraphics' ]
            assert name == 'batik-anim'
            assert version == '1.9.1'
            assert qualifiers.packaging == 'sources'
        }

        parse('maven:org.apache.xmlgraphics/batik-anim@1.9.1?repository_url=repo.spring.io/release').with {
            assert type == 'maven'
            assert namespace == [ 'org.apache.xmlgraphics' ]
            assert name == 'batik-anim'
            assert version == '1.9.1'
            assert qualifiers.repository_url == 'repo.spring.io/release'
        }

        parse('npm:%40angular/animation@12.3.1').with {
            assert type == 'npm'
            assert namespace == [ '@angular' ]
            assert name == 'animation'
            assert version == '12.3.1'
        }

        parse('npm:foobar@12.3.1').with {
            assert type == 'npm'
            assert name == 'foobar'
            assert version == '12.3.1'
        }

        parse('nuget:EnterpriseLibrary.Common@6.0.1304').with {
            assert type == 'nuget'
            assert name == 'EnterpriseLibrary.Common'
            assert version == '6.0.1304'
        }

        parse('pypi:django@1.11.1').with {
            assert type == 'pypi'
            assert name == 'django'
            assert version == '1.11.1'
        }

        parse('rpm:fedora/curl@7.50.3-1.fc25?arch=i386&distro=fedora-25').with {
            assert type == 'rpm'
            assert namespace == [ 'fedora' ]
            assert name == 'curl'
            assert version == '7.50.3-1.fc25'
            assert qualifiers.arch == 'i386'
            assert qualifiers.distro == 'fedora-25'
        }

        parse('rpm:opensuse/curl@7.56.1-1.1.?arch=i386&distro=opensuse-tumbleweed').with {
            assert type == 'rpm'
            assert namespace == [ 'opensuse' ]
            assert name == 'curl'
            assert version == '7.56.1-1.1.'
            assert qualifiers.arch == 'i386'
            assert qualifiers.distro == 'opensuse-tumbleweed'
        }
    }

    @Test
    void parse_ignoreDoubleSlash() {
        parse('foo://bar@baz').with {
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
            parse('1foo:bar@baz')
        }

        // type must not contain spaces
        expectFailure {
            parse('f o o:bar@baz')
        }
    }

    @Test
    void parseType_withSpecial() {
        parse('foo-1+2:bar@baz').with {
            assert type == 'foo-1+2'
            assert namespace == null
            assert name == 'bar'
            assert version == 'baz'
        }
    }

    @Test
    void parseType_renderedLower() {
        parse('FOO:bar@baz').with {
            assert it.toString() == 'foo:bar@baz'
        }
    }

    @Test
    void parseNamespace() {
        parse('foo:bar/baz@qux').with {
            assert type == 'foo'
            assert namespace == [ 'bar' ]
            assert name == 'baz'
            assert version == 'qux'
        }
    }

    @Test
    void parseNamespace_withSegments() {
        parse('foo:/a/b/c/baz@qux').with {
            assert type == 'foo'
            assert namespace == [ 'a', 'b', 'c' ]
            assert name == 'baz'
            assert version == 'qux'
        }
    }

    @Test
    void parseQualifiers() {
        parse('foo:bar@baz?a=b&c=d#blah').with {
            assert type == 'foo'
            assert namespace == null
            assert name == 'bar'
            assert version == 'baz'
            assert qualifiers.a == 'b'
            assert qualifiers.c == 'd'
            assert subpath == [ 'blah' ]
        }
    }

    @Test
    void parseSubpath() {
        parse('foo:bar@baz#qux').with {
            assert type == 'foo'
            assert namespace == null
            assert name == 'bar'
            assert version == 'baz'
            assert subpath == [ 'qux' ]
        }
    }
}
