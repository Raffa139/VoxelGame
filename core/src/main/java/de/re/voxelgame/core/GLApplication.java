package de.re.voxelgame.core;

import de.re.voxelgame.core.objects.GLVertexArrayManager;
import de.re.voxelgame.core.objects.sampler.GLSamplerManager;
import de.re.voxelgame.core.objects.shader.GLShaderManager;
import de.re.voxelgame.core.objects.shader.Shader;
import org.joml.Matrix4f;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public abstract class GLApplication {
  protected final GLContext context;

  protected final GLShaderManager shaderManager;
  protected final GLSamplerManager samplerManager;
  protected final GLVertexArrayManager vaoManager;

  protected float currentTime;

  protected Camera camera;

  protected Matrix4f view;

  protected Matrix4f projection;

  private final List<Shader> shaders;

  private final Map<Class<? extends ApplicationSystem>, ApplicationSystem> systems;

  public GLApplication(int width, int height, String title) {
    context = GLContext.init(width, height, title);
    shaderManager = GLShaderManager.get();
    samplerManager = GLSamplerManager.get();
    vaoManager = GLVertexArrayManager.get();
    shaders = new ArrayList<>();
    systems = new HashMap<>();
  }

  protected Shader createShader(String vertexFile, String fragmentFile) throws IOException, URISyntaxException {
    Shader shader = shaderManager.createShader(vertexFile, fragmentFile);
    shaders.add(shader);
    return shader;
  }

  protected void useCamera(Camera camera) {
    this.camera = camera;
  }

  protected <T extends ApplicationSystem> void addSystem(Class<T> system) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (!hasSystem(system)) {
      systems.put(system, system.getConstructor(GLApplication.class).newInstance(this));
    }
  }

  protected <T extends ApplicationSystem> void removeSystem(Class<T> system) {
    systems.remove(system);
  }

  protected <T extends ApplicationSystem> boolean hasSystem(Class<T> system) {
    return systems.containsKey(system);
  }

  protected <T extends ApplicationSystem> T getSystem(Class<T> system) {
    if (!hasSystem(system)) {
      throw new IllegalArgumentException(system.getName() + " not found!");
    }

    return system.cast(systems.get(system));
  }

  protected void beginFrame() {
    currentTime = (float) glfwGetTime();
    setupViewProjection();
    setupShader();

    for (Class<? extends ApplicationSystem> system : systems.keySet()) {
      ApplicationSystem instance = systems.get(system);
      instance.invoke();
    }
  }

  protected void endFrame() {
    if (cameraInUse()) {
      camera.update(context.getDeltaTime(), !context.isMouseCursorToggled());
    }
    context.update();
  }

  protected void quit() {
    shaderManager.terminate();
    samplerManager.terminate();
    vaoManager.terminate();
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
