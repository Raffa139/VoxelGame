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
    // Shader
    ResourceLoader.Resource chunkVert = ResourceLoader.locateResource("shader/chunk.vert", Application.class);
    ResourceLoader.Resource chunkFrag = ResourceLoader.locateResource("shader/chunk.frag", Application.class);
    Shader chunkShader = new Shader(chunkVert.toPath(), chunkFrag.toPath());

    ResourceLoader.Resource waterVert = ResourceLoader.locateResource("shader/chunk.vert", Application.class);
    ResourceLoader.Resource waterFrag = ResourceLoader.locateResource("shader/water.frag", Application.class);
    Shader waterShader = new Shader(waterVert.toPath(), waterFrag.toPath());

    ResourceLoader.Resource aabbVert = ResourceLoader.locateResource("shader/chunkAABB.vert", Application.class);
    ResourceLoader.Resource aabbFrag = ResourceLoader.locateResource("shader/chunkAABB.frag", Application.class);
    Shader chunkAABBShader = new Shader(aabbVert.toPath(), aabbFrag.toPath());

    ResourceLoader.Resource hudVert = ResourceLoader.locateResource("shader/basicHud.vert", Application.class);
    ResourceLoader.Resource hudFrag = ResourceLoader.locateResource("shader/basicHud.frag", Application.class);
    Shader hudShader = new Shader(hudVert.toPath(), hudFrag.toPath());

    ResourceLoader.Resource screenVert = ResourceLoader.locateResource("shader/screen.vert", Application.class);
    ResourceLoader.Resource screenFrag = ResourceLoader.locateResource("shader/screen.frag", Application.class);
    Shader screenShader = new Shader(screenVert.toPath(), screenFrag.toPath());

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
    Texture2dArray texture2dArray = new Texture2dArray(16, 16, textureFiles);

    OpenSimplexNoise noise = new OpenSimplexNoise(LocalDateTime.now().getLong(ChronoField.NANO_OF_DAY));
    ChunkManager chunkManager = new ChunkManager(noise);
    VoxelCamera camera = new VoxelCamera(new WorldPosition(0.0f, 10.0f, 0.0f), new CrossHairTarget(chunkManager));
    ChunkInteractionManager interactionManager = new ChunkInteractionManager(chunkManager, camera);

    ChunkRenderer chunkRenderer = new ChunkRenderer(context, chunkShader, waterShader, chunkAABBShader);
    HudRenderer hudRenderer = new HudRenderer(context, hudShader);

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

      texture2dArray.bind(0);
      chunkRenderer.render(chunkManager.getChunks(), view, projection, intersectionPos, fbo, textureColorBuffer);

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

      glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT);
      glDisable(GL_CULL_FACE);
      glDisable(GL_DEPTH_TEST);

      glBindVertexArray(screenQuad);
      glDrawArrays(GL_TRIANGLES, 0, screenQuadVertices.length);
      glBindVertexArray(0);

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

    glDeleteTextures(textureColorBuffer);
    glDeleteTextures(textureColorBuffer2);
    glDeleteTextures(depthBuffer);
    glDeleteTextures(depthBuffer2);
    glDeleteFramebuffers(fbo);
    glDeleteFramebuffers(fbo2);
    texture2dArray.cleanup();
    chunkShader.terminate();
    chunkAABBShader.terminate();
    hudShader.terminate();
  }
}
