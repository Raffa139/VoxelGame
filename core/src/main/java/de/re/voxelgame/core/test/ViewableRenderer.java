package de.re.voxelgame.core.test;

import de.re.voxelgame.core.objects.shader.GLShaderManager;
import de.re.voxelgame.core.objects.shader.Shader;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ViewableRenderer {
  private static final String VERTEX_SHADER_CONTENT =
      "#version 330 core\n" +
      "layout (location = 0) in vec3 iPos;\n" +
      "void main() {\n" +
      "    gl_Position = vec4(iPos, 1.0);\n" +
      "}";
  private static final String FRAGMENT_SHADER_CONTENT =
      "#version 330 core\n" +
      "layout (location = 0) out vec4 FragColor;\n" +
      "void main() {\n" +
      "    gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
      "}";

  private final Shader viewableShader;

  public ViewableRenderer() throws IOException, URISyntaxException {
    viewableShader = GLShaderManager.get()
        .createShader(VERTEX_SHADER_CONTENT, FRAGMENT_SHADER_CONTENT);
  }

  public void prepare() {
    glClearColor(0.0f, 0.0f, 1.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT);
  }

  public void render(Viewable viewable) {
    viewableShader.use();

    glBindVertexArray(viewable.getVaoId());
    glDrawArrays(GL_TRIANGLES, 0, viewable.getVertexCount());
    glBindVertexArray(0);
  }
}
