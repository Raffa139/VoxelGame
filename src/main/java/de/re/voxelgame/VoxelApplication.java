package de.re.voxelgame;

import de.ren.ecs.engine.GLApplication;
import de.ren.ecs.engine.ecs.system.LoadingSystem;
import de.ren.ecs.engine.objects.Framebuffer;
import de.ren.ecs.engine.objects.sampler.Sampler2D;
import de.ren.ecs.engine.objects.sampler.Sampler2DArray;
import de.ren.ecs.engine.objects.shader.Shader;
import de.ren.ecs.engine.util.ResourceLoader;
import de.re.voxelgame.camera.VoxelCamera;
import de.re.voxelgame.camera.VoxelCameraSystem;
import de.re.voxelgame.font.FontRenderer;
import de.re.voxelgame.gui.HudRenderer;
import de.re.voxelgame.skybox.Skybox;
import de.re.voxelgame.skybox.SkyboxRenderer;
import de.re.voxelgame.util.DebugSystem;
import de.re.voxelgame.world.WorldPosition;
import de.re.voxelgame.world.chunk.ChunkInteractionSystem;
import de.re.voxelgame.world.chunk.ChunkLoadingSystem;
import de.re.voxelgame.world.chunk.ChunkRenderer;
import de.re.voxelgame.world.chunk.ChunkSystem;
import org.joml.Vector3f;
import org.lwjgl.Version;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class VoxelApplication extends GLApplication {
  public VoxelApplication(int width, int height, String title) {
    super(width, height, title);
    System.out.println("LWJGL " + Version.getVersion());
  }

  public void run() throws IOException {
    ecs.unregisterEntityListener(ecs.getSystem(LoadingSystem.class));
    ecs.removeSystem(LoadingSystem.class);

    // Shaders
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

    Sampler2D fontAtlas = samplerManager.sampler2D(ResourceLoader.locateResource("font/calibri/calibri.png", VoxelApplication.class).toPath());

    VoxelCamera camera = new VoxelCamera(new WorldPosition(3.0f, 65.0f, 3.0f), 65.0f);
    useCamera(camera);

    Skybox skybox = new Skybox(
        ResourceLoader.locateResource("skybox/right.png", VoxelApplication.class).toPath(),
        ResourceLoader.locateResource("skybox/left.png", VoxelApplication.class).toPath(),
        ResourceLoader.locateResource("skybox/top.png", VoxelApplication.class).toPath(),
        ResourceLoader.locateResource("skybox/bottom.png", VoxelApplication.class).toPath(),
        ResourceLoader.locateResource("skybox/back.png", VoxelApplication.class).toPath(),
        ResourceLoader.locateResource("skybox/front.png", VoxelApplication.class).toPath());

    // Post-processing
    float[] screenQuadVertices = {
        // positions   // texCoords
        -1.0f, 1.0f, 0.0f, 1.0f,
        -1.0f, -1.0f, 0.0f, 0.0f,
        1.0f, -1.0f, 1.0f, 0.0f,

        -1.0f, 1.0f, 0.0f, 1.0f,
        1.0f, -1.0f, 1.0f, 0.0f,
        1.0f, 1.0f, 1.0f, 1.0f
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
    Framebuffer normalVoxelBuffer = new Framebuffer(context.getWindowWidth(), context.getWindowHeight());
    Framebuffer transparentVoxelBuffer = new Framebuffer(context.getWindowWidth(), context.getWindowHeight());

    ecs.addSystem(VoxelCameraSystem.class);
    ecs.getSystem(VoxelCameraSystem.class).setCamera(camera);
    ecs.addSystem(ChunkLoadingSystem.class);
    ecs.addSystem(ChunkSystem.class);
    ecs.addSystem(ChunkInteractionSystem.class);
    ecs.addSystem(DebugSystem.class);

    ChunkRenderer chunkRenderer = new ChunkRenderer(this);
    HudRenderer hudRenderer = new HudRenderer(this);
    SkyboxRenderer skyboxRenderer = new SkyboxRenderer(this);
    FontRenderer fontRenderer = new FontRenderer(this, ResourceLoader.locateResource("font/calibri/calibri.fnt", VoxelApplication.class).toPath(), fontAtlas);

    while (glApplicationIsRunning()) {
      beginFrame();

      // Render voxels
      chunkRenderer.render(normalVoxelBuffer, transparentVoxelBuffer, arraySampler, normalMap);

      // Render skybox
      normalVoxelBuffer.bind();
      skyboxRenderer.render(skybox, camera);

      // Post-processing
      screenShader.use();
      screenShader.setInt("normalVoxelSampler", 0);
      screenShader.setInt("transparentSampler", 1);
      screenShader.setInt("voxelDepthSampler", 2);
      screenShader.setInt("transparentDepthSampler", 3);

      glBindFramebuffer(GL_FRAMEBUFFER, 0);

      normalVoxelBuffer.bindColorTexture(0);
      transparentVoxelBuffer.bindColorTexture(1);
      normalVoxelBuffer.bindDepthStencilTexture(2);
      transparentVoxelBuffer.bindDepthStencilTexture(3);

      glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT);
      glDisable(GL_DEPTH_TEST);

      glBindVertexArray(screenQuad);
      glDrawArrays(GL_TRIANGLES, 0, screenQuadVertices.length);
      glBindVertexArray(0);

      // Render hud
      hudRenderer.render();

      // Render text
      fontRenderer.renderText("Hello World!", 0, context.getWindowHeight() - 35.0f, 0.5f, new Vector3f(0.1f, 0.65f, 0.75f));

      endFrame();
    }

    normalVoxelBuffer.cleanup();
    transparentVoxelBuffer.cleanup();

    quit();
  }

  public Shader shaderFromResources(String vertex, String fragment) throws IOException {
    Path vertexFile = ResourceLoader.locateResource(vertex, VoxelApplication.class).toPath();
    Path fragmentFile = ResourceLoader.locateResource(fragment, VoxelApplication.class).toPath();
    return createShader(vertexFile, fragmentFile);
  }
}
