package de.re.voxelgame.core;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

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
            // Front
            -1.0f,  1.0f, 1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
             1.0f, -1.0f, 1.0f, 0.0f, 1.0f,
             1.0f, -1.0f, 1.0f, 0.0f, 1.0f,
             1.0f,  1.0f, 1.0f, 0.0f, 0.0f,
            -1.0f,  1.0f, 1.0f, 1.0f, 0.0f,

            // Back
             1.0f,  1.0f, -1.0f, 1.0f, 0.0f,
             1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f, 0.0f, 1.0f,
            -1.0f, -1.0f, -1.0f, 0.0f, 1.0f,
            -1.0f,  1.0f, -1.0f, 0.0f, 0.0f,
             1.0f,  1.0f, -1.0f, 1.0f, 0.0f,

            // Left
            -1.0f,  1.0f, -1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f,  1.0f, 0.0f, 1.0f,
            -1.0f, -1.0f,  1.0f, 0.0f, 1.0f,
            -1.0f,  1.0f,  1.0f, 0.0f, 0.0f,
            -1.0f,  1.0f, -1.0f, 1.0f, 0.0f,

            // Right
            1.0f,  1.0f,  1.0f, 1.0f, 0.0f,
            1.0f, -1.0f,  1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, -1.0f, 0.0f, 1.0f,
            1.0f, -1.0f, -1.0f, 0.0f, 1.0f,
            1.0f,  1.0f, -1.0f, 0.0f, 0.0f,
            1.0f,  1.0f,  1.0f, 1.0f, 0.0f,

            // Top
             1.0f,  1.0f,  1.0f, 1.0f, 0.0f,
             1.0f,  1.0f, -1.0f, 1.0f, 1.0f,
            -1.0f,  1.0f, -1.0f, 0.0f, 1.0f,
            -1.0f,  1.0f, -1.0f, 0.0f, 1.0f,
            -1.0f,  1.0f,  1.0f, 0.0f, 0.0f,
             1.0f,  1.0f,  1.0f, 1.0f, 0.0f,

            // Bottom
            -1.0f, -1.0f,  1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
             1.0f, -1.0f, -1.0f, 0.0f, 1.0f,
             1.0f, -1.0f, -1.0f, 0.0f, 1.0f,
             1.0f, -1.0f,  1.0f, 0.0f, 0.0f,
            -1.0f, -1.0f,  1.0f, 1.0f, 0.0f
    };

    int vao = glGenVertexArrays();
    glBindVertexArray(vao);

    int vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0L);
    glEnableVertexAttribArray(1);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 3 * 4);

    glBindVertexArray(0);

    // Shader
    ResourceLoader.Resource vert = ResourceLoader.locateResource("shader/basic.vert", HelloWorld.class);
    ResourceLoader.Resource frag = ResourceLoader.locateResource("shader/basic.frag", HelloWorld.class);
    Shader basicShader = new Shader(vert.toPath(), frag.toPath());

    // Texture
    PNGDecoder decoder =
            new PNGDecoder(ResourceLoader.locateResource("images/grass.png", HelloWorld.class).toFileInputStream());
    ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
    decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
    buffer.flip();

    int texture = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    glGenerateMipmap(GL_TEXTURE_2D);

    Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 5.0f));

    while (!context.isCloseRequested()) {
      glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      glEnable(GL_CULL_FACE);

      Matrix4f model = new Matrix4f();
      Matrix4f view = camera.getViewMatrix();
      Matrix4f projection = new Matrix4f()
              .perspective((float) Math.toRadians(45.0f), 1080.0f / 720.0f, 0.01f, 1000.0f);

      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D, texture);

      basicShader.use();
      basicShader.setMatrix4("iModel", model);
      basicShader.setMatrix4("iView", view);
      basicShader.setMatrix4("iProjection", projection);
      basicShader.setFloat("iTime", (float)glfwGetTime());

      glBindVertexArray(vao);
      glDrawArrays(GL_TRIANGLES, 0, vertices.length);
      glBindVertexArray(0);

      if (KeyListener.keyPressed(GLFW_KEY_ESCAPE)) {
        context.requestClose();
      }

      camera.update(context.getDeltaTime());

      context.update();
    }

    basicShader.terminate();
  }
}
