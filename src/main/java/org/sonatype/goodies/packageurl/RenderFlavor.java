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
package org.sonatype.goodies.packageurl;

import javax.annotation.Nullable;

/**
 * How to render the Package-URL.
 *
 * @since ???
 */
public enum RenderFlavor
{
  /**
   * Render with {@link PackageUrl#SCHEME} (default).
   */
  SCHEME,

  /**
   * Render with-out {@link PackageUrl#SCHEME}.
   */
  SCHEMELESS;

  private static RenderFlavor _default = SCHEME;

  public static RenderFlavor getDefault() {
    return _default;
  }

  /**
   * Set the default flavor, or null for the original default.
   */
  public static void setDefault(@Nullable final RenderFlavor flavor) {
    _default = flavor == null ? SCHEMELESS : flavor;
  }
}
