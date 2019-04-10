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
package org.sonatype.goodies.packageurl.jaxb

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlElementWrapper
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

import org.sonatype.goodies.packageurl.PackageUrl
import org.sonatype.goodies.packageurl.RenderFlavor
import org.sonatype.goodies.testsupport.TestSupport

import groovy.transform.ToString
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link PackageUrlXmlAdapter}.
 */
class PackageUrlXmlAdapterTest
  extends TestSupport
{
  @Before
  void setUp() {
    RenderFlavor.default = RenderFlavor.SCHEME
  }

  private String fixture(final String path) {
    def url = getClass().getResource('/' + path)
    assert url != null
    return url.text
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @ToString(includePackage = false, includeNames = true)
  private static class Envelope
  {
    @XmlElementWrapper
    @XmlElement(name="item")
    @XmlJavaTypeAdapter(PackageUrlXmlAdapter.class)
    List<PackageUrl> coordinates
  }

  @Test
  void 'serialize complex'() {
    JAXBContext context = JAXBContext.newInstance(Envelope.class)
    Marshaller marshaller = context.createMarshaller()
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)

    def value = new Envelope(
        coordinates: [
            PackageUrl.parse('pkg:maven/foo/bar@1'),
            PackageUrl.parse('pkg:maven/baz/qux@2')
        ]
    )

    def expected = fixture('fixtures/complex.xml')

    def writer = new StringWriter()
    marshaller.marshal(value, writer)

    assert writer.toString() == expected
  }
}
