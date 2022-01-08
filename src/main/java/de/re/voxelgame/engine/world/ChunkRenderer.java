package de.re.voxelgame.engine.world;

import de.re.voxelgame.core.GLContext;
import de.re.voxelgame.core.MemoryManager;
import de.re.voxelgame.core.Shader;
import de.re.voxelgame.engine.voxel.Voxel;
import de.re.voxelgame.engine.voxel.VoxelFace;
import de.re.voxelgame.engine.voxel.VoxelType;
import de.re.voxelgame.engine.voxel.VoxelVertex;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;

import static de.re.voxelgame.engine.world.Chunk.CHUNK_SIZE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ChunkRenderer {
  private final GLContext context;

  private final Shader shader;
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

  public ChunkRenderer(GLContext context, Shader shader, Shader AABBShader) {
    this.context = context;
    this.shader = shader;
    this.AABBShader = AABBShader;
  }

  public void render(Collection<Chunk> chunks, Matrix4f view, Matrix4f projection, WorldPosition mouseCursorIntersectionPos) {
    shader.use();
    shader.setMatrix4("iView", view);
    shader.setMatrix4("iProjection", projection);
    shader.setVec3("iColor", new Vector3f(0.0f, 0.0f, 0.5f));

    AABBShader.use();
    AABBShader.setMatrix4("iView", view);
    AABBShader.setMatrix4("iProjection", projection);

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
