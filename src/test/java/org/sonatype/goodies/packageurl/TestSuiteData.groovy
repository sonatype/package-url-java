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

import groovy.json.JsonSlurper
import groovy.transform.ToString

/**
 * Package URL specification test-suite-data
 *
 * @see <a href="https://github.com/package-url/purl-spec/blob/master/test-suite-data.json">latest</a>
 */
class TestSuiteData
{
  @ToString(includeNames = true, includePackage = false)
  static class Entry
  {
    String description

    String purl

    String canonical_purl

    String type

    String namespace

    String name

    String version

    Map<String, String> qualifiers

    String subpath

    boolean is_invalid
  }

  static List<Entry> get(final String resource) {
    URL url = this.getResource(resource)
    assert url != null: "Missing resource: $resource"

    def entries = new JsonSlurper().parse(url) as List<Map>
    return entries.collect { new Entry(it) }
  }

  static void main(final String[] args) {
    get().each {
      println it
    }
  }
}
