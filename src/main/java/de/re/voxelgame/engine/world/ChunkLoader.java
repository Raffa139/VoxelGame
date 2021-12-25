package de.re.voxelgame.engine.world;

import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import de.re.voxelgame.engine.voxel.Voxel;
import de.re.voxelgame.engine.voxel.VoxelFace;
import de.re.voxelgame.engine.voxel.VoxelVertex;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.re.voxelgame.engine.world.Chunk.CHUNK_SIZE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class ChunkLoader {
  private static final Map<Integer, Integer> VERTEX_TEXTURE_INDICES = Map.of(
      0, 0,
      1, 1,
      2, 2,
      3, 2,
      4, 3,
      5, 0
  );

  private ChunkLoader() {
  }

  public static Chunk loadChunkNoise(Vector3f position, OpenSimplexNoise noise) {
    List<VoxelVertex> translatedVertices = new ArrayList<>();

    for (int x = 0; x < CHUNK_SIZE; x++) {
      for (int z = 0; z < CHUNK_SIZE; z++) {
        float tx = x+(position.x*CHUNK_SIZE);
        float tz = z+(position.z*CHUNK_SIZE);

        int height = noise.voxelNoise2d(tx, tz);
        int heightN = noise.voxelNoise2d(tx, tz-1);
        int heightS = noise.voxelNoise2d(tx, tz+1);
        int heightE = noise.voxelNoise2d(tx+1, tz);
        int heightW = noise.voxelNoise2d(tx-1, tz);

        for (int y = 0; y < CHUNK_SIZE; y++) {
          int ty = (int) (y + position.y * CHUNK_SIZE);
          Voxel voxel = new Voxel(6);

          // Water level = 50
          if (ty > 50 && ty <= 56) {
            // Sand
            voxel = new Voxel(2);
          } else if (ty > 56 && ty <= 85) {
            // Grass
            voxel = new Voxel(4);
          } else if (ty > 85 && ty <= 90) {
            // Dirt
            voxel = new Voxel(1);
          } else if (ty > 90) {
            // Stone
            voxel = new Voxel(0);
          }

          if (ty == height || (ty == 50 && ty > height)) {
            voxel.join(VoxelFace.TOP);
          }
          if (ty > heightE && ty <= height) {
            voxel.join(0.8f, VoxelFace.RIGHT);
          }
          if (ty > heightW && ty <= height) {
            voxel.join(0.8f, VoxelFace.LEFT);
          }
          if (ty > heightN && ty <= height) {
            voxel.join(0.6f, VoxelFace.BACK);
          }
          if (ty > heightS && ty <= height) {
            voxel.join(0.6f, VoxelFace.FRONT);
          }

          if (voxel.hasVertices()) {
            translatedVertices.addAll(voxel.translate(x, y, z).getVertices());
          }
        }
      }
    }

    return storeAndReturnChunk(translatedVertices, position);
  }

  private static Chunk storeAndReturnChunk(List<VoxelVertex> translatedVertices, Vector3f position) {
    if (translatedVertices.size() == 0) {
      return new Chunk(position, -1, -1);
    }

    int[] vertexData = new int[translatedVertices.size()];
    for (int i = 0; i < translatedVertices.size(); i++) {
      VoxelVertex v = translatedVertices.get(i);

      // Push position bits according to vertex data schematics
      vertexData[i] = (int) v.getPosition().x << 26;
      vertexData[i] = vertexData[i] | (int) v.getPosition().y << 20;
      vertexData[i] = vertexData[i] | (int) v.getPosition().z << 14;

      // Push index of texture coordinate according to vertex data schematics
      int textureCoordIndex = (int) Math.floor(i % 6.0);
      vertexData[i] = vertexData[i] | VERTEX_TEXTURE_INDICES.get(textureCoordIndex) << 12;

      // Push texture tile id (voxel-type) according to vertex data schematics
      vertexData[i] = vertexData[i] | v.getType() << 3;

      // Push light level according to vertex data schematics
      vertexData[i] = vertexData[i] | (int) (v.getLightLevel() * 5);
    }

    int vertexCount = vertexData.length;

    int vaoId = glGenVertexArrays();
    glBindVertexArray(vaoId);

    int vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);

    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 1, GL_FLOAT, false, 4, 0L);

    glBindVertexArray(0);

    return new Chunk(position, vaoId, vertexCount);
  }
}
