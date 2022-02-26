package de.re.voxelgame.core;

import de.re.voxelgame.core.shader.GLShaderManager;
import de.re.voxelgame.core.shader.Shader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public abstract class GLApplication {
  protected final GLContext context;

  protected final GLShaderManager shaderManager;

  protected float currentTime;

  private final List<Shader> shaders;

  public GLApplication(int width, int height, String title) {
    context = GLContext.init(width, height, title);
    shaderManager = GLShaderManager.get();
    shaders = new ArrayList<>();
  }

  protected Shader createShader(String vertexFile, String fragmentFile) throws IOException, URISyntaxException {
    Shader shader = shaderManager.createShader(vertexFile, fragmentFile);
    shaders.add(shader);
    return shader;
  }

  protected void beginFrame() {
    currentTime = (float) glfwGetTime();
    setupShader();
  }

  protected void endFrame() {
    context.update();
  }

  protected void quit() {
    shaderManager.terminate();
    context.terminate();
  }

  private void setupShader() {
    for (Shader shader : shaders) {
      shader.use();
      /*shader.setMatrix4("iView", view);
      shader.setMatrix4("iProjection", projection);
      shader.setVec3("iCameraPosition", camera.getPos());*/
      shader.setFloat("iTime", currentTime);
      shader.setInt("iWindowWidth", context.getWindowWidth());
      shader.setInt("iWindowHeight", context.getWindowHeight());
      shader.setFloat("iAspectRatio", context.getAspectRatio());
    }
  }
}
