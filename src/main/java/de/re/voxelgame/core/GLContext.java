package de.re.voxelgame.core;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLContext {
  private final long window;

  private int frames;
  private float deltaTime;
  private float lastFrame;
  private float last;

  private boolean mouseCursorToggled = false;

  public GLContext(int width, int height, String title) {
    // Setup error callback. Default implementation will print error messages in System.err
    glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

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

    // Setup input callback
    glfwSetKeyCallback(window, KeyListener::keyCallback);
    glfwSetCursorPosCallback(window, MouseListener::cursorPosCallback);
    glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
    glfwSetScrollCallback(window, MouseListener::scrollCallback);

    glfwSetFramebufferSizeCallback(window, GLContext::framebufferSizeCallback);

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
    glfwSwapInterval(0); // 1: V-Sync, 0: Remove fps cap
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    glfwShowWindow(window);

    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    createCapabilities();
  }

  public void update() {
    float currentFrame = (float)glfwGetTime();
    deltaTime = currentFrame - lastFrame;
    lastFrame = currentFrame;
    frames++;

    if ((currentFrame - last) >= 1.0f) {
      glfwSetWindowTitle(window, "FPS: " + frames);
      frames = 0;
      last = currentFrame;
    }

    glfwSwapBuffers(window);
    glfwPollEvents();
  }

  public boolean isCloseRequested() {
    return glfwWindowShouldClose(window);
  }

  public void requestClose() {
    glfwSetWindowShouldClose(window, true);
  }

  public void toggleMouseCursor() {
    if (!mouseCursorToggled) {
      glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    } else {
      glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }
    mouseCursorToggled = !mouseCursorToggled;
  }

  public boolean isMouseCursorToggled() {
    return mouseCursorToggled;
  }

  public void terminate() {
    MemoryManager.terminate();

    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }

  public long getWindow() {
    return window;
  }

  public int getFrames() {
    return frames;
  }

  public float getDeltaTime() {
    return deltaTime;
  }

  private static void framebufferSizeCallback(long window, int width, int height) {
    glViewport(0, 0, width, height);
  }
}
