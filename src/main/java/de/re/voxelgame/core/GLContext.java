package de.re.voxelgame.core;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLContext {
  private final long window;

  public GLContext(int width, int height, String title) {
    // Setup error callback. Default implementation will print error messages in System.err
    GLFWErrorCallback.createPrint(System.err).set();

    // Init GLFW
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW!");
    }

    // Configure GLFW
    glfwDefaultWindowHints(); // Optional, current hints are already the defaults
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

    // Create window
    window = glfwCreateWindow(width, height, title, NULL, NULL);
    if (window == NULL) {
      glfwTerminate();
      throw new IllegalStateException("Failed to create GLFW window!");
    }

    // Setup key callback
    glfwSetKeyCallback(window, KeyListener::keyCallback);

    // Get thread stack & push a new frame
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pWidth = stack.mallocInt(1); // int pointer
      IntBuffer pHeight = stack.mallocInt(1); // int pointer

      glfwGetWindowSize(window, pWidth, pHeight);
      GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor()); // Get resolution of primary monitor

      // Center window
      glfwSetWindowPos(
              window,
              (vidMode.width() - pWidth.get(0)) / 2,
              (vidMode.height() - pHeight.get(0)) / 2);
    }

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1); // 1: V-Sync, 0: Remove fps cap
    glfwShowWindow(window);

    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    createCapabilities();
  }

  public void update() {
    glfwSwapBuffers(window);
    glfwPollEvents();
  }

  public boolean isCloseRequested() {
    return glfwWindowShouldClose(window);
  }

  public void requestClose() {
    glfwSetWindowShouldClose(window, true);
  }

  public void terminate() {
    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    glfwTerminate();
    glfwSetErrorCallback(null).free(); // Free error callback
  }

  public long getWindow() {
    return window;
  }
}
