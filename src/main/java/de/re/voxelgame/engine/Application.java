package de.re.voxelgame.engine;

import de.re.voxelgame.core.*;
import de.re.voxelgame.engine.gui.HudRenderer;
import de.re.voxelgame.engine.world.*;
import de.re.voxelgame.core.util.ResourceLoader;
import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import org.joml.Matrix4f;
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

    GLContext context = GLContext.init(1080, 720, "OpenGL");
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

    OpenSimplexNoise noise = new OpenSimplexNoise(LocalDateTime.now().getLong(ChronoField.NANO_OF_DAY));
    ChunkManager chunkManager = new ChunkManager(noise);
    VoxelCamera camera = new VoxelCamera(new WorldPosition(0.0f, 10.0f, 0.0f), new CrossHairTarget(chunkManager));
    ChunkInteractionManager interactionManager = new ChunkInteractionManager(chunkManager, camera);

    ChunkRenderer chunkRenderer = new ChunkRenderer(context, chunkShader, chunkAABBShader);
    HudRenderer hudRenderer = new HudRenderer(context, hudShader);

    float lastPressed = 0.0f;
    while (!context.isCloseRequested()) {
      glClearColor(0.2f, 0.6f, 1.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      glEnable(GL_CULL_FACE);
      glEnable(GL_DEPTH_TEST);

      float currentFrameTime = (float) glfwGetTime();

      chunkManager.generate(currentFrameTime, 0.0001f);

      // Cross-hair voxel intersection
      camera.update(context.getDeltaTime(), !context.isMouseCursorToggled());

      if (!context.isMouseCursorToggled()) {
        interactionManager.highlightVoxel();
      }

      // Voxel placement
      if (!context.isMouseCursorToggled()) {
        if (MouseListener.buttonPressed(GLFW_MOUSE_BUTTON_1) && currentFrameTime > lastPressed + 0.25f) {
          lastPressed = currentFrameTime;
          interactionManager.placeVoxel();
        }

        if (MouseListener.buttonPressed(GLFW_MOUSE_BUTTON_2) && currentFrameTime > lastPressed + 0.25f) {
          lastPressed = currentFrameTime;
          interactionManager.removeVoxel();
        }
      }

      Matrix4f view = camera.getViewMatrix();
      Matrix4f projection = new Matrix4f()
          .perspective((float) Math.toRadians(65.0f), context.getAspectRatio(), 0.01f, 1000.0f);

      // Chunk mouse-cursor intersection
      WorldPosition intersectionPos = null;
      if (context.isMouseCursorToggled()) {
        intersectionPos = interactionManager.calculateMouseCursorIntersection(projection, context.getWindowWidth(), context.getWindowHeight());
      }

      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D_ARRAY, textureArray.getId());
      chunkRenderer.render(chunkManager.getChunks(), view, projection, intersectionPos);
      glBindTexture(GL_TEXTURE_2D_ARRAY, 0);

      hudRenderer.render();

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
