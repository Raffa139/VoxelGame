package de.re.voxelgame.engine;

import de.re.voxelgame.core.*;
import de.re.voxelgame.engine.world.Chunk;
import de.re.voxelgame.engine.world.ChunkLoader;
import de.re.voxelgame.core.util.ResourceLoader;
import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
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
    String[] textureFiles = {
        "textures/cobblestone.png",
        "textures/dirt.png",
        "textures/sand.png",
        "textures/grass_side.png",
        "textures/grass.png",
        "textures/missing.png",
        "textures/water.png",
        "textures/wood.png",
        "textures/leaves.png",
        "textures/cactus_side.png"
    };
    TextureArray textureArray = new TextureArray(16, 16, textureFiles);

    Camera camera = new Camera(new Vector3f(0.0f, 10.0f, 0.0f));

    OpenSimplexNoise noise = new OpenSimplexNoise(LocalDateTime.now().getLong(ChronoField.NANO_OF_DAY));
    List<Chunk> chunks = new ArrayList<>();
    int chunkCount = 20;
    int chunkStacks = 6;
    /*for (int x = 0; x < chunkCount; x++) {
      for (int z = 0; z < chunkCount; z++) {
        for (int y = 0; y < chunkStacks; y++) {
          chunks.add(ChunkLoader.loadChunkNoise(new Vector3f(x, y, z), noise));
        }
      }
    }*/

    int x = 0;
    int z = 0;
    int y = 0;
    float last = 0.0f;

    float lastPressed = 0.0f;
    while (!context.isCloseRequested()) {
      glClearColor(0.2f, 0.6f, 1.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      glEnable(GL_CULL_FACE);
      glEnable(GL_DEPTH_TEST);

      Matrix4f view = camera.getViewMatrix();
      Matrix4f projection = new Matrix4f()
              .perspective((float) Math.toRadians(65.0f), 1080.0f / 720.0f, 0.01f, 1000.0f);

      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D_ARRAY, textureArray.getId());

      basicShader.use();
      basicShader.setMatrix4("iView", view);
      basicShader.setMatrix4("iProjection", projection);
      basicShader.setFloat("iTime", (float)glfwGetTime());

      float currentFrame = (float)glfwGetTime();
      if ((currentFrame - last) >= 0.005f) {
        last = currentFrame;

        if (x < chunkCount) {
          System.out.println("Chunk update");
          System.out.println("X: " + x);
          System.out.println("Z: " + z);
          System.out.println("Y: " + y);

          if (z < chunkCount) {
            if (y < chunkStacks) {
              chunks.add(ChunkLoader.loadChunkNoise(new Vector3f(x, y, z), noise));
              y++;
            }

            if (y == chunkStacks) {
              z++;
              y = 0;
            }
          }

          if (z == chunkCount) {
            x++;
            z = 0;
          }
        }
      }

      for (Chunk chunk : chunks) {
        if (chunk.containsVertices()) {
          Matrix4f model = new Matrix4f();
          model.translate(
              chunk.getPosition().x * Chunk.CHUNK_SIZE,
              chunk.getPosition().y * Chunk.CHUNK_SIZE,
              chunk.getPosition().z * Chunk.CHUNK_SIZE);
          basicShader.setMatrix4("iModel", model);

          glBindVertexArray(chunk.getVaoId());
          glDrawArrays(GL_TRIANGLES, 0, chunk.getVertexCount());
          glBindVertexArray(0);
        }
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
        System.out.println("Info");
      }

      camera.update(context.getDeltaTime());

      context.update();
    }

    basicShader.terminate();
  }
}
