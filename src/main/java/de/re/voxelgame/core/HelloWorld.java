package de.re.voxelgame.core;

import org.lwjgl.Version;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class HelloWorld {
  public static void main(String[] args) throws IOException, URISyntaxException {
    new HelloWorld().run();
  }

  public void run() throws IOException, URISyntaxException {
    System.out.println("LWJGL " + Version.getVersion());

    GLContext context = new GLContext(1080, 720, "OpenGL");
    loop(context);

    context.terminate();
  }

  private void loop(GLContext context) throws IOException, URISyntaxException {
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
    ResourceLoader.Resource vert = ResourceLoader.locateResource("shader/basic.vert", HelloWorld.class);
    ResourceLoader.Resource frag = ResourceLoader.locateResource("shader/basic.frag", HelloWorld.class);
    Shader basicShader = new Shader(vert.toPath(), frag.toPath());

    while (!context.isCloseRequested()) {
      glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT);

      basicShader.use();
      basicShader.setFloat("iTime", (float)glfwGetTime());
      glBindVertexArray(vao);
      glDrawArrays(GL_TRIANGLES, 0, 3);
      glBindVertexArray(0);

      if (KeyListener.keyPressed(GLFW_KEY_ESCAPE)) {
        context.requestClose();
      }

      context.update();
    }

    basicShader.terminate();
    context.terminate();
  }
}
