package de.re.voxelgame.engine;

import de.re.voxelgame.core.*;
import de.re.voxelgame.core.MemoryManager;
import de.re.voxelgame.core.sampler.SamplerCube;
import de.re.voxelgame.core.sampler.Sampler2D;
import de.re.voxelgame.core.sampler.Sampler2DArray;
import de.re.voxelgame.core.sampler.Samplers;
import de.re.voxelgame.core.shader.Shader;
import de.re.voxelgame.core.shader.Shaders;
import de.re.voxelgame.engine.gui.HudRenderer;
import de.re.voxelgame.engine.voxel.VoxelType;
import de.re.voxelgame.engine.world.*;
import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.Version;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
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
    // Shaders
    Shader chunkShader = Shaders.create("shader/chunk.vert", "shader/chunk.frag");
    Shader waterShader = Shaders.create("shader/chunk.vert", "shader/water.frag");
    Shader chunkAABBShader = Shaders.create("shader/chunkAABB.vert", "shader/chunkAABB.frag");
    Shader hudShader = Shaders.create("shader/basicHud.vert", "shader/basicHud.frag");
    Shader skyboxShader = Shaders.create("shader/skybox.vert", "shader/skybox.frag");
    Shader screenShader = Shaders.create("shader/screen.vert", "shader/screen.frag");

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
    Sampler2DArray arraySampler = Samplers.sampler2DArray(16, 16, textureFiles);

    SamplerCube skybox = Samplers.samplerCube("skybox/right.png", "skybox/left.png", "skybox/top.png",
        "skybox/bottom.png", "skybox/back.png", "skybox/front.png");

    Sampler2D normalMap = Samplers.sampler2D("textures/normal_map.png");

    OpenSimplexNoise noise = new OpenSimplexNoise(LocalDateTime.now().getLong(ChronoField.NANO_OF_DAY));
    ChunkManager chunkManager = new ChunkManager(noise);
    VoxelCamera camera = new VoxelCamera(new WorldPosition(0.0f, 10.0f, 0.0f), new CrossHairTarget(chunkManager));
    ChunkInteractionManager interactionManager = new ChunkInteractionManager(chunkManager, camera);

    ChunkRenderer chunkRenderer = new ChunkRenderer(context, chunkShader, waterShader, chunkAABBShader);
    HudRenderer hudRenderer = new HudRenderer(context, hudShader);

    // Skybox geometry
    float[] skyboxVertices = {
        -1.0f,  1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f,  1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,

        -1.0f, -1.0f,  1.0f,
        -1.0f, -1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,
        -1.0f,  1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f,

        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,

        -1.0f, -1.0f,  1.0f,
        -1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f, -1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f,

        -1.0f,  1.0f, -1.0f,
        1.0f,  1.0f, -1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        -1.0f,  1.0f,  1.0f,
        -1.0f,  1.0f, -1.0f,

        -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f,  1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f,  1.0f,
        1.0f, -1.0f,  1.0f
    };

    int skyboxVao = MemoryManager
        .allocateVao()
        .bufferData(skyboxVertices, GL_STATIC_DRAW)
        .enableAttribArray(0)
        .attribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0L)
        .doFinal();

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

    int screenQuad = MemoryManager
        .allocateVao()
        .bufferData(screenQuadVertices, GL_STATIC_DRAW)
        .enableAttribArray(0)
        .attribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0)
        .enableAttribArray(1)
        .attribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4L)
        .doFinal();

    // Framebuffer 1
    int fbo = glGenFramebuffers();
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);

    int textureColorBuffer = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, textureColorBuffer);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, context.getWindowWidth(), context.getWindowHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glBindTexture(GL_TEXTURE_2D, 0);

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureColorBuffer, 0);

    int depthBuffer = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, depthBuffer);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, context.getWindowWidth(), context.getWindowHeight(), 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (ByteBuffer) null);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glBindTexture(GL_TEXTURE_2D, 0);

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthBuffer, 0);

    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
      System.err.println("ERROR::FRAMEBUFFER::NOT::COMPLETE");
      System.exit(-1);
    }
    glBindFramebuffer(GL_FRAMEBUFFER, 0);

    // Framebuffer 2
    int fbo2 = glGenFramebuffers();
    glBindFramebuffer(GL_FRAMEBUFFER, fbo2);

    int textureColorBuffer2 = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, textureColorBuffer2);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, context.getWindowWidth(), context.getWindowHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glBindTexture(GL_TEXTURE_2D, 0);

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureColorBuffer2, 0);

    int depthBuffer2 = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, depthBuffer2);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, context.getWindowWidth(), context.getWindowHeight(), 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (ByteBuffer) null);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glBindTexture(GL_TEXTURE_2D, 0);

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthBuffer2, 0);

    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
      System.err.println("ERROR::FRAMEBUFFER::NOT::COMPLETE");
      System.exit(-1);
    }
    glBindFramebuffer(GL_FRAMEBUFFER, 0);

    float lastPressed = 0.0f;
    while (!context.isCloseRequested()) {
      float currentFrameTime = (float) glfwGetTime();

      chunkManager.generate(currentFrameTime, 0.0001f);

      // Cross-hair voxel intersection
      camera.update(context.getDeltaTime(), !context.isMouseCursorToggled());

      if (!context.isMouseCursorToggled()) {
        interactionManager.highlightVoxel();
      }

      // Voxel placement
      if (!context.isMouseCursorToggled()) {
        if (MouseListener.buttonPressed(GLFW_MOUSE_BUTTON_2) && currentFrameTime > lastPressed + 0.25f) {
          lastPressed = currentFrameTime;
          interactionManager.placeVoxel(VoxelType.LEAVES);
        }

        if (MouseListener.buttonPressed(GLFW_MOUSE_BUTTON_1) && currentFrameTime > lastPressed + 0.25f) {
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

      // Render voxels
      waterShader.use();
      waterShader.setVec3("iCameraPos", camera.getPos());
      chunkRenderer.render(chunkManager.getChunks(), view, projection, intersectionPos, fbo, fbo2, arraySampler, normalMap);

      // Render skybox
      glBindFramebuffer(GL_FRAMEBUFFER, fbo);
      glDepthFunc(GL_LEQUAL);

      Matrix4f skyboxView = camera.getViewMatrix().get3x3(new Matrix3f()).get(new Matrix4f());
      skyboxShader.use();
      skyboxShader.setMatrix4("iView", skyboxView);
      skyboxShader.setMatrix4("iProjection", projection);

      glBindVertexArray(skyboxVao);
      skybox.bind(0);
      glDrawArrays(GL_TRIANGLES, 0, skyboxVertices.length);
      glBindVertexArray(0);
      glDepthFunc(GL_LESS);

      // Post-processing
      screenShader.use();
      screenShader.setInt("normalVoxelSampler", 0);
      screenShader.setInt("transparentSampler", 1);
      screenShader.setInt("voxelDepthSampler", 2);
      screenShader.setInt("transparentDepthSampler", 3);

      glBindFramebuffer(GL_FRAMEBUFFER, 0);

      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D, textureColorBuffer);
      glActiveTexture(GL_TEXTURE1);
      glBindTexture(GL_TEXTURE_2D, textureColorBuffer2);
      glActiveTexture(GL_TEXTURE2);
      glBindTexture(GL_TEXTURE_2D, depthBuffer);
      glActiveTexture(GL_TEXTURE3);
      glBindTexture(GL_TEXTURE_2D, depthBuffer2);

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

      if (KeyListener.keyPressed(GLFW_KEY_E) && currentFrameTime > lastPressed + 0.25f) {
        lastPressed = currentFrameTime;
        context.toggleMouseCursor();
      }

      camera.update(context.getDeltaTime(), !context.isMouseCursorToggled());
      context.update();
    }

    glDeleteTextures(textureColorBuffer);
    glDeleteTextures(textureColorBuffer2);
    glDeleteTextures(depthBuffer);
    glDeleteTextures(depthBuffer2);
    glDeleteFramebuffers(fbo);
    glDeleteFramebuffers(fbo2);
  }
}
