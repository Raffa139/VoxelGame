package de.re.voxelgame.core.objects.sampler;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

public class SamplerCube extends Sampler {
  protected SamplerCube(List<FileInputStream> fins) throws IOException {
    super(glGenTextures());

    glBindTexture(GL_TEXTURE_2D_ARRAY, id);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

    for (int i = 0; i < fins.size(); i++) {
      FileInputStream fin = fins.get(i);

      PNGDecoder decoder = new PNGDecoder(fin);
      ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
      decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
      buffer.flip();

      glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
      glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    }

    glBindTexture(GL_TEXTURE_2D, 0);
  }

  @Override
  public void bind(int index) {
    glActiveTexture(GL_TEXTURE0 + index);
    glBindTexture(GL_TEXTURE_CUBE_MAP, id);
  }
}
