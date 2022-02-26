package de.re.voxelgame.core;

import de.re.voxelgame.core.shader.GLShaderManager;
import de.re.voxelgame.core.shader.Shader;
import org.joml.Matrix4f;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public abstract class GLApplication {
  protected final GLContext context;

  protected final GLShaderManager shaderManager;

  protected float currentTime;

  protected Camera camera;

  protected Matrix4f view;

  protected Matrix4f projection;

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

  protected void useCamera(Camera camera) {
    this.camera = camera;
  }

  protected void beginFrame() {
    currentTime = (float) glfwGetTime();
    setupViewProjection();
    setupShader();
  }

  protected void endFrame() {
    if (cameraInUse()) {
      camera.update(context.getDeltaTime(), !context.isMouseCursorToggled());
    }
    context.update();
  }

  protected void quit() {
    shaderManager.terminate();
    context.terminate();
  }

  private void setupShader() {
    for (Shader shader : shaders) {
      shader.use();
      shader.setFloat("iTime", currentTime);
      shader.setInt("iWindowWidth", context.getWindowWidth());
      shader.setInt("iWindowHeight", context.getWindowHeight());
      shader.setFloat("iAspectRatio", context.getAspectRatio());

      if (cameraInUse()) {
        shader.setMatrix4("iView", view);
        shader.setMatrix4("iProjection", projection);
        shader.setVec3("iCameraPosition", camera.getPos());
      }
    }
  }

  private void setupViewProjection() {
    if (cameraInUse()) {
      view = camera.getViewMatrix();
      projection = new Matrix4f().perspective((float) Math.toRadians(camera.getFov()), context.getAspectRatio(), 0.01f, 1000.0f);
    }
  }

  private boolean cameraInUse() {
    return camera != null;
  }
}
