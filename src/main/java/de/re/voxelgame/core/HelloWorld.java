package de.re.voxelgame.core;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

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

    // Geometry
    float[] vertices = {
            -0.5f, -0.5f, 0.0f,
             0.0f,  0.5f, 0.0f,
             0.5f, -0.5f, 0.0f
    };

    int vao = glGenVertexArrays();
    glBindVertexArray(vao);

    int vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L);

    // Shader
    String vert =
            "#version 330 core\n" +
            "layout (location = 0) in vec3 pos;\n" +
            "void main() {\n" +
            "gl_Position = vec4(pos, 1.0);\n" +
            "}\n";

    String frag =
            "#version 330 core\n" +
            "uniform float time;\n" +
            "out vec4 FragColor;\n" +
            "void main() {\n" +
            "FragColor = vec4((sin(time/2.0)+1.0) / 2.0, (sin(time*2.0)+1.0) / 2.0, (sin(time)+1.0) / 2.0, 1.0);\n" +
            "}";

    Shader shader = new Shader(vert, frag);

    while (!glfwWindowShouldClose(window)) {
      glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT);

      shader.use();
      shader.setFloat("time", (float)glfwGetTime());
      glBindVertexArray(vao);
      glDrawArrays(GL_TRIANGLES, 0, 3);
      glBindVertexArray(0);

      glfwSwapBuffers(window);
      glfwPollEvents();
    }
  }
}
