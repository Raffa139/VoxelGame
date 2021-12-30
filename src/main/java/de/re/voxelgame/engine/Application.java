package de.re.voxelgame.engine;

import de.re.voxelgame.core.*;
import de.re.voxelgame.engine.world.Chunk;
import de.re.voxelgame.core.util.ResourceLoader;
import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import de.re.voxelgame.engine.world.ChunkManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

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
    ResourceLoader.Resource chunkVert = ResourceLoader.locateResource("shader/chunk.vert", Application.class);
    ResourceLoader.Resource chunkFrag = ResourceLoader.locateResource("shader/chunk.frag", Application.class);
    Shader chunkShader = new Shader(chunkVert.toPath(), chunkFrag.toPath());

    ResourceLoader.Resource hudVert = ResourceLoader.locateResource("shader/basicHud.vert", Application.class);
    ResourceLoader.Resource hudFrag = ResourceLoader.locateResource("shader/basicHud.frag", Application.class);
    Shader hudShader = new Shader(hudVert.toPath(), hudFrag.toPath());

    // Cross-hair
    float[] crossHairVertices = {
        -0.5f,  0.05f, 0.0f,
        -0.5f, -0.05f, 0.0f,
         0.5f, -0.05f, 0.0f,
         0.5f, -0.05f, 0.0f,
         0.5f,  0.05f, 0.0f,
        -0.5f,  0.05f, 0.0f
    };

    int crossHairVao = MemoryManager
        .allocateVao()
        .bufferData(crossHairVertices, GL_STATIC_DRAW)
        .enableAttribArray(0)
        .attribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0L)
        .doFinal();

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
        "textures/cactus_side.png",
        "textures/gravel.png"
    };
    TextureArray textureArray = new TextureArray(16, 16, textureFiles);

    DebugCamera camera = new DebugCamera(new Vector3f(0.0f, 10.0f, 0.0f));

    OpenSimplexNoise noise = new OpenSimplexNoise(LocalDateTime.now().getLong(ChronoField.NANO_OF_DAY));
    ChunkManager chunkManager = new ChunkManager(noise);

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

      float currentFrameTime = (float) glfwGetTime();

      chunkShader.use();
      chunkShader.setMatrix4("iView", view);
      chunkShader.setMatrix4("iProjection", projection);
      chunkShader.setFloat("iTime", currentFrameTime);

      chunkManager.update(currentFrameTime, 0.0001f);

      for (Chunk chunk : chunkManager.getChunks()) {
        if (chunk.containsVertices()) {
          Matrix4f model = new Matrix4f();
          model.translate(chunk.getWorldPosition());
          chunkShader.setMatrix4("iModel", model);

          if (chunk.getPosition().equals(camera.getPositionOfCurrentChunk())) {
            chunkShader.setVec3("iColor", new Vector3f(0.0f, 0.0f, 0.5f));
          } else {
            chunkShader.setVec3("iColor", new Vector3f(0.0f, 0.0f, 0.0f));
          }

          glBindVertexArray(chunk.getVaoId());
          glDrawArrays(GL_TRIANGLES, 0, chunk.getVertexCount());
          glBindVertexArray(0);
        }
      }

      hudShader.use();
      hudShader.setMatrix4("iModel", new Matrix4f().scale(0.05f));

      glBindVertexArray(crossHairVao);
      glDrawArrays(GL_TRIANGLES, 0, crossHairVertices.length);

      hudShader.setMatrix4("iModel", new Matrix4f()
          .rotate((float) Math.toRadians(90.0), new Vector3f(0.0f, 0.0f, 1.0f))
          .scale(0.05f));
      glDrawArrays(GL_TRIANGLES, 0, crossHairVertices.length);
      glBindVertexArray(0);

      if (KeyListener.keyPressed(GLFW_KEY_ESCAPE)) {
        context.requestClose();
      }

      if (KeyListener.keyPressed(GLFW_KEY_P)) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
      } else {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
      }

      if (KeyListener.keyPressed(GLFW_KEY_E) && currentFrameTime > lastPressed + 0.25f) {
        lastPressed = currentFrameTime;
        context.toggleMouseCursor();
        System.out.println(
            "X: " + camera.getPositionInCurrentChunk().x +
          ", Y: " + camera.getPositionInCurrentChunk().y +
          ", Z: " + camera.getPositionInCurrentChunk().z);
      }

      camera.update(context.getDeltaTime(), !context.isMouseCursorToggled());

      context.update();
    }

    textureArray.cleanup();
    chunkShader.terminate();
    hudShader.terminate();
  }
}
