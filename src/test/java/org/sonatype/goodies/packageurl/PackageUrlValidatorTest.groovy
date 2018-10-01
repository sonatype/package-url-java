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

import static org.sonatype.goodies.packageurl.PackageUrlValidator.validateName
import static org.sonatype.goodies.packageurl.PackageUrlValidator.validateNamespace
import static org.sonatype.goodies.packageurl.PackageUrlValidator.validateQualifiers
import static org.sonatype.goodies.packageurl.PackageUrlValidator.validateSubpath
import static org.sonatype.goodies.packageurl.PackageUrlValidator.validateType
import static org.sonatype.goodies.packageurl.PackageUrlValidator.validateVersion

/**
 * {@link PackageUrlValidator} tests.
 */
class PackageUrlValidatorTest
    extends TestSupport
{
  //
  // Type
  //

  @Test(expected = MissingComponentException.class)
  void 'type is non-null'() {
    validateType(null)
  }

  @Test(expected = InvalidException.class)
  void 'type non-alpha prefix is invalid'() {
    validateType('_invalid')
  }

  @Test(expected = InvalidException.class)
  void 'type numeric prefix is invalid'() {
    validateType('123invalid')
  }

  @Test(expected = InvalidException.class)
  void 'type space invalid'() {
    validateType('invalid spaces')
  }

  @Test
  void 'type alpha numbers'() {
    validateType('valid1234')
  }

  @Test
  void 'type alpha numbers and special'() {
    validateType('valid-12.34')
  }

  //
  // Namespace
  //

  @Test
  void 'namespace is nullable'() {
    validateNamespace(null)
  }

  //
  // Name
  //

  @Test(expected = MissingComponentException.class)
  void 'name is non-null'() {
    validateName(null)
  }

  //
  // Version
  //

  @Test
  void 'version is nullable'() {
    validateVersion(null)
  }

  //
  // Qualifiers
  //

  @Test
  void 'qualifiers is nullable'() {
    validateQualifiers(null)
  }

  //
  // Subpath
  //

  @Test
  void 'subpath is nullable'() {
    validateSubpath(null)
  }
}
