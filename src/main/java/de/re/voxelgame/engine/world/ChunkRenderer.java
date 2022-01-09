package de.re.voxelgame.engine.world;

import de.re.voxelgame.core.*;
import de.re.voxelgame.engine.voxel.Voxel;
import de.re.voxelgame.engine.voxel.VoxelFace;
import de.re.voxelgame.engine.voxel.VoxelType;
import de.re.voxelgame.engine.voxel.VoxelVertex;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;

import static de.re.voxelgame.engine.world.Chunk.CHUNK_SIZE;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
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
    Voxel border = new Voxel(VoxelType.MISSING, false,
        VoxelFace.FRONT,
        VoxelFace.BACK,
        VoxelFace.LEFT,
        VoxelFace.RIGHT,
        VoxelFace.TOP,
        VoxelFace.BOTTOM);
    List<VoxelVertex> borderVertices = border.getVertices();
    borderVertexData = new float[borderVertices.size()*3];
    for (int i = 0; i < borderVertexData.length; i+=3) {
      VoxelVertex v = borderVertices.get((int) Math.floor(i/3.0));
      borderVertexData[i] = v.getPosition().x;
      borderVertexData[i+1] = v.getPosition().y;
      borderVertexData[i+2] = v.getPosition().z;
    }

    borderVaoId = MemoryManager
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

  public void render(Collection<Chunk> chunks, Matrix4f view, Matrix4f projection,
                     WorldPosition mouseCursorIntersectionPos, int fbo, int fbo2,
                     Texture2dArray textureArray, Texture2d normalMap) {
    float currentTime = (float) glfwGetTime();

    // First rendering pass for transparent voxels
    glBindFramebuffer(GL_FRAMEBUFFER, fbo2);

    normalMap.bind(0);

    waterShader.use();
    waterShader.setMatrix4("iView", view);
    waterShader.setMatrix4("iProjection", projection);
    waterShader.setVec3("iColor", new Vector3f(0.0f, 0.0f, 0.5f));
    waterShader.setVec3("iLightDirection", new Vector3f(0.7f, 0.3f, 0.3f));
    waterShader.setFloat("iTime", currentTime);

    glClearColor(0.2f, 0.6f, 1.0f, 1.0f);
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
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);

    textureArray.bind(0);

    shader.use();
    shader.setMatrix4("iView", view);
    shader.setMatrix4("iProjection", projection);
    shader.setVec3("iColor", new Vector3f(0.0f, 0.0f, 0.5f));
    shader.setFloat("iTime", currentTime);

    AABBShader.use();
    AABBShader.setMatrix4("iView", view);
    AABBShader.setMatrix4("iProjection", projection);

    glClearColor(0.2f, 0.6f, 1.0f, 1.0f);
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
