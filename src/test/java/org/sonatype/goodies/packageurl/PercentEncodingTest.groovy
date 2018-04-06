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

/**
 * Tests for {@link PercentEncoding}.
 */
class PercentEncodingTest
    extends TestSupport
{
    @Test
    void basic() {
        def value = '1.2.3-FOO/bar baz'

        def encoded = PercentEncoding.encode(value)
        log encoded
        assert encoded == '1.2.3-FOO/bar%20baz'

        def decoded = PercentEncoding.decode(encoded)
        log decoded
        assert value == decoded
    }
}
