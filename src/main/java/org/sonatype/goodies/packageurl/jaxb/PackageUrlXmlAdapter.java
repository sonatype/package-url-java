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
package org.sonatype.goodies.packageurl.jaxb;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.sonatype.goodies.packageurl.PackageUrl;
import org.sonatype.goodies.packageurl.PackageUrl.RenderFlavor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link PackageUrl} JAXB {@link XmlAdapter}.
 *
 * @since 1.0.0
 */
public class PackageUrlXmlAdapter
    extends XmlAdapter<String, PackageUrl>
{
  private final RenderFlavor flavor;

  public PackageUrlXmlAdapter(final RenderFlavor flavor) {
    this.flavor = checkNotNull(flavor);
  }

  public PackageUrlXmlAdapter() {
    this(RenderFlavor.getDefault());
  }

  @Nullable
  @Override
  public PackageUrl unmarshal(@Nullable final String value) throws Exception {
    if (value != null) {
      return PackageUrl.parse(value);
    }
    return null;
  }

  @Nullable
  @Override
  public String marshal(@Nullable final PackageUrl value) throws Exception {
    if (value != null) {
      return value.toString(flavor);
    }
    return null;
  }
}
