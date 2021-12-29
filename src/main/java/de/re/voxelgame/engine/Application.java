package de.re.voxelgame.engine;

import de.re.voxelgame.core.*;
import de.re.voxelgame.engine.world.Chunk;
import de.re.voxelgame.engine.world.ChunkLoader;
import de.re.voxelgame.core.util.ResourceLoader;
import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.Version;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import static de.re.voxelgame.core.util.Vectors.*;
import static de.re.voxelgame.engine.world.Chunk.CHUNK_SIZE;
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

    int crossHairVao = glGenVertexArrays();
    glBindVertexArray(crossHairVao);

    int crossHairVbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, crossHairVbo);
    glBufferData(GL_ARRAY_BUFFER, crossHairVertices, GL_STATIC_DRAW);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0L);

    glBindVertexArray(0);

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

    Camera camera = new Camera(new Vector3f(0.0f, 10.0f, 0.0f));

    OpenSimplexNoise noise = new OpenSimplexNoise(LocalDateTime.now().getLong(ChronoField.NANO_OF_DAY));
    List<Chunk> chunks = new ArrayList<>();
    int chunkCount = 20;
    int chunkStacks = 6;

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

      chunkShader.use();
      chunkShader.setMatrix4("iView", view);
      chunkShader.setMatrix4("iProjection", projection);
      chunkShader.setFloat("iTime", (float)glfwGetTime());

      // Mouse picking
      // https://gist.github.com/DomNomNom/46bb1ce47f68d255fd5d
      // (https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter23/chapter23.html)
      float rx = (float) ((2.0 * MouseListener.getLastPosX()) / 1080.0 - 1.0f);
      float ry = (float) ((2.0 * MouseListener.getLastPosY()) / 720.0 - 1.0f);
      float rz = 1.0f;
      Matrix4f inverseView = view.invert(new Matrix4f());
      Matrix4f inverseProjection = projection.invert(new Matrix4f());

      // Ray in normalized device coords
      Vector3f rNdc = new Vector3f(rx, -ry, rz);
      //Vector3f rNdc = camera.getFront();

      // Ray in projected space
      Vector4f rClip = new Vector4f(rNdc.x, rNdc.y, -1.0f, 1.0f);

      // Ray in view space
      Vector4f rView = rClip.mul(inverseProjection, new Vector4f());
      rView = new Vector4f(rView.x, rView.y, -1.0f, 0.0f); // (only x/y needed to 'un-project')

      // Ray in world space
      Vector4f rWorld = rView.mul(inverseView, new Vector4f());
      Vector3f ray = new Vector3f(rWorld.x, rWorld.y, rWorld.z).normalize();

      float currentFrame = (float)glfwGetTime();
      if ((currentFrame - last) >= 0.0001f) {
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

      Vector3f intersectionPos = null;
      for (Chunk chunk : chunks) {
        if (chunk.containsVertices()) {
          Vector3f chunkWorldPos = new Vector3f(
              chunk.getPosition().x * CHUNK_SIZE,
              chunk.getPosition().y * CHUNK_SIZE,
              chunk.getPosition().z * CHUNK_SIZE
          );
          Vector3f boxMin = new Vector3f(
              chunkWorldPos.x,
              chunkWorldPos.y,
              chunkWorldPos.z
          );
          Vector3f boxMax = new Vector3f(
              chunkWorldPos.x + (CHUNK_SIZE - 1),
              chunkWorldPos.y + (CHUNK_SIZE - 1),
              chunkWorldPos.z + (CHUNK_SIZE - 1)
          );

          Vector3f ro = camera.getPos();
          Vector3f rd = camera.getFront().normalize();

          Vector3f tMin = div(sub(boxMin, ro), rd);
          Vector3f tMax = div(sub(boxMax, ro), rd);
          Vector3f t1 = min(tMin, tMax);
          Vector3f t2 = max(tMin, tMax);
          float tNear = Math.max(Math.max(t1.x, t1.y), t1.z);
          float tFar = Math.min(Math.min(t2.x, t2.y), t2.z);
          boolean intersects = tNear < tFar;
          //Vector2f intersection = new Vector2f(tNear, tFar);

          if (intersects) {
            intersectionPos = chunk.getPosition();
            break;
          }
        }
      }

      for (Chunk chunk : chunks) {
        if (chunk.containsVertices()) {
          Matrix4f model = new Matrix4f();
          model.translate(
              chunk.getPosition().x * CHUNK_SIZE,
              chunk.getPosition().y * CHUNK_SIZE,
              chunk.getPosition().z * CHUNK_SIZE);
          chunkShader.setMatrix4("iModel", model);

          if (chunk.getPosition().equals(intersectionPos)) {
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

      if (KeyListener.keyPressed(GLFW_KEY_E) && glfwGetTime() > lastPressed + 0.25f) {
        lastPressed = (float) glfwGetTime();
        context.toggleMouseCursor();
      }

      camera.update(context.getDeltaTime(), !context.isMouseCursorToggled());

      context.update();
    }

    chunkShader.terminate();
    hudShader.terminate();
  }
}
