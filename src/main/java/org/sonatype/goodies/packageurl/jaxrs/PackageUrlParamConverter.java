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
package org.sonatype.goodies.packageurl.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import org.sonatype.goodies.packageurl.PackageUrl;
import org.sonatype.goodies.packageurl.RenderFlavor;

import static java.util.Objects.requireNonNull;

/**
 * {@link PackageUrl} JAX-RS parameter converter.
 *
 * @since 1.0.0
 */
public class PackageUrlParamConverter
    implements ParamConverter<PackageUrl>
{
  @Nullable
  private final RenderFlavor flavor;

  public PackageUrlParamConverter(@Nullable final RenderFlavor flavor) {
    this.flavor = flavor;
  }

  @Override
  public PackageUrl fromString(final String value) {
    if (value == null) {
      throw new IllegalArgumentException();
    }
    return PackageUrl.parse(value);
  }

  @Override
  public String toString(final PackageUrl value) {
    if (value == null) {
      throw new IllegalArgumentException();
    }
    return value.toString(flavor != null ? flavor : RenderFlavor.getDefault());
  }

  @Provider
  public static class ProviderImpl
      implements ParamConverterProvider
  {
    @Nullable
    private final RenderFlavor flavor;

    public ProviderImpl(final RenderFlavor flavor) {
      this.flavor = flavor;
    }

    public ProviderImpl() {
      this(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType,
                                              final Type genericType,
                                              final Annotation[] annotations)
    {
      requireNonNull(rawType);
      if (rawType.equals(PackageUrl.class)) {
        return (ParamConverter<T>) new PackageUrlParamConverter(flavor);
      }
      return null;
    }
  }
}
