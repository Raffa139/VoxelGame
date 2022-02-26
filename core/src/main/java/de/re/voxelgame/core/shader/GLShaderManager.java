package de.re.voxelgame.core.shader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.glDeleteProgram;

public class GLShaderManager {
  private static GLShaderManager instant;

  private final List<Integer> shaderIds;

  private GLShaderManager() {
    shaderIds = new ArrayList<>();
  }

  public static GLShaderManager get() {
    if (instant == null) {
      instant = new GLShaderManager();
    }

    return instant;
  }

  public Shader createShader(String vertexFile, String fragmentFile) throws IOException, URISyntaxException {
    Shader shader = new Shader(vertexFile, fragmentFile);
    shaderIds.add(shader.getId());

    return shader;
  }

  public void terminate() {
    for (int id : shaderIds) {
      glDeleteProgram(id);
    }
  }
}
