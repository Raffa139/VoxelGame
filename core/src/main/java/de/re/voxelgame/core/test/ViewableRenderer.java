package de.re.voxelgame.core.test;

import de.re.voxelgame.core.objects.shader.GLShaderManager;
import de.re.voxelgame.core.objects.shader.Shader;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ViewableRenderer {
  private final Shader viewableShader;

  public ViewableRenderer() throws IOException, URISyntaxException {
    viewableShader = GLShaderManager.get()
        .createShader("viewable.vert", "viewable.frag");
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
