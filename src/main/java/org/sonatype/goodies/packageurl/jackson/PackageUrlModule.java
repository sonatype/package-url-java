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
package org.sonatype.goodies.packageurl.jackson;

import java.io.IOException;

import org.sonatype.goodies.packageurl.PackageUrl;
import org.sonatype.goodies.packageurl.PackageUrl.RenderFlavor;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link PackageUrl} Jackson module.
 *
 * @since 1.0.0
 */
public class PackageUrlModule
    extends SimpleModule
{
  private static final long serialVersionUID = 1L;

  private RenderFlavor flavor = RenderFlavor.getDefault();

  /**
   * Configure {@link RenderFlavor} for string rendering.
   */
  public PackageUrlModule withFlavor(final RenderFlavor flavor) {
    this.flavor = checkNotNull(flavor);
    return this;
  }

  @Override
  public void setupModule(final SetupContext context) {
    addSerializer(PackageUrl.class, new PackageUrlSerializer(flavor));
    addDeserializer(PackageUrl.class, new PackageUrlDeserializer());

    super.setupModule(context);
  }

  /**
   * {@link PackageUrl} deserializer.
   */
  public static class PackageUrlDeserializer
      extends StdDeserializer<PackageUrl>
  {
    private static final long serialVersionUID = 1L;

    public PackageUrlDeserializer() {
      super(PackageUrl.class);
    }

    @Override
    public PackageUrl deserialize(final JsonParser parser, final DeserializationContext context)
        throws IOException, JsonProcessingException
    {
      String value = parser.readValueAs(String.class);
      return PackageUrl.parse(value);
    }
  }

  /**
   * {@link PackageUrl} serializer.
   */
  public static class PackageUrlSerializer
      extends StdSerializer<PackageUrl>
  {
    private static final long serialVersionUID = 1L;

    private final RenderFlavor flavor;

    public PackageUrlSerializer(final RenderFlavor flavor) {
      super(PackageUrl.class);
      this.flavor = checkNotNull(flavor);
    }

    @Override
    public void serialize(final PackageUrl value, final JsonGenerator generator, final SerializerProvider provider)
        throws IOException
    {
      generator.writeString(value.toString(flavor));
    }
  }
}
