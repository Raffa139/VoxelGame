package de.re.voxelgame.engine;

import de.re.voxelgame.core.*;
import de.re.voxelgame.core.util.Vectors;
import de.re.voxelgame.engine.intersection.AABB;
import de.re.voxelgame.engine.intersection.Ray;
import de.re.voxelgame.engine.intersection.RayCaster;
import de.re.voxelgame.engine.voxel.Voxel;
import de.re.voxelgame.engine.voxel.VoxelFace;
import de.re.voxelgame.engine.voxel.VoxelType;
import de.re.voxelgame.engine.voxel.VoxelVertex;
import de.re.voxelgame.engine.world.Chunk;
import de.re.voxelgame.core.util.ResourceLoader;
import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import de.re.voxelgame.engine.world.ChunkManager;
import de.re.voxelgame.engine.world.WorldPosition;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;

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

    ResourceLoader.Resource aabbVert = ResourceLoader.locateResource("shader/chunkAABB.vert", Application.class);
    ResourceLoader.Resource aabbFrag = ResourceLoader.locateResource("shader/chunkAABB.frag", Application.class);
    Shader chunkAABBShader = new Shader(aabbVert.toPath(), aabbFrag.toPath());

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

    // Chunk border
    Voxel border = new Voxel(VoxelType.MISSING, false,
        VoxelFace.FRONT,
        VoxelFace.BACK,
        VoxelFace.LEFT,
        VoxelFace.RIGHT,
        VoxelFace.TOP,
        VoxelFace.BOTTOM);
    List<VoxelVertex> borderVertices = border.getVertices();
    float[] borderVertexData = new float[borderVertices.size()*3];
    for (int i = 0; i < borderVertexData.length; i+=3) {
      VoxelVertex v = borderVertices.get((int) Math.floor(i/3.0));
      borderVertexData[i] = v.getPosition().x;
      borderVertexData[i+1] = v.getPosition().y;
      borderVertexData[i+2] = v.getPosition().z;
    }

    int borderVaoId = MemoryManager
        .allocateVao()
        .bufferData(borderVertexData, GL_STATIC_DRAW)
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

    DebugCamera camera = new DebugCamera(new WorldPosition(0.0f, 10.0f, 0.0f));

    OpenSimplexNoise noise = new OpenSimplexNoise(LocalDateTime.now().getLong(ChronoField.NANO_OF_DAY));
    ChunkManager chunkManager = new ChunkManager(noise);

    float lastPressed = 0.0f;
    WorldPosition lastVoxelInCrossHair = new WorldPosition(0.0f);
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

      chunkManager.generate(currentFrameTime, 0.0001f);

      // Cross-hair voxel intersection
      WorldPosition voxelInCrossHair = null;
      WorldPosition placeableVoxel = null;
      boolean crossHairOnBlock = false;
      for (float t = 0.0f; t < 8.0f; t+=0.1f) {
        voxelInCrossHair = new WorldPosition(Vectors.add(camera.getPos(), Vectors.mul(camera.getFront().normalize(), t)));
        Vector3f chunkPos = voxelInCrossHair.getCurrentChunkPosition();
        Vector3f voxelPos = voxelInCrossHair.getAbsolutePositionInCurrentChunk();
        Chunk chunk = chunkManager.getChunkPositionMap().get(chunkPos);

        byte voxelId = chunk != null ? chunk.getVoxelId((int) voxelPos.x, (int) voxelPos.y, (int) voxelPos.z) : 0;
        if (voxelId != 0) {
          crossHairOnBlock = true;
          placeableVoxel = new WorldPosition(Vectors.add(camera.getPos(), Vectors.mul(camera.getFront().normalize(), t-0.1f)));
          break;
        }
      }

      if (!context.isMouseCursorToggled()) {
        if (!crossHairOnBlock || !voxelInCrossHair.getCurrentChunkPosition().equals(lastVoxelInCrossHair.getCurrentChunkPosition())) {
          chunkManager.reloadChunk(voxelInCrossHair.getCurrentChunkPosition(), null);
          chunkManager.reloadChunk(lastVoxelInCrossHair.getCurrentChunkPosition(), null);
          lastVoxelInCrossHair = voxelInCrossHair;
        } else {
          chunkManager.reloadChunk(voxelInCrossHair.getCurrentChunkPosition(), voxelInCrossHair.getAbsolutePositionInCurrentChunk());
        }
      }

      // Voxel placement
      if (!context.isMouseCursorToggled()) {
        if (MouseListener.buttonPressed(GLFW_MOUSE_BUTTON_1) && currentFrameTime > lastPressed + 0.25f) {
          lastPressed = currentFrameTime;

          if (crossHairOnBlock) {
            Vector3f chunkPos = placeableVoxel.getCurrentChunkPosition();
            Vector3f voxelPos = placeableVoxel.getAbsolutePositionInCurrentChunk();
            Chunk chunk = chunkManager.getChunkPositionMap().get(chunkPos);
            if (chunk != null) {
              chunk.placeVoxel((int) voxelPos.x, (int) voxelPos.y, (int) voxelPos.z, VoxelType.WOOD);
            }
          }
        }

        if (MouseListener.buttonPressed(GLFW_MOUSE_BUTTON_2) && currentFrameTime > lastPressed + 0.25f) {
          lastPressed = currentFrameTime;

          if (crossHairOnBlock) {
            Vector3f chunkPos = voxelInCrossHair.getCurrentChunkPosition();
            Vector3f voxelPos = voxelInCrossHair.getAbsolutePositionInCurrentChunk();
            Chunk chunk = chunkManager.getChunkPositionMap().get(chunkPos);
            if (chunk != null) {
              chunk.removeVoxel((int) voxelPos.x, (int) voxelPos.y, (int) voxelPos.z);
            }
          }
        }
      }

      // Chunk mouse-cursor intersection
      Ray ray = RayCaster.fromMousePosition(MouseListener.getLastPosX(), MouseListener.getLastPosY(), camera, projection, 1080.0f, 720.0f);
      WorldPosition intersectionPos = null;
      if (context.isMouseCursorToggled()) {
        for (Chunk chunk : chunkManager.getChunks()) {
          AABB chunkBounding = chunk.getBoundingBox();
          boolean intersects = ray.intersectsAABB(chunkBounding);

          if (intersects) {
            intersectionPos = chunk.getRelativePosition();
            break;
          }
        }
      }

      chunkShader.use();
      chunkShader.setMatrix4("iView", view);
      chunkShader.setMatrix4("iProjection", projection);
      chunkShader.setFloat("iTime", currentFrameTime);
      chunkShader.setVec3("iColor", new Vector3f(0.0f, 0.0f, 0.5f));

      chunkAABBShader.use();
      chunkAABBShader.setMatrix4("iView", view);
      chunkAABBShader.setMatrix4("iProjection", projection);

      for (Chunk chunk : chunkManager.getChunks()) {
        if (chunk.hasMesh()) {
          Matrix4f model = new Matrix4f();
          model.translate(chunk.getWorldPosition().getVector());

          chunkShader.use();
          chunkShader.setMatrix4("iModel", model);

          glBindVertexArray(chunk.getMesh().getVaoId());
          glDrawArrays(GL_TRIANGLES, 0, chunk.getMesh().getVertexCount());
          glBindVertexArray(0);
        }

        // Draw chunk border
        if (context.isMouseCursorToggled()) {
          Matrix4f model = new Matrix4f();
          model.translate(chunk.getWorldPosition().getVector()).scale(CHUNK_SIZE);

          chunkAABBShader.use();
          chunkAABBShader.setMatrix4("iModel", model);
          if (chunk.getRelativePosition().equals(intersectionPos) && context.isMouseCursorToggled()) {
            chunkAABBShader.setVec3("iColor", new Vector3f(1.0f, 0.0f, 0.0f));
          } else {
            chunkAABBShader.setVec3("iColor", new Vector3f(1.0f, 1.0f, 1.0f));
          }

          glBindVertexArray(borderVaoId);
          glDrawArrays(GL_LINES, 0, borderVertexData.length);
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

        /*System.out.println();
        System.out.println("Voxel: ");
        System.out.println(
            "ABS X: " + voxelInCrossHair.getAbsolutePositionInCurrentChunk().x +
          ", ABS Y: " + voxelInCrossHair.getAbsolutePositionInCurrentChunk().y +
          ", ABS Z: " + voxelInCrossHair.getAbsolutePositionInCurrentChunk().z);
        System.out.println(
            "X: " + voxelInCrossHair.getPositionInCurrentChunk().x +
          ", Y: " + voxelInCrossHair.getPositionInCurrentChunk().y +
          ", Z: " + voxelInCrossHair.getPositionInCurrentChunk().z);

        System.out.println();
        System.out.println("Camera: ");
        System.out.println(
            "ABS X: " + camera.getWorldPosition().getAbsolutePositionInCurrentChunk().x +
          ", ABS Y: " + camera.getWorldPosition().getAbsolutePositionInCurrentChunk().y +
          ", ABS Z: " + camera.getWorldPosition().getAbsolutePositionInCurrentChunk().z);
        System.out.println(
            "X: " + camera.getWorldPosition().getPositionInCurrentChunk().x +
          ", Y: " + camera.getWorldPosition().getPositionInCurrentChunk().y +
          ", Z: " + camera.getWorldPosition().getPositionInCurrentChunk().z);*/
      }

      camera.update(context.getDeltaTime(), !context.isMouseCursorToggled());

      context.update();
    }

    textureArray.cleanup();
    chunkShader.terminate();
    chunkAABBShader.terminate();
    hudShader.terminate();
  }
}
