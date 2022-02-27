package de.re.voxelgame.engine;

import de.re.voxelgame.core.*;
import de.re.voxelgame.core.ecs.entity.BasicEntity;
import de.re.voxelgame.core.ecs.entity.StaticEntity;
import de.re.voxelgame.core.ecs.system.HelloSystem;
import de.re.voxelgame.core.ecs.system.TestSystem;
import de.re.voxelgame.core.objects.Framebuffer;
import de.re.voxelgame.core.objects.sampler.Sampler2D;
import de.re.voxelgame.core.objects.sampler.Sampler2DArray;
import de.re.voxelgame.core.objects.shader.Shader;
import de.re.voxelgame.engine.gui.HudRenderer;
import de.re.voxelgame.engine.skybox.Skybox;
import de.re.voxelgame.engine.voxel.VoxelType;
import de.re.voxelgame.engine.world.*;
import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import org.joml.Vector3f;
import org.lwjgl.Version;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Application extends GLApplication {
  public static void main(String[] args) throws IOException, URISyntaxException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    new Application(1080, 720, "OpenGL").run();
  }

  public Application(int width, int height, String title) {
    super(width, height, title);
  }

  public void run() throws IOException, URISyntaxException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    System.out.println("LWJGL " + Version.getVersion());

    ecs.addSystem(HelloSystem.class);
    ecs.addSystem(TestSystem.class);

    ecs.addEntity(new BasicEntity());
    ecs.addEntity(new BasicEntity(new Vector3f(5.0f)));
    StaticEntity staticEntity = new StaticEntity();
    ecs.addEntity(new StaticEntity());
    ecs.addEntity(staticEntity);
    ecs.removeEntity(staticEntity);

    beginFrame();
    //loop();
    quit();
  }

  private void loop() throws IOException, URISyntaxException {
    // Shaders
    Shader chunkShader = createShader("shader/chunk.vert", "shader/chunk.frag");
    Shader waterShader = createShader("shader/chunk.vert", "shader/water.frag");
    Shader chunkAABBShader = createShader("shader/chunkAABB.vert", "shader/chunkAABB.frag");
    Shader hudShader = createShader("shader/basicHud.vert", "shader/basicHud.frag");
    Shader skyboxShader = createShader("shader/skybox.vert", "shader/skybox.frag");
    Shader screenShader = createShader("shader/screen.vert", "shader/screen.frag");

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
    Sampler2DArray arraySampler = samplerManager.sampler2DArray(16, 16, textureFiles);

    Sampler2D normalMap = samplerManager.sampler2D("textures/normal_map.png");

    OpenSimplexNoise noise = new OpenSimplexNoise(LocalDateTime.now().getLong(ChronoField.NANO_OF_DAY));
    ChunkManager chunkManager = new ChunkManager(noise);
    VoxelCamera camera = new VoxelCamera(new WorldPosition(3.0f, 65.0f, 3.0f), 65.0f, new CrossHairTarget(chunkManager));
    ChunkInteractionManager interactionManager = new ChunkInteractionManager(chunkManager, camera);

    useCamera(camera);

    ChunkRenderer chunkRenderer = new ChunkRenderer(context, chunkShader, waterShader, chunkAABBShader);
    HudRenderer hudRenderer = new HudRenderer(context, hudShader);

    Skybox skybox = new Skybox("skybox/right.png", "skybox/left.png", "skybox/top.png",
        "skybox/bottom.png", "skybox/back.png", "skybox/front.png");

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
  }
}
