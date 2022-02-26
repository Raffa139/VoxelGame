package de.re.voxelgame.engine.world;

import de.re.voxelgame.core.*;
import de.re.voxelgame.core.GLVertexArrayManager;
import de.re.voxelgame.core.sampler.Sampler2D;
import de.re.voxelgame.core.sampler.Sampler2DArray;
import de.re.voxelgame.core.shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Collection;

import static de.re.voxelgame.engine.world.Chunk.CHUNK_SIZE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.*;

public class ChunkRenderer {
  private final GLContext context;

  private final Shader shader;
  private final Shader waterShader;
  private final Shader AABBShader;

  private static float[] borderVertexData;
  private static int borderVaoId;

  static {
    // Chunk border
    borderVertexData = new float[]{
        0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,

        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f,

        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 0.0f,
        1.0f, 0.0f, 0.0f,

        0.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,

        0.0f, 1.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        0.0f, 1.0f, 1.0f,
        0.0f, 1.0f, 0.0f,

        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 1.0f
    };

    borderVaoId = GLVertexArrayManager.get()
        .allocateVao()
        .bufferData(borderVertexData, GL_STATIC_DRAW)
        .enableAttribArray(0)
        .attribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0L)
        .doFinal();
  }

  public ChunkRenderer(GLContext context, Shader shader, Shader waterShader, Shader AABBShader) {
    this.context = context;
    this.shader = shader;
    this.waterShader = waterShader;
    this.AABBShader = AABBShader;
  }

  public void render(Collection<Chunk> chunks, WorldPosition mouseCursorIntersectionPos,
                     Framebuffer fbo, Framebuffer fbo2,
                     Sampler2DArray textureArray, Sampler2D normalMap) {
    // First rendering pass for transparent voxels
    fbo2.bind();

    normalMap.bind(0);

    waterShader.use();
    waterShader.setVec3("iColor", new Vector3f(0.0f, 0.0f, 0.5f));
    waterShader.setVec3("iLightDirection", new Vector3f(2.0f, 0.6f, -1.0f));

    glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glEnable(GL_CULL_FACE);
    glEnable(GL_DEPTH_TEST);

    for (Chunk chunk : chunks) {
      if (chunk.hasMesh()) {
        Matrix4f model = new Matrix4f();
        model.translate(chunk.getWorldPosition().getVector());

        waterShader.use();
        waterShader.setMatrix4("iModel", model);

        glBindVertexArray(chunk.getMesh().getVaoId());
        glDrawArrays(GL_TRIANGLES, 0, chunk.getMesh().getVertexCount());
        glBindVertexArray(0);
      }
    }

    // Second rendering pass for normal voxels
    fbo.bind();

    textureArray.bind(0);

    shader.use();
    shader.setVec3("iColor", new Vector3f(0.0f, 0.0f, 0.5f));

    glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glEnable(GL_CULL_FACE);
    glEnable(GL_DEPTH_TEST);

    for (Chunk chunk : chunks) {
      if (chunk.hasMesh()) {
        Matrix4f model = new Matrix4f();
        model.translate(chunk.getWorldPosition().getVector());

        shader.use();
        shader.setMatrix4("iModel", model);

        glBindVertexArray(chunk.getMesh().getVaoId());
        glDrawArrays(GL_TRIANGLES, 0, chunk.getMesh().getVertexCount());
        glBindVertexArray(0);
      }

      // Draw chunk border
      if (context.isMouseCursorToggled()) {
        Matrix4f model = new Matrix4f();
        model.translate(chunk.getWorldPosition().getVector()).scale(CHUNK_SIZE);

        AABBShader.use();
        AABBShader.setMatrix4("iModel", model);
        if (chunk.getRelativePosition().equals(mouseCursorIntersectionPos) && context.isMouseCursorToggled()) {
          AABBShader.setVec3("iColor", new Vector3f(1.0f, 0.0f, 0.0f));
        } else {
          AABBShader.setVec3("iColor", new Vector3f(1.0f, 1.0f, 1.0f));
        }

        glBindVertexArray(borderVaoId);
        glDrawArrays(GL_LINES, 0, borderVertexData.length);
        glBindVertexArray(0);
      }
    }
  }
}
