package de.re.voxelgame;

import de.re.engine.GLApplication;
import de.re.engine.KeyListener;
import de.re.engine.MouseListener;
import de.re.engine.ecs.system.BasicKeyBindings;
import de.re.engine.ecs.system.LoadingSystem;
import de.re.engine.objects.Framebuffer;
import de.re.engine.objects.sampler.Sampler2D;
import de.re.engine.objects.sampler.Sampler2DArray;
import de.re.engine.objects.shader.Shader;
import de.re.engine.util.ResourceLoader;
import de.re.voxelgame.gui.HudRenderer;
import de.re.voxelgame.skybox.Skybox;
import de.re.voxelgame.voxel.VoxelType;
import de.re.voxelgame.noise.OpenSimplexNoise;
import de.re.voxelgame.world.ChunkInteractionManager;
import de.re.voxelgame.world.ChunkManager;
import de.re.voxelgame.world.ChunkRenderer;
import de.re.voxelgame.world.WorldPosition;
import org.lwjgl.Version;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class VoxelApplication extends GLApplication {
  public VoxelApplication(int width, int height, String title) {
    super(width, height, title);
    System.out.println("LWJGL " + Version.getVersion());
  }

  public void run() throws IOException {
    ecs.unregisterEntityListener(ecs.getSystem(LoadingSystem.class));
    ecs.removeSystem(BasicKeyBindings.class);
    ecs.removeSystem(LoadingSystem.class);

    // Shaders
    Shader chunkShader = shaderFromResources("shader/chunk.vert", "shader/chunk.frag");
    Shader waterShader = shaderFromResources("shader/chunk.vert", "shader/water.frag");
    Shader chunkAABBShader = shaderFromResources("shader/chunkAABB.vert", "shader/chunkAABB.frag");
    Shader hudShader = shaderFromResources("shader/basicHud.vert", "shader/basicHud.frag");
    Shader skyboxShader = shaderFromResources("shader/skybox.vert", "shader/skybox.frag");
    Shader screenShader = shaderFromResources("shader/screen.vert", "shader/screen.frag");

    // Textures
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
        "textures/cactus_top.png",
        "textures/cactus_bottom.png",
        "textures/gravel.png"
    };
    Path[] texturePaths = Arrays.stream(textureFiles)
        .map(tf -> {
          try {
            return ResourceLoader.locateResource(tf, VoxelApplication.class).toPath();
          } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
          }
        })
        .collect(Collectors.toList())
        .toArray(new Path[]{});
    Sampler2DArray arraySampler = samplerManager.sampler2DArray(16, 16, texturePaths);

    Sampler2D normalMap = samplerManager.sampler2D(ResourceLoader.locateResource("textures/normal_map.png", VoxelApplication.class).toPath());

    OpenSimplexNoise noise = new OpenSimplexNoise(LocalDateTime.now().getLong(ChronoField.NANO_OF_DAY));
    ChunkManager chunkManager = new ChunkManager(noise);
    VoxelCamera camera = new VoxelCamera(new WorldPosition(3.0f, 65.0f, 3.0f), 65.0f, new CrossHairTarget(chunkManager));
    ChunkInteractionManager interactionManager = new ChunkInteractionManager(chunkManager, camera);

    useCamera(camera);

    ChunkRenderer chunkRenderer = new ChunkRenderer(context, chunkShader, waterShader, chunkAABBShader);
    HudRenderer hudRenderer = new HudRenderer(context, hudShader);

    Skybox skybox = new Skybox(
        ResourceLoader.locateResource("skybox/right.png", VoxelApplication.class).toPath(),
        ResourceLoader.locateResource("skybox/left.png", VoxelApplication.class).toPath(),
        ResourceLoader.locateResource("skybox/top.png", VoxelApplication.class).toPath(),
        ResourceLoader.locateResource("skybox/bottom.png", VoxelApplication.class).toPath(),
        ResourceLoader.locateResource("skybox/back.png", VoxelApplication.class).toPath(),
        ResourceLoader.locateResource("skybox/front.png", VoxelApplication.class).toPath());

    chunkManager.initCamPos(camera);

    // Post-processing
    float[] screenQuadVertices = {
        // positions   // texCoords
        -1.0f,  1.0f,  0.0f, 1.0f,
        -1.0f, -1.0f,  0.0f, 0.0f,
        1.0f, -1.0f,  1.0f, 0.0f,

        -1.0f,  1.0f,  0.0f, 1.0f,
        1.0f, -1.0f,  1.0f, 0.0f,
        1.0f,  1.0f,  1.0f, 1.0f
    };

    int screenQuad = vaoManager
        .allocateVao()
        .bufferData(screenQuadVertices, GL_STATIC_DRAW)
        .enableAttribArray(0)
        .attribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0)
        .enableAttribArray(1)
        .attribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4L)
        .doFinal();

    // Framebuffer
    Framebuffer fbo = new Framebuffer(context.getWindowWidth(), context.getWindowHeight());
    Framebuffer fbo2 = new Framebuffer(context.getWindowWidth(), context.getWindowHeight());

    float lastPressed = 0.0f;
    while (!context.isCloseRequested()) {
      beginFrame();

      //chunkManager.generate(currentFrameTime, 0.0001f);
      chunkManager.update(camera);
      chunkManager.cancelChunks(camera);

      // Cross-hair voxel intersection
      camera.update(context.getDeltaTime(), !context.isMouseCursorToggled());

      if (!context.isMouseCursorToggled()) {
        interactionManager.highlightVoxel();
      }

      // Voxel placement
      if (!context.isMouseCursorToggled()) {
        if (MouseListener.buttonPressed(GLFW_MOUSE_BUTTON_2) && currentTime > lastPressed + 0.25f) {
          lastPressed = currentTime;
          interactionManager.placeVoxel(VoxelType.LEAVES);
        }

        if (MouseListener.buttonPressed(GLFW_MOUSE_BUTTON_1) && currentTime > lastPressed + 0.25f) {
          lastPressed = currentTime;
          interactionManager.removeVoxel();
        }
      }

      // Chunk mouse-cursor intersection
      WorldPosition intersectionPos = null;
      if (context.isMouseCursorToggled()) {
        intersectionPos = interactionManager.calculateMouseCursorIntersection(projection, context.getWindowWidth(), context.getWindowHeight());
      }

      // Render voxels
      chunkRenderer.render(chunkManager.getChunks(), intersectionPos, fbo, fbo2, arraySampler, normalMap);

      // Render skybox
      fbo.bind();
      skybox.render(skyboxShader, camera);

      // Post-processing
      screenShader.use();
      screenShader.setInt("normalVoxelSampler", 0);
      screenShader.setInt("transparentSampler", 1);
      screenShader.setInt("voxelDepthSampler", 2);
      screenShader.setInt("transparentDepthSampler", 3);

      glBindFramebuffer(GL_FRAMEBUFFER, 0);

      fbo.bindColorTexture(0);
      fbo2.bindColorTexture(1);
      fbo.bindDepthStencilTexture(2);
      fbo2.bindDepthStencilTexture(3);

      glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT);
      glDisable(GL_CULL_FACE);
      glDisable(GL_DEPTH_TEST);

      glBindVertexArray(screenQuad);
      glDrawArrays(GL_TRIANGLES, 0, screenQuadVertices.length);
      glBindVertexArray(0);

      // Render hud
      hudRenderer.render();

      // Keybindings
      if (KeyListener.keyPressed(GLFW_KEY_ESCAPE)) {
        context.requestClose();
      }

      if (KeyListener.keyPressed(GLFW_KEY_P)) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
      } else {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
      }

      if (KeyListener.keyPressed(GLFW_KEY_E) && currentTime > lastPressed + 0.25f) {
        lastPressed = currentTime;
        context.toggleMouseCursor();
      }

      endFrame();
    }

    fbo.cleanup();
    fbo2.cleanup();

    quit();
  }

  private Shader shaderFromResources(String vertex, String fragment) throws IOException {
    Path vertexFile = ResourceLoader.locateResource(vertex, VoxelApplication.class).toPath();
    Path fragmentFile = ResourceLoader.locateResource(fragment, VoxelApplication.class).toPath();
    return createShader(vertexFile, fragmentFile);
  }
}
