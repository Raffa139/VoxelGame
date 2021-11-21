package de.re.application.core;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;

public class HelloWorld {
  private static final long NULL = 0L;

  private long window;

  public static void main(String[] args) {
    new HelloWorld().run();
  }

  public void run() {
    System.out.println("LWJGL " + Version.getVersion());

    init();
    loop();

    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    glfwTerminate();
    glfwSetErrorCallback(null).free(); // Free error callback
  }

  private void init() {
    // Setup error callback. Default implementation will print error messages in System.err
    GLFWErrorCallback.createPrint(System.err).set();

    // Init GLFW
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW!");
    }

    // Configure GLFW
    glfwDefaultWindowHints(); // Optional, current hints are already the defaults
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

    // Create window
    window = glfwCreateWindow(1080, 720, "Hello World", NULL, NULL);
    if (window == NULL) {
      throw new IllegalStateException("Failed to create GLFW window!");
    }

    // Setup key callback
    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true);
      }
    });

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
    glfwSwapInterval(1); // V-Sync
    glfwShowWindow(window);
  }

  private void loop() {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    createCapabilities();

    glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

    while (!glfwWindowShouldClose(window)) {
      glClear(GL_COLOR_BUFFER_BIT);
      glfwSwapBuffers(window);
      glfwPollEvents();
    }
  }
}
