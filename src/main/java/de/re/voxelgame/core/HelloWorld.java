package de.re.voxelgame.core;

import org.lwjgl.Version;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class HelloWorld {
  public static void main(String[] args) {
    new HelloWorld().run();
  }

  public void run() {
    System.out.println("LWJGL " + Version.getVersion());

    GLContext context = new GLContext(1080, 720, "OpenGL");
    loop(context);

    context.terminate();
  }

  private void loop(GLContext context) {
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

    while (!context.isCloseRequested()) {
      glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT);

      shader.use();
      shader.setFloat("time", (float)glfwGetTime());
      glBindVertexArray(vao);
      glDrawArrays(GL_TRIANGLES, 0, 3);
      glBindVertexArray(0);

      context.update();
    }
  }
}
