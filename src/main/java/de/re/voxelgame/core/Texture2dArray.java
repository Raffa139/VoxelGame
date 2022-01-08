package de.re.voxelgame.core;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.re.voxelgame.core.util.ResourceLoader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture2dArray extends Texture {
  public Texture2dArray(int width, int height, String... files) throws IOException, URISyntaxException {
    super(glGenTextures());

    glBindTexture(GL_TEXTURE_2D_ARRAY, id);
    glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
    glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    int depth = files.length;
    glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA, width, height, depth, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);

    for (int layer = 0; layer < depth; layer++) {
      String file = files[layer];
      PNGDecoder decoder = new PNGDecoder(ResourceLoader.locateResource(file, Texture2dArray.class).toFileInputStream());
      ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
      decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
      buffer.flip();

      if (decoder.getWidth() != width || decoder.getHeight() != height) {
        throw new IllegalArgumentException("Image with or height does not match with argument!");
      }

      glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
      glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, layer, width, height, 1, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    }

    glGenerateMipmap(GL_TEXTURE_2D_ARRAY);

    glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
  }

  @Override
  public void bind(int index) {
    glActiveTexture(GL_TEXTURE0 + index);
    glBindTexture(GL_TEXTURE_2D_ARRAY, id);
  }
}
