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

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import org.sonatype.goodies.packageurl.PackageUrl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link PackageUrl} JAX-RS parameter converter.
 *
 * @since ???
 */
public class PackageUrlParamConverter
    implements ParamConverter<PackageUrl>
{
  @Override
  public PackageUrl fromString(final String value) {
    checkArgument(value != null);
    return PackageUrl.parse(value);
  }

  @Override
  public String toString(final PackageUrl value) {
    checkArgument(value != null);
    return value.toString();
  }

  @Provider
  public static class ProviderImpl
      implements ParamConverterProvider
  {
    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType,
                                              final Type genericType,
                                              final Annotation[] annotations)
    {
      checkNotNull(rawType);
      if (rawType.equals(PackageUrl.class)) {
        return (ParamConverter<T>) new PackageUrlParamConverter();
      }
      return null;
    }
  }
}
