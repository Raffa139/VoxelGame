package de.re.voxelgame.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;

import java.io.IOException;
import java.net.URISyntaxException;

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
            -0.5f,  0.5f, 0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.5f, 0.5f,
             0.5f, -0.5f, 0.5f, 0.0f, 0.5f,
             0.5f, -0.5f, 0.5f, 0.0f, 0.5f,
             0.5f,  0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f,  0.5f, 0.5f, 0.5f, 0.0f,

            // Back
             0.5f,  0.5f, -0.5f, 0.5f, 0.0f,
             0.5f, -0.5f, -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.5f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.5f,
            -0.5f,  0.5f, -0.5f, 0.0f, 0.0f,
             0.5f,  0.5f, -0.5f, 0.5f, 0.0f,

            // Left
            -0.5f,  0.5f, -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f,  0.5f, 0.0f, 0.5f,
            -0.5f, -0.5f,  0.5f, 0.0f, 0.5f,
            -0.5f,  0.5f,  0.5f, 0.0f, 0.0f,
            -0.5f,  0.5f, -0.5f, 0.5f, 0.0f,

            // Right
            0.5f,  0.5f,  0.5f, 0.5f, 0.0f,
            0.5f, -0.5f,  0.5f, 0.5f, 0.5f,
            0.5f, -0.5f, -0.5f, 0.0f, 0.5f,
            0.5f, -0.5f, -0.5f, 0.0f, 0.5f,
            0.5f,  0.5f, -0.5f, 0.0f, 0.0f,
            0.5f,  0.5f,  0.5f, 0.5f, 0.0f,

            // Top
             0.5f,  0.5f,  0.5f, 0.5f, 0.0f,
             0.5f,  0.5f, -0.5f, 0.5f, 0.5f,
            -0.5f,  0.5f, -0.5f, 0.0f, 0.5f,
            -0.5f,  0.5f, -0.5f, 0.0f, 0.5f,
            -0.5f,  0.5f,  0.5f, 0.0f, 0.0f,
             0.5f,  0.5f,  0.5f, 0.5f, 0.0f,

            // Bottom
            -0.5f, -0.5f,  0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.5f, 0.5f,
             0.5f, -0.5f, -0.5f, 0.0f, 0.5f,
             0.5f, -0.5f, -0.5f, 0.0f, 0.5f,
             0.5f, -0.5f,  0.5f, 0.0f, 0.0f,
            -0.5f, -0.5f,  0.5f, 0.5f, 0.0f
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
    Texture texture = new Texture("images/grass.png");

    Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 5.0f));

    int chunkSize = 16;
    int chunks = 3;
    float lastPressed = 0.0f;
    while (!context.isCloseRequested()) {
      glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      glEnable(GL_CULL_FACE);
      glEnable(GL_DEPTH_TEST);

      Matrix4f view = camera.getViewMatrix();
      Matrix4f projection = new Matrix4f()
              .perspective((float) Math.toRadians(65.0f), 1080.0f / 720.0f, 0.01f, 1000.0f);

      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D, texture.getId());

      basicShader.use();
      basicShader.setMatrix4("iView", view);
      basicShader.setMatrix4("iProjection", projection);
      basicShader.setFloat("iTime", (float)glfwGetTime());

      glBindVertexArray(vao);
      for (int i = 0; i < chunks; i++) {
        for (int j = 0; j < chunks; j++) {
          for (int x = 0; x < chunkSize; x++) {
            for (int z = 0; z < chunkSize; z++) {
              // Position in world
              int X = x + i * chunkSize;
              int Z = z + j * chunkSize;

              // Max. size in x/z direction
              int SX = (chunkSize-1) + (chunks-1) * chunkSize;
              int SZ = (chunkSize-1) + (chunks-1) * chunkSize;

              Matrix4f model = new Matrix4f();
              model.translate(X, 0.0f, Z);
              basicShader.setMatrix4("iModel", model);

              if ((X == 0 && Z == 0) || (X == 0 && Z == SZ) || (X == SX && Z == 0) || (X == SX && Z == SZ)) { // One of 4 corner cases
                // Corner
                basicShader.setVec3("iColor", new Vector3f(1.0f, 0.0f, 0.0f));
              } else if (((X == 0 || X == SX) && (Z > 0 && Z < SZ)) || ((Z == 0 || Z == SZ) && (X > 0 && X < SX))) { // One of 2x2 edge cases
                // Edge
                basicShader.setVec3("iColor", new Vector3f(0.0f, 0.0f, 1.0f));
              } else if ((X > 0 && X < SX) && (Z > 0 && Z < SZ)) { // Remaining middle cases
                // Middle
                basicShader.setVec3("iColor", new Vector3f(0.0f, 1.0f, 0.0f));
              }

              glDrawArrays(GL_TRIANGLES, 0, vertices.length);
            }
          }
        }
      }
      glBindVertexArray(0);

      if (KeyListener.keyPressed(GLFW_KEY_ESCAPE)) {
        context.requestClose();
      }

      if (KeyListener.keyPressed(GLFW_KEY_P)) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
      } else {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
      }

      if (KeyListener.keyPressed(GLFW_KEY_UP) && glfwGetTime() > lastPressed + 0.25f) {
        chunks++;
        lastPressed = (float) glfwGetTime();

        System.out.println("Chunks: " + chunks*chunks);
        System.out.println("Chunk size: " + chunkSize);
        System.out.println("Blocks/Chunk: " + chunkSize*chunkSize);
        System.out.println("Blocks: " + chunks * chunkSize*chunkSize);
        System.out.println("Faces: " + 6 * chunks * chunkSize*chunkSize);
        System.out.println("Triangles: " + 2 * 6 * chunks * chunkSize*chunkSize);
      } else if (KeyListener.keyPressed(GLFW_KEY_DOWN) && glfwGetTime() > lastPressed + 0.25f) {
        chunks = chunks == 1 ? 1 : chunks-1;
        lastPressed = (float) glfwGetTime();

        System.out.println("Chunks: " + chunks*chunks);
        System.out.println("Chunk size: " + chunkSize);
        System.out.println("Blocks/Chunk: " + chunkSize*chunkSize);
        System.out.println("Blocks: " + chunks * chunkSize*chunkSize);
        System.out.println("Faces: " + 6 * chunks * chunkSize*chunkSize);
        System.out.println("Triangles: " + 2 * 6 * chunks * chunkSize*chunkSize);
      }

      if (KeyListener.keyPressed(GLFW_KEY_RIGHT) && glfwGetTime() > lastPressed + 0.25f) {
        chunkSize++;
        lastPressed = (float) glfwGetTime();

        System.out.println("Chunks: " + chunks*chunks);
        System.out.println("Chunk size: " + chunkSize);
        System.out.println("Blocks/Chunk: " + chunkSize*chunkSize);
        System.out.println("Blocks: " + chunks * chunkSize*chunkSize);
        System.out.println("Faces: " + 6 * chunks * chunkSize*chunkSize);
        System.out.println("Triangles: " + 2 * 6 * chunks * chunkSize*chunkSize);
      } else if (KeyListener.keyPressed(GLFW_KEY_LEFT) && glfwGetTime() > lastPressed + 0.25f) {
        chunkSize = chunkSize == 1 ? 1 : chunkSize-1;
        lastPressed = (float) glfwGetTime();

        System.out.println("Chunks: " + chunks*chunks);
        System.out.println("Chunk size: " + chunkSize);
        System.out.println("Blocks/Chunk: " + chunkSize*chunkSize);
        System.out.println("Blocks: " + chunks * chunkSize*chunkSize);
        System.out.println("Faces: " + 6 * chunks * chunkSize*chunkSize);
        System.out.println("Triangles: " + 2 * 6 * chunks * chunkSize*chunkSize);
      }

      camera.update(context.getDeltaTime());

      context.update();
    }

    basicShader.terminate();
  }
}
