package de.re.voxelgame.core.shader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.glDeleteProgram;

public final class Shaders {
  private static final List<Integer> SHADER_IDS = new ArrayList<>();

  private Shaders() {
  }

  public static Shader create(String vertexFile, String fragmentFile) throws IOException, URISyntaxException {
    Shader shader = new Shader(vertexFile, fragmentFile);
    SHADER_IDS.add(shader.getId());

    return shader;
  }

  public static void terminate() {
    for (int id : SHADER_IDS) {
      glDeleteProgram(id);
    }
  }
}
