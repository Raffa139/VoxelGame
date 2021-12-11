package de.re.voxelgame.engine;

import de.re.voxelgame.core.*;
import de.re.voxelgame.util.ResourceLoader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.Version;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class Application {
  public static void main(String[] args) throws IOException, URISyntaxException {
    new Application().run();
  }

  public void run() throws IOException, URISyntaxException {
    System.out.println("LWJGL " + Version.getVersion());

    GLContext context = new GLContext(1080, 720, "OpenGL");
    loop(context);

    context.terminate();
  }

  private void loop(GLContext context) throws IOException, URISyntaxException {
    // Shader
    ResourceLoader.Resource vert = ResourceLoader.locateResource("shader/basic.vert", Application.class);
    ResourceLoader.Resource frag = ResourceLoader.locateResource("shader/basic.frag", Application.class);
    Shader basicShader = new Shader(vert.toPath(), frag.toPath());

    // Texture
    Texture texture = new Texture("images/grass.png");

    Camera camera = new Camera(new Vector3f(0.0f, 10.0f, 0.0f));

    int chunkCount = 10;
    List<Chunk> chunks = new ArrayList<>();
    for (int i = 0; i < chunkCount; i++) {
      for (int j = 0; j < chunkCount; j++) {
        Chunk chunk = new Chunk(new Vector2f(i, j));
        chunk.prepare();
        chunks.add(chunk);
      }
    }

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

      for (Chunk chunk : chunks) {
        Matrix4f model = new Matrix4f();
        model.translate(chunk.getPosition().x*16, 0.0f, chunk.getPosition().y*16);
        basicShader.setMatrix4("iModel", model);

        glBindVertexArray(chunk.getVaoId());
        glDrawArrays(GL_TRIANGLES, 0, chunk.getVertexCount());
        glBindVertexArray(0);
      }

      if (KeyListener.keyPressed(GLFW_KEY_ESCAPE)) {
        context.requestClose();
      }

      if (KeyListener.keyPressed(GLFW_KEY_P)) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
      } else {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
      }

      if (KeyListener.keyPressed(GLFW_KEY_I) && glfwGetTime() > lastPressed + 0.25f) {
        lastPressed = (float) glfwGetTime();
        printDebugInfo(chunkCount);
      }

      camera.update(context.getDeltaTime());

      context.update();
    }

    basicShader.terminate();
  }

  private void printDebugInfo(int chunkCount) {
    System.out.println("Chunks: " + chunkCount*chunkCount);
    System.out.println("Chunk size: " + Chunk.CHUNK_SIZE);
    System.out.println("Blocks/Chunk: " + Chunk.CHUNK_SIZE*Chunk.CHUNK_SIZE);
    System.out.println("Blocks: " + chunkCount * Chunk.CHUNK_SIZE*Chunk.CHUNK_SIZE);
    System.out.println("Faces: " + chunkCount * 2*Chunk.CHUNK_SIZE*Chunk.CHUNK_SIZE+4*16);
    System.out.println("Triangles: " + 2 * chunkCount * 2*Chunk.CHUNK_SIZE*Chunk.CHUNK_SIZE+4*16);
    System.out.println("Vertices: " + 3 * 2 * chunkCount * 2*Chunk.CHUNK_SIZE*Chunk.CHUNK_SIZE+4*16);
  }
}
