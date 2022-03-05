package de.re.voxelgame.core.objects.shader;

import java.io.IOException;
import java.nio.file.Path;
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

  public Shader createShader(Path vertexFile, Path fragmentFile) throws IOException {
    Shader shader = new Shader(vertexFile, fragmentFile);
    shaderIds.add(shader.getId());

    return shader;
  }

  public Shader createShader(String vertexContent, String fragmentContent) throws IOException {
    Shader shader = new Shader(vertexContent, fragmentContent);
    shaderIds.add(shader.getId());

    return shader;
  }

  public void terminate() {
    for (int id : shaderIds) {
      glDeleteProgram(id);
    }
  }
}
